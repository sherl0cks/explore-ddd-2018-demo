package io.openinnovationlabs.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.domain.AggregateIdentity;

public class OpportunityId extends AggregateIdentity {
    @JsonCreator
    public OpportunityId(@JsonProperty("id") String id) {
        super(id, Opportunity.class);
    }
}
