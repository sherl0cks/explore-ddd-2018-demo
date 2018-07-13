package io.openinnovationlabs.ddd.eventstore;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface EventStore {

    String APPEND_ADDRESS = "v1-EventStore-Append";
    String LOAD_EVENT_ADDRESS = "v1-EventStore-LoadEvents";

    void append(Message<JsonObject> loadEventsCommand);

    void loadEvents(Message<JsonObject> appendEventsCommand);
}
