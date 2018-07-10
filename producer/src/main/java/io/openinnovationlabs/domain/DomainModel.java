package io.openinnovationlabs.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
 * TODO perhaps one would like to provide async handlers to the methods
 * <p>
 * In short, the domain model becomes a simple abstraction over the Vert.x event bus which assumes a verticle per
 * aggregate instance, which makes verticles look more actor like as they all become uniquely addressable a la the
 * actor mailbox
 * <p>
 * This has the handy (but truly unintentional) implication of removing the need to know vert.x event bus primitives, at
 * least while in the domain. Adapters e.g. http / db / messaging will still need some, but minimal vert.x knowledge.
 */
public class DomainModel {

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
                        LOGGER.info(ar.cause().getLocalizedMessage());

                        // what happens if more than one command is issued before the verticle comes up?
                        // dead letter here ?
                        deployAggregrateVerticle(command);
                    }
                } else {
                    LOGGER.error(ar.cause().getLocalizedMessage());
                }
            }
        });
    }

    /**
     * TODO handle rehydration from event log
     * use Akka as the example https://doc.akka.io/docs/akka/current/persistence.html
     */
    private void deployAggregrateVerticle(Command command) {
        JsonObject config = new JsonObject().put("id", command.aggregateIdentity().id);
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(command.aggregateIdentity().type, options, ar -> {
            if (ar.succeeded()) {
                LOGGER.info(String.format("deployment of %s succeeded", command.aggregateIdentity()));

                // when verticle is deployed, send a create message to it
                issueCommand(command);
            } else {
                LOGGER.error(String.format("deployment of %s failed", command.aggregateIdentity()));
            }
        });
    }

    public void publishEvent(Event event) {
        DeliveryOptions options = new DeliveryOptions().addHeader("eventClassName", event.getClass().getName());
        vertx.eventBus().publish(vertxAddressFor(event), JsonObject.mapFrom(event), options);
    }

    public void publishEvents(List<Event> events) {
        for (Event e : events) {
            publishEvent(e);
        }
    }

    public void persistEvents(List<Event> events) {
        AppendEventsCommand command = new AppendEventsCommand(events);
        vertx.eventBus().send("EventStore-Append", JsonObject.mapFrom(command));
    }

    public void persistAndPublishEvents(List<Event> events) {
        // TODO persist
        persistEvents(events);
        publishEvents(events);
    }

    public void loadEvents(AggregateIdentity aggregateIdentity, Handler<AsyncResult<LoadEventResponse>> handler) {
        LoadEventsCommand command = new LoadEventsCommand(aggregateIdentity);
        vertx.eventBus().send("EventStore-LoadEvents", JsonObject.mapFrom(command), ar -> {
            if (ar.succeeded()) {
                LoadEventResponse response = null;
                try {
                    response = ((JsonObject)ar.result().body()).mapTo(LoadEventResponse.class);
                } catch (Exception e){
                    System.err.println(ar.result().body());
                    System.err.println(e.getLocalizedMessage());
                }

                handler.handle(Future.succeededFuture(response) );
                LOGGER.info( response.toString() );
            } else {
                LOGGER.error(String.format("failed to load events: %s", ar.cause().getLocalizedMessage()));
            }
        });
    }


    // TODO dedup magic string
    public MessageConsumer<JsonObject> subscribeToEventStream(AggregateIdentity identity) {
        return vertx.eventBus().consumer(String.format("%s-%s-Events", identity.type.getSimpleName(),
                identity.id));
    }

    public void close(Handler<AsyncResult<Void>> completionHandler) {
        vertx.close(completionHandler);
    }

    public String vertxAddressFor(Command command) {
        return String.format("%s-%s-Commands",
                command.aggregateIdentity().type.getSimpleName(),
                command.aggregateIdentity().id
        );
    }

    public String vertxAddressFor(Event event) {
        return String.format("%s-%s-Events",
                event.aggregateIdentity().type.getSimpleName(),
                event.aggregateIdentity().id
        );
    }
}