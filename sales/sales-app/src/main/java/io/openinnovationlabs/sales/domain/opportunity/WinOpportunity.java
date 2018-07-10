package io.openinnovationlabs.sales.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.Command;

public class WinOpportunity implements Command {
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
