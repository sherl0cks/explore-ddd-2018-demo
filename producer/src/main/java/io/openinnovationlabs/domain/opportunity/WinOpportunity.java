package io.openinnovationlabs.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.domain.Command;

public class WinOpportunity implements Command<OpportunityId> {
    public final OpportunityId opportunityId;

    @JsonCreator()
    public WinOpportunity(@JsonProperty("opportunityId") OpportunityId opportunityId) {
        this.opportunityId = opportunityId;
    }

    @Override
    public OpportunityId aggregateIdentity() {
        return opportunityId;
    }
}
