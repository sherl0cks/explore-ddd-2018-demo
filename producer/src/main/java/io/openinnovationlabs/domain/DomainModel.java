package io.openinnovationlabs.domain;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO Something could be done here with service proxies
 */
public class DomainModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainModel.class);
    private Vertx vertx;

    public DomainModel(Vertx vertx) {
        this.vertx = vertx;
    }

    public void issueCommand(Command command) {
        String address = vertxEventBusAddressFor(command);
        DeliveryOptions options = new DeliveryOptions().addHeader("commandClassname", command.getClass().getName());
        vertx.eventBus().send(address, JsonObject.mapFrom(command), options, ar -> {
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
        JsonObject config = new JsonObject().put("id", command.aggregateId);
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(command.aggregateType, options, ar -> {
            if (ar.succeeded()) {
                LOGGER.info(String.format("deployment of %s %s succeeded", command.aggregateType.getSimpleName(),
                        command
                                .aggregateId));

                // when verticle is deployed, send a create message to it
                issueCommand(command);
            } else {
                LOGGER.error(String.format("deployment of %s %s failed", command.aggregateType.getSimpleName(), command.aggregateId
                ));
            }
        });
    }

    public void publishEvent(Event event) {
        vertx.eventBus().publish(vertxEventBusAddressFor(event), JsonObject.mapFrom(event));
    }

    public String vertxEventBusAddressFor(Command command) {
        return String.format("%s-%s-Commands",
                command.aggregateType.getSimpleName(),
                command.aggregateId
        );
    }

    public String vertxEventBusAddressFor(Event event) {
        return String.format("%s-%s-Events",
                event.aggregateType.getSimpleName(),
                event.aggregateId
        );
    }

    // TODO dedup magic string
    public MessageConsumer<JsonObject> subscribeToEventStream(Class aggregateType, String aggregateId){
       return vertx.eventBus().consumer(String.format("%s-%s-Events",aggregateType.getSimpleName(),aggregateId));
    }

}