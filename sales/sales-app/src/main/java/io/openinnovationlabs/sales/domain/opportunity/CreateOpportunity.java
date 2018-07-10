package io.openinnovationlabs.sales.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.Command;

/**
 * CreateOpportunity
 */
public class CreateOpportunity implements Command {

    public final String customerName;
    public final String opportunityType;
    public final OpportunityId opportunityId;

    @JsonCreator
    public CreateOpportunity(@JsonProperty("opportunityId") OpportunityId opportunityId,
                             @JsonProperty("customerName") String customerName,
                             @JsonProperty("opportunityType") String opportunityType) {
        this.opportunityId = opportunityId;
        this.customerName = customerName;
        this.opportunityType = opportunityType;
    }

    @Override
    public OpportunityId aggregateIdentity() {
        return opportunityId;
    }
}