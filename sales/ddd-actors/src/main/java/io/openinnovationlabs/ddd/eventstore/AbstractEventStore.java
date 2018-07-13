package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.Event;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


// TODO should probably be a service proxy
public abstract class AbstractEventStore extends AbstractVerticle implements AppendOnlyStore, EventStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEventStore.class);

    @Override
    public void append(Message<JsonObject> message) {
        AppendEventsCommand command = message.body().mapTo(AppendEventsCommand.class);
        append(command.events).setHandler(ar -> {
            if ( ar.succeeded() ){
                LOGGER.debug(String.format("Events appended %d", command.events.size()));
                message.reply("success");
            } else {
                LOGGER.error(String.format("Failed to append events: %s", ar.cause().toString()));
                message.reply(String.format("failed: %s", ar.cause().toString()));
            }
        });


    }

    @Override
    public void loadEvents(Message<JsonObject> message) {
        LoadEventsCommand command = message.body().mapTo(LoadEventsCommand.class);

        loadEvents(command.aggregateIdentity).setHandler(ar -> {
            if (ar.succeeded()) {
                LoadEventResponse response = new LoadEventResponse(ar.result());
                message.reply(JsonObject.mapFrom(response));
                LOGGER.debug("Load events reply");
            } else {
                LOGGER.error(String.format("Failed to load events: %s", ar.cause().toString()));
                message.reply(new LoadEventResponse(new ArrayList<Event>()));
            }
        });


    }

    protected void initializeEventBusConsumers() {
        vertx.eventBus().<JsonObject>consumer(APPEND_ADDRESS).handler(this::append);
        vertx.eventBus().<JsonObject>consumer(LOAD_EVENT_ADDRESS).handler(this::loadEvents);
        LOGGER.debug("EventStore addresses subscribed");
    }
}
