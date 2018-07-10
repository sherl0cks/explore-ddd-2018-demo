package io.openinnovationlabs.ddd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ReplayEventsCommand implements Command {

    public final AggregateIdentity aggregateIdentity;
    public final List<Event> events;

    @JsonCreator()
    public ReplayEventsCommand(@JsonProperty("aggregateIdentity") AggregateIdentity aggregateIdentity,
                               @JsonProperty("events") List<Event> events) {
        this.aggregateIdentity = aggregateIdentity;
        this.events = events;
    }

    @Override
    public AggregateIdentity aggregateIdentity() {
        return aggregateIdentity;
    }
}
