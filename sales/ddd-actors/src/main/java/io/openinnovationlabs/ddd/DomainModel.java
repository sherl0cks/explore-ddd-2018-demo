package io.openinnovationlabs.ddd;

import io.openinnovationlabs.ddd.eventstore.AbstractEventStore;
import io.openinnovationlabs.ddd.eventstore.AppendEventsCommand;
import io.openinnovationlabs.ddd.eventstore.LoadEventsCommand;
import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * TODO Something could be done here with service proxies
 * TODO perhaps this needs an interface
 * <p>
 * In short, the domain model becomes a simple abstraction over the Vert.x event bus which assumes a verticle per
 * aggregate instance, which makes verticles look more actor like as they all become uniquely addressable a la the
 * actor mailbox
 * <p>
 * This has the handy (but truly unintentional) implication of removing the need to know vert.x event bus primitives, at
 * least while in the domain. Adapters e.g. http / db / messaging will still need some, but minimal vert.x knowledge.
 */
public class DomainModel {

    public static final String COMMAND_ADDRESS_FORMAT = "v1-%s-%s-Commands";
    public static final String EVENTS_ADDRESS_FORMAT = "v1-%s-%s-Events";

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainModel.class);
    private Vertx vertx;


    public DomainModel(Vertx vertx) {
        this.vertx = vertx;
    }

    public Future<CommandProcessingResponse> issueCommand(Command command) {
        Future<CommandProcessingResponse> future = Future.future();
        sendCommand(command).setHandler(ar -> {
            if (ar.failed()) {
                if (ar.cause() instanceof ReplyException) {
                    if (((ReplyException) ar.cause()).failureType().equals(ReplyFailure.NO_HANDLERS)) {
                        LOGGER.debug(String.format("%s :: No handlers, deploying verticle", command.aggregateIdentity
                                ()));


                        loadEvents(command.aggregateIdentity())
                                .compose(response -> deploy(command, response)
                                        .compose(v -> sendReplayEventsCommand(command.aggregateIdentity(), response))
                                        .compose(v -> sendCommand(command))).setHandler(future.completer());
                    }
                } else {
                    LOGGER.error(ar.cause().getLocalizedMessage());
                    future.fail(ar.cause());
                }
            } else{
                future.complete(ar.result());
            }
        });
        return future;
    }


    private Future<CommandProcessingResponse> sendCommand(Command command) {
        Future<CommandProcessingResponse> future = Future.future();
        DeliveryOptions options = new DeliveryOptions().addHeader("commandClassname", command.getClass().getName());
        JsonObject jsonObject = JsonObject.mapFrom(command);
        LOGGER.trace(jsonObject.toString());
        vertx.eventBus().send(vertxAddressFor(command), jsonObject, options, ar -> {
            if (ar.succeeded()){
                CommandProcessingResponse response = ((JsonObject) ar.result().body()).mapTo
                        (CommandProcessingResponse.class);
                if (response.succeeded()){
                    future.complete(response);
                } else {
                    future.fail(response.exception);
                }
            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }

    private Future<String> deploy(Command command, CommandProcessingResponse response) {
        Future<String> future = Future.future();
        JsonObject config = new JsonObject().put("id", command.aggregateIdentity().id);
        if (response.events.size() > 0) {
            config.put("replay", true);
        }
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(command.aggregateIdentity().type, options, ar -> {
            if (ar.succeeded()) {
                LOGGER.info(String.format("%s :: Verticle deployment succeeded", command.aggregateIdentity()));
                future.complete(ar.result());
            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }

    private Future<CommandProcessingResponse> sendReplayEventsCommand(AggregateIdentity aggregateIdentity, CommandProcessingResponse response) {
        Future<CommandProcessingResponse> future = Future.future();
        if (response.events.size() == 0) {
            future.complete();
        } else {
            ReplayEventsCommand replayEventsCommand = new ReplayEventsCommand(aggregateIdentity, response.events);
            sendCommand(replayEventsCommand).setHandler( future.completer() );
        }
        return future;
    }

    // TODO does publish belong here? should publish be supported via Kafka?
    @Deprecated
    public void publishEvent(Event event) {
        DeliveryOptions options = new DeliveryOptions().addHeader("eventClassName", event.getClass().getName());
        vertx.eventBus().publish(vertxAddressFor(event), JsonObject.mapFrom(event), options);
    }

    // TODO does publish belong here? should publish be supported via Kafka?
    @Deprecated
    public Future<Void> publishEvents(List<Event> events) {
        Future<Void> future = Future.future();
        for (Event e : events) {
            publishEvent(e);
        }
        future.complete();
        return future;
    }

    public Future<Boolean> persistEvents(List<Event> events) {
        Future<Boolean> future = Future.future();
        AppendEventsCommand command = new AppendEventsCommand(events);
        vertx.eventBus().send(AbstractEventStore.APPEND_ADDRESS, JsonObject.mapFrom(command), ar -> {
            if (ar.succeeded()) {
                if ( ((String) ar.result().body()).equals("success")){
                    future.complete();
                } else{
                    future.fail(((String) ar.result().body()));
                }

            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }

    // TODO does publish belong here? should publish be supported via Kafka?
    @Deprecated
    public Future<Void> persistAndPublishEvents(List<Event> events) {
        Future<Void> future = Future.future();
        persistEvents(events).compose(v -> publishEvents(events)).setHandler(future.completer());
        return future;
    }

    public Future<CommandProcessingResponse> loadEvents(AggregateIdentity aggregateIdentity) {
        Future<CommandProcessingResponse> future = Future.future();
        LoadEventsCommand command = new LoadEventsCommand(aggregateIdentity);
        vertx.eventBus().send(AbstractEventStore.LOAD_EVENT_ADDRESS, JsonObject.mapFrom(command), ar -> {
            if (ar.succeeded()) {
                CommandProcessingResponse response = null;
                try {
                    response = ((JsonObject) ar.result().body()).mapTo(CommandProcessingResponse.class);
                } catch (Exception e) {
                    LOGGER.error(ar.result().body().toString());
                    LOGGER.error(e.getLocalizedMessage());
                }
                LOGGER.debug(String.format("%s :: Loaded %d events from event store", aggregateIdentity, response.events
                        .size()));
                future.complete(response);
            } else {
                LOGGER.error(String.format("Failed to load events: %s", ar.cause().getLocalizedMessage()));
                future.fail(ar.cause());
            }
        });
        return future;
    }


    public MessageConsumer<JsonObject> subscribeToEventStream(AggregateIdentity identity) {
        return vertx.eventBus().consumer(String.format(EVENTS_ADDRESS_FORMAT, identity.type.getSimpleName(),
                identity.id));
    }

    public void close(Handler<AsyncResult<Void>> completionHandler) {
        vertx.close(completionHandler);
    }

    public String vertxAddressFor(Command command) {
        return String.format(COMMAND_ADDRESS_FORMAT,
                command.aggregateIdentity().type.getSimpleName(),
                command.aggregateIdentity().id
        );
    }

    public String vertxAddressFor(Event event) {
        return String.format(EVENTS_ADDRESS_FORMAT,
                event.aggregateIdentity().type.getSimpleName(),
                event.aggregateIdentity().id
        );
    }

}