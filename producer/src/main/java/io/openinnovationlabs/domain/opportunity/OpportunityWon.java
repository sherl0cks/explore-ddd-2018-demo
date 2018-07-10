package io.openinnovationlabs.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.domain.AggregateIdentity;
import io.openinnovationlabs.domain.Event;

import java.time.Instant;

public class OpportunityWon implements Event {

    public final OpportunityId opportunityId;
    public final Instant occurredOn;

    public final long index;

    @JsonProperty("newStatus")
    public final String newStatus = "won";

    @JsonCreator
    public OpportunityWon(@JsonProperty("opportunityId") OpportunityId opportunityId,
                          @JsonProperty("occurredOn") String occurredOn,
                          @JsonProperty("index") long index
    ) {
        this.opportunityId = opportunityId;
        this.occurredOn = Instant.parse(occurredOn);
        this.index = index;
    }


    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public AggregateIdentity aggregateIdentity() {
        return opportunityId;
    }

    @Override
    public long index() {
        return index;
    }
}
