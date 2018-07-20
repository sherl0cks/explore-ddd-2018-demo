package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Event;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a simple impl for testing purposes only
 */
public class InMemoryAppendOnlyEventStoreForTests extends AbstractEventStore implements AppendOnlyStore {

    private List<Event> dataStore = new ArrayList<>();

    @Override
    public void start() throws Exception {
        initializeEventBusConsumers();
    }

    @Override
    public Future<Void> append(List<Event> events) {
        Future future = Future.future();
        dataStore.addAll(events);
        future.complete();
        return future;
    }

    @Override
    public Future<List<Event>> loadEvents(AggregateIdentity aggregateIdentity) {
        Future<List<Event>> future = Future.future();
        List<Event> resultSet = new ArrayList<>();
        for (Event e : dataStore){
            AggregateIdentity aggregateIdentity1 = e.getAggregateIdentity();
            if (aggregateIdentity.equals(aggregateIdentity1)){
                resultSet.add(e);
            }
        }
        future.complete(resultSet);
        return future;
    }

}
