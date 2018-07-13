package io.openinnovationlabs.ddd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;


public class EventsReplayed implements Event {

    public final Instant occurredOn;
    public final AggregateIdentity aggregateIdentity;

    @JsonCreator
    public EventsReplayed(
            @JsonProperty("occurredOn") String occurredOn,
            @JsonProperty("aggregateIdentity") AggregateIdentity aggregateIdentity
    ) {
        this.occurredOn = Instant.parse(occurredOn);
        this.aggregateIdentity = aggregateIdentity;
    }

    @Override
    public Instant occurredOn() {
        return null;
    }

    @Override
    public AggregateIdentity aggregateIdentity() {
        return null;
    }

    @Override
    public long stream_index() {
        return -1;
    }
}
