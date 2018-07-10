package io.openinnovationlabs.domain.eventstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.domain.AggregateIdentity;

public class LoadEventsCommand {
    public final AggregateIdentity aggregateIdentity;

    @JsonCreator
    public LoadEventsCommand(@JsonProperty("aggregateIdentity") AggregateIdentity aggregateIdentity) {
        this.aggregateIdentity = aggregateIdentity;
    }
}
