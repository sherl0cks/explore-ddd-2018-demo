package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.eventstore.InMemoryAppendOnlyStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO should probably be a service proxy
public class EventStore extends AbstractVerticle {

    public final static String APPEND_ADDRESS = "v1-EventStore-Append";
    public final static String LOAD_EVENT_ADDRESS = "v1-EventStore-LoadEvents";

    private final static Logger LOGGER = LoggerFactory.getLogger(EventStore.class);

    private AppendOnlyStore store;

    @Override
    public void start() throws Exception {
        String appendOnlyStoreType = config().getString("appendOnlyStoreType");
        if (appendOnlyStoreType.equals("InMemory")) {
            this.store = new InMemoryAppendOnlyStore();
        } else {
            new IllegalStateException(String.format("appendOnlyStoreType %s currently not implemented", appendOnlyStoreType));
        }

        vertx.eventBus().<JsonObject>consumer(APPEND_ADDRESS).handler(this::append);

        vertx.eventBus().<JsonObject>consumer(LOAD_EVENT_ADDRESS).handler(this::loadEvents);

        LOGGER.info("Event Store is up");
    }


    public void append(Message<JsonObject> message) {
        AppendEventsCommand command = message.body().mapTo(AppendEventsCommand.class);
        store.append(command.events);
        message.reply("complete");
        LOGGER.debug(String.format("Events appended %d", command.events.size()));
    }

    public void loadEvents(Message<JsonObject> message) {
        LoadEventsCommand command = message.body().mapTo(LoadEventsCommand.class);
        LoadEventResponse response = new LoadEventResponse(store.loadEvents(command.aggregateIdentity));
        message.reply(JsonObject.mapFrom(response));
        LOGGER.debug("Load events reply");
    }

}
