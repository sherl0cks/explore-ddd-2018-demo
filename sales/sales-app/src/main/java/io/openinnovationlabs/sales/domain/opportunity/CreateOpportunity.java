package io.openinnovationlabs.sales.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.AbstractBaseCommand;
import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Command;

/**
 * CreateOpportunity
 */
public class CreateOpportunity extends AbstractBaseCommand {

    public final String customerName;
    public final String opportunityType;
    public final String opportunityName;

    @JsonCreator
    public CreateOpportunity(@JsonProperty("opportunityId") AggregateIdentity opportunityId,
                             @JsonProperty("customerName") String customerName,
                             @JsonProperty("opportunityType") String opportunityType,
                             @JsonProperty("opportunityName") String opportunityName) {
        super(opportunityId);
        this.customerName = customerName;
        this.opportunityType = opportunityType;
        this.opportunityName = opportunityName;
    }

}