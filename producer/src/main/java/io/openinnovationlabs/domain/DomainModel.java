package io.openinnovationlabs.domain;

import io.openinnovationlabs.domain.eventstore.AppendEventsCommand;
import io.openinnovationlabs.domain.eventstore.LoadEventResponse;
import io.openinnovationlabs.domain.eventstore.LoadEventsCommand;
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

    public void issueCommand(Command command) {
        DeliveryOptions options = new DeliveryOptions().addHeader("commandClassname", command.getClass().getName());
        vertx.eventBus().send(vertxAddressFor(command), JsonObject.mapFrom(command), options, ar -> {
            if (ar.failed()) {
                if (ar.cause() instanceof ReplyException) {
                    if (((ReplyException) ar.cause()).failureType().equals(ReplyFailure.NO_HANDLERS)) {
                        LOGGER.info(String.format("%s :: no handlers, deploying verticle", command.aggregateIdentity()));


                        loadEvents(command.aggregateIdentity())
                                .compose(response -> deploy(command, response)
                                        .compose(v -> sendReplayEventsCommand(command.aggregateIdentity(), response))
                                        .compose(v -> sendCommand(command)));
                    }
                } else {
                    LOGGER.error(ar.cause().getLocalizedMessage());
                }
            }
        });
    }

    // TODO perhaps merge with other send command class?
    public Future sendCommand(Command command) {
        Future future = Future.future();
        DeliveryOptions options = new DeliveryOptions().addHeader("commandClassname", command.getClass().getName());
        vertx.eventBus().send(vertxAddressFor(command), JsonObject.mapFrom(command), options, future.completer());
        return future;
    }

    private Future<String> deploy(Command command, LoadEventResponse response) {
        Future<String> future = Future.future();
        JsonObject config = new JsonObject().put("id", command.aggregateIdentity().id);
        if (response.events.size() > 0) {
            config.put("replay", true);
        }
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(command.aggregateIdentity().type, options, ar -> {
            if (ar.succeeded()) {
                LOGGER.info(String.format("%s :: deployment succeeded", command.aggregateIdentity()));
                future.complete(ar.result());
            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }

    private Future sendReplayEventsCommand(AggregateIdentity aggregateIdentity, LoadEventResponse response) {
        Future future = Future.future();
        if (response.events.size() == 0) {
            future.complete();
        } else {
            ReplayEventsCommand replayEventsCommand = new ReplayEventsCommand(aggregateIdentity, response.events);
            DeliveryOptions options = new DeliveryOptions().addHeader("commandClassname", replayEventsCommand.getClass().getName());
            vertx.eventBus().send(vertxAddressFor(replayEventsCommand), JsonObject.mapFrom(replayEventsCommand), options,
                    future.completer());
        }
        return future;
    }

    public void publishEvent(Event event) {
        DeliveryOptions options = new DeliveryOptions().addHeader("eventClassName", event.getClass().getName());
        vertx.eventBus().publish(vertxAddressFor(event), JsonObject.mapFrom(event), options);
    }

    // TODO perhaps this should be done another way, given publish is a sync API
    public Future publishEvents(List<Event> events) {
        Future future = Future.future();
        for (Event e : events) {
            publishEvent(e);
        }
        future.complete();
        return future;
    }

    public Future persistEvents(List<Event> events) {
        Future future = Future.future();
        AppendEventsCommand command = new AppendEventsCommand(events);
        vertx.eventBus().send("EventStore-Append", JsonObject.mapFrom(command), ar -> {
            if (ar.succeeded()) {
                LOGGER.info("events persisted!!!");
                future.complete();
            } else {
                LOGGER.error("no persist");
                future.fail(ar.cause());
            }
        });
        return future;
    }

    public Future persistAndPublishEvents(List<Event> events) {
        Future future = Future.future();
        persistEvents(events).compose(v -> publishEvents(events)).setHandler(future.completer());
        return future;
    }

    public Future<LoadEventResponse> loadEvents(AggregateIdentity aggregateIdentity) {
        Future<LoadEventResponse> future = Future.future();
        LoadEventsCommand command = new LoadEventsCommand(aggregateIdentity);
        vertx.eventBus().send("EventStore-LoadEvents", JsonObject.mapFrom(command), ar -> {
            if (ar.succeeded()) {
                LoadEventResponse response = null;
                try {
                    response = ((JsonObject) ar.result().body()).mapTo(LoadEventResponse.class);
                } catch (Exception e) {
                    LOGGER.error(ar.result().body().toString());
                    LOGGER.error(e.getLocalizedMessage());
                }
                LOGGER.info(String.format("%s :: loaded %d events from event store", aggregateIdentity, response.events.size()));
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