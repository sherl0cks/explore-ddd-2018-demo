package io.openinnovationlabs.sales;

import io.openinnovationlabs.sales.adapters.http.OpportunityDTO;
import io.openinnovationlabs.sales.domain.opportunity.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ObjectMother {

    public static OpportunityDTO opportunityDTO() {
        return new OpportunityDTO("test", "residency");
    }

    public static CreateOpportunity opportunityCommand(){
        return new CreateOpportunity(new
                OpportunityId(UUID.randomUUID().toString()),
                "test", "residency");
    }

    public static OpportunityCreated opportunityCreated(){
        return new OpportunityCreated(new OpportunityId(UUID.randomUUID().toString()), "acme", "residency", Instant.now().minus(1, ChronoUnit.HOURS).toString(), 0);
    }

    public static OpportunityCreated badOpportunityCreated(){
        return new OpportunityCreated(new OpportunityId(UUID.randomUUID().toString()), "acme", "residency", Instant.now().minus(1, ChronoUnit.HOURS).toString(), 0);
    }


}
