package io.openinnovationlabs.ddd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;


public class EventsReplayed implements Event {

    public final Instant occurredOn;
    public final AggregateIdentity aggregateIdentity;

    @JsonCreator
    public EventsReplayed(
            @JsonProperty("getOccurredOn") String occurredOn,
            @JsonProperty("getAggregateIdentity") AggregateIdentity aggregateIdentity
    ) {
        this.occurredOn = Instant.parse(occurredOn);
        this.aggregateIdentity = aggregateIdentity;
    }

    @Override
    public Instant getOccurredOn() {
        return null;
    }

    @Override
    public AggregateIdentity getAggregateIdentity() {
        return null;
    }

    @Override
    public long getEventStreamIndex() {
        return -1;
    }
}
