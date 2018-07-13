package io.openinnovationlabs.sales.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.AbstractBaseEvent;

import java.time.Instant;

public class OpportunityWon extends AbstractBaseEvent {


    @JsonProperty("newStatus")
    public final String newStatus = "won";

    @JsonCreator
    public OpportunityWon(@JsonProperty("opportunityId") OpportunityId opportunityId,
                          @JsonProperty("occurredOn") String occurredOn,
                          @JsonProperty("streamIndex") long stream_index) {
        super(opportunityId, occurredOn, stream_index);
    }
}
