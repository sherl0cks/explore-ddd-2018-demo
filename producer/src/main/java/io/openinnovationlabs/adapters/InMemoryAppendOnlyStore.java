package io.openinnovationlabs.adapters;

import io.openinnovationlabs.domain.AggregateIdentity;
import io.openinnovationlabs.domain.Event;
import io.openinnovationlabs.domain.eventstore.AppendOnlyStore;

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
