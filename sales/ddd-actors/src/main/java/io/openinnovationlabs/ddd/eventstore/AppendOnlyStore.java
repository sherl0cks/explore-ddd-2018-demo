package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Event;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;

public interface AppendOnlyStore {

    public Future<Void> append(List<Event> events);
    public Future<List<Event>> loadEvents(AggregateIdentity aggregateIdentity);
}
