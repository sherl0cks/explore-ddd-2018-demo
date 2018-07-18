package io.openinnovationlabs.sales.adapters.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityId;

public class OpportunityDTO {

    public String customerName = "";
    public String opportunityType = "";

    @JsonCreator
    public OpportunityDTO(@JsonProperty("customerName") String customerName,
                          @JsonProperty("opportunityType") String opportunityType) {
        this.customerName = customerName;
        this.opportunityType = opportunityType;
    }

    public CreateOpportunity to(String id) {
        CreateOpportunity createOpportunity = new CreateOpportunity(new OpportunityId(id), customerName,
                opportunityType);
        return createOpportunity;
    }
}
