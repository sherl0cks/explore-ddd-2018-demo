package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Event;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface AppendOnlyStore {

    public void append(List<Event> events);
    public List<Event> loadEvents(AggregateIdentity aggregateIdentity);
}
