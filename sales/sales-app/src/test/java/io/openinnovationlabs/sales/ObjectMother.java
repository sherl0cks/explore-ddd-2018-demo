package io.openinnovationlabs.sales;

import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityId;

import java.util.UUID;

public class ObjectMother {

    public static CreateOpportunity createOpportunityCommand() {
        return new CreateOpportunity(new OpportunityId(UUID.randomUUID().toString()), "test", "residency");
    }
}
