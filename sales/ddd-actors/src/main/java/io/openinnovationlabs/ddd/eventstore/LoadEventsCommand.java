package io.openinnovationlabs.ddd.eventstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.AggregateIdentity;

public class LoadEventsCommand {
    public final AggregateIdentity aggregateIdentity;

    @JsonCreator
    public LoadEventsCommand(@JsonProperty("getAggregateIdentity") AggregateIdentity aggregateIdentity) {
        this.aggregateIdentity = aggregateIdentity;
    }
}
