package io.openinnovationlabs.domain;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 *
 * The idea here is to make Verticles more actor like, where every Aggregate instance has it's mailbox which follows
 * a simple convention to remove boiler plate. This is inspired by https://vaughnvernon.co/?p=780. In this case, the
 * mailbox accepts Commands, not any arbitrary message.
 *
 * // TODO how are event subscriptions between Aggregates handled?
 *
 * Reflection modelled after https://github.com/eventuate-clients/eventuate-client-java/blob/master/eventuate-client-java/src/main/java/io/eventuate/ReflectiveMutableCommandProcessingAggregate.java
 */
public abstract class Aggregate extends AbstractVerticle {

    private static Logger LOGGER = LoggerFactory.getLogger(Aggregate.class);
    private String id;
    private DomainModel domainModel; // perhaps this should be a singleton?

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

        domainModel = new DomainModel(vertx);
        initializeMessageConsumers();

        startFuture.complete();
        start();
    }

    private void initializeMessageConsumers() {
        String commandAddress = String.format("%s-%s-Commands", this.getClass().getSimpleName(), this.id);
        LOGGER.info( String.format("subscribing to command address: %s", commandAddress));
        MessageConsumer<JsonObject> commandProcessor = vertx.eventBus().consumer(commandAddress);
        commandProcessor.handler(message -> handleCommandMessage(message));
    }

    private void handleCommandMessage(Message<JsonObject> message) {
        Object command = mapToCommandObject(message);
        List<Event> events = processCommand(command);
        applyEvents(events);
        publishEvents(events);
    }

    private Object mapToCommandObject(Message<JsonObject> message){
        String commandClassname = message.headers().get("commandClassname");
        Class<?> commandClass = null;
        try {
            commandClass = Class.forName(commandClassname);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        return message.body().mapTo(commandClass);
    }

    private List<Event> processCommand(Object command){
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

    private void applyEvents(List<Event> events){
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

    private void publishEvents(List<Event> events){
        DomainModel domainModel = new DomainModel(vertx);
        for (Event e : events) {
            domainModel.publishEvent(e);
        }

    }

}
