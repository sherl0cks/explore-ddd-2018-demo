package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Event;
import io.openinnovationlabs.ddd.eventstore.AppendOnlyStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAppendOnlyStore implements AppendOnlyStore {

    private List<Event> dataStore = new ArrayList<>();

    @Override
    public void append(List<Event> events) {
        dataStore.addAll(events);
    }

    @Override
    public List<Event> loadEvents(AggregateIdentity aggregateIdentity) {
        List<Event> resultSet = new ArrayList<>();
        for (Event e : dataStore){
            AggregateIdentity aggregateIdentity1 = e.aggregateIdentity();
            if (aggregateIdentity.equals(aggregateIdentity1)){
                resultSet.add(e);
            }
        }
        return resultSet;
    }

    public void clear(){
        dataStore.clear();
    }
}
