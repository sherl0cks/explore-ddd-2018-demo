package io.openinnovationlabs.domain.eventstore;

import io.openinnovationlabs.adapters.InMemoryAppendOnlyStore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

// TODO should probably be a service proxy
public class EventStore extends AbstractVerticle {

    private AppendOnlyStore store;

    @Override
    public void start() throws Exception {
        String appendOnlyStoreType = config().getString("appendOnlyStoreType");
        if ( appendOnlyStoreType.equals("InMemory") ){
            this.store = new InMemoryAppendOnlyStore();
        } else {
            new IllegalStateException(String.format("appendOnlyStoreType %s currently not implemented",appendOnlyStoreType));
        }
        MessageConsumer<JsonObject> appendConsumer = vertx.eventBus().consumer("EventStore-Append");
        appendConsumer.handler(message -> append(message));

        MessageConsumer<JsonObject> loadEventsConsumer = vertx.eventBus().consumer("EventStore-LoadEvents");
        loadEventsConsumer.handler(message -> loadEvents(message));

        MessageConsumer<JsonObject> clearEventsConsumer = vertx.eventBus().consumer("EventStore-Clear");
        clearEventsConsumer.handler(message -> clearEvents(message));
    }


    public void append(Message<JsonObject> message) {
        AppendEventsCommand command = message.body().mapTo(AppendEventsCommand.class);
        store.append(command.events);
    }

    public void loadEvents(Message<JsonObject> message) {
        LoadEventsCommand command = message.body().mapTo(LoadEventsCommand.class);
        LoadEventResponse response = new LoadEventResponse(store.loadEvents(command.aggregateIdentity));
        message.reply(JsonObject.mapFrom(response));
    }

    public void clearEvents(Message<JsonObject> message){
        if (config().getString("appendOnlyStoreType").equals("InMemory")){
            ((InMemoryAppendOnlyStore) store).clear();
            message.reply("success");
        } else {
            message.reply("fail");
        }
    }
}
