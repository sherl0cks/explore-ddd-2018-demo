package io.openinnovationlabs.sales.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Event;

import java.time.Instant;

/**
 * OpportunityEvent
 */
public class OpportunityCreated implements Event {

    public final String customerName;
    public final String opportunityType;
    public final Instant occurredOn;
    public final OpportunityId opportunityId;

    public final long index;

    @JsonProperty("newStatus")
    public final String newStatus = "created";

    @JsonCreator
    public OpportunityCreated(@JsonProperty("opportunityId") OpportunityId opportunityId,
                              @JsonProperty("customerName") String customerName,
                              @JsonProperty("opportunityType") String opportunityType,
                              @JsonProperty("occurredOn") String occurredOn,
                              @JsonProperty("index") long index
    ) {
        this.occurredOn = Instant.parse(occurredOn);
        this.opportunityId = opportunityId;
        this.customerName = customerName;
        this.opportunityType = opportunityType;
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