package io.openinnovationlabs.domain;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The idea here is to make Verticles more actor like, where every Aggregate instance has it's mailbox which follows
 * a simple convention to remove boiler plate. This is inspired by https://vaughnvernon.co/?p=780. In this case, the
 * mailbox accepts Commands, not any arbitrary message.
 * <p>
 * // TODO how are event subscriptions between Aggregates handled?
 * <p>
 * Reflection modelled after https://github.com/eventuate-clients/eventuate-client-java/blob/master/eventuate-client-java/src/main/java/io/eventuate/ReflectiveMutableCommandProcessingAggregate.java
 */
public abstract class Aggregate extends AbstractVerticle {

    private static Logger LOGGER = LoggerFactory.getLogger(Aggregate.class);
    protected long eventIndex = 0;
    protected Boolean replaying;
    private String id;
    private DomainModel domainModel; // TODO perhaps this should be a singleton?

    /**
     * idea here is give subclasses common initialization but with an extension point in start(). Subclasses
     * shouldn't be doing resource intensive stuff in start()
     */
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        String id = context.config().getString("id");
        if (id == null || id.isEmpty()) {
            startFuture.fail("id must be set in DeploymentOptions");
        } else {
            this.id = id;
        }

        if (context.config().containsKey("replay")) {
            replaying = context.config().getBoolean("replay");
        } else {
            replaying = false;
        }

        this.domainModel = new DomainModel(vertx);


        initializeMessageConsumers();

        start();
        startFuture.complete();
    }

    private void initializeMessageConsumers() {
        String commandAddress = String.format("%s-%s-Commands", this.getClass().getSimpleName(), this.id);
        MessageConsumer<JsonObject> commandProcessor = vertx.eventBus().consumer(commandAddress);
        commandProcessor.handler(message -> handleCommandMessage(message));
    }

    /**
     * the order of operations in this method is handled differently by Akka than what Vaughn Vernon suggests in
     * https://vaughnvernon.co/?page_id=168#iddd
     * https://doc.akka.io/docs/akka/2.5/persistence.html
     * <p>
     * the current approach where persistence happens after applying events suggests that non transactional,
     * synchronous interaction with remote services (e.g. http), which is owned by this bounded context should be
     * implemented with adapters that subscribe to the vertx event stream and thus would receive the events only
     * after they were successfully persisted. external bounded contexts will follow the event stream in kafka
     * <p>
     * TODO what does eventuate do?
     * TODO what is the right way?
     */
    private void handleCommandMessage(Message<JsonObject> message) {
        Object command = mapToCommandObject(message);
        final List<Event> events = processCommand(command);
        applyEvents(events);
        if (events.get(events.size() - 1) instanceof EventsReplayed) {
            // do not persist or publish events, we're just replaying the event stream
            // the event applier handles mutating state
        } else {
            domainModel.persistAndPublishEvents(events);
        }
        message.reply("complete"); // is this the right place to ACK?
    }

    private Object mapToCommandObject(Message<JsonObject> message) {
        String commandClassname = message.headers().get("commandClassname");
        Class<?> commandClass = null;
        try {
            commandClass = Class.forName(commandClassname);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        return message.body().mapTo(commandClass);
    }

    // TODO this ought to have more robust error handling
    private List<Event> processCommand(Object command) {
        List<Event> events = null;

        try {
            events = (List<Event>) getClass().getMethod("process", command.getClass()).invoke(this, command);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
        return events;
    }

    // TODO this ought to may more robust error handling
    private void applyEvents(List<Event> events) {
        for (Event e : events) {
            try {
                getClass().getMethod("apply", e.getClass()).invoke(this, e);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }
    }

    public List<Event> process(ReplayEventsCommand command) {
        if (replaying) {
            List<Event> events = new ArrayList<>(command.events);
            events.add(new EventsReplayed(Instant.now().toString(), command.aggregateIdentity));
            return events;
        } else {
            // TODO better exception handling here
            throw new IllegalStateException("aggregate must be in replay mode to accept replay command");
        }
    }

    public void apply(EventsReplayed event){
        this.replaying = false;
        LOGGER.info(String.format("Replay complete for %s",event.aggregateIdentity));
    }

}
