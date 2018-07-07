package io.openinnovationlabs.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.domain.Command;

/**
 * CreateOpportunity
 */
public class CreateOpportunity extends Command {

    public final String customerName;
    public final String opportunityType;

    @JsonCreator
    public CreateOpportunity(@JsonProperty("aggregateId") String aggregateId,
                             @JsonProperty("customerName") String customerName,
                             @JsonProperty("opportunityType") String opportunityType){
        super(Opportunity.class, aggregateId);
        this.customerName = customerName;
        this.opportunityType = opportunityType;
    }

}