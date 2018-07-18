package io.openinnovationlabs.sales.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.AbstractBaseCommand;
import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Command;

public class WinOpportunity extends AbstractBaseCommand {

    @JsonCreator()
    public WinOpportunity(@JsonProperty("opportunityId") AggregateIdentity opportunityId) {
        super(opportunityId);
    }

    public WinOpportunity(String opportunityId){
        super(new OpportunityId(opportunityId));
    }
}
