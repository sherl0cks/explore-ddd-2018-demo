package io.openinnovationlabs.domain.opportunity;

import io.openinnovationlabs.domain.Event;

/**
 * OpportunityEvent
 */
public class OpportunityCreated extends Event {

    public final String customerName;
    public final String opportunityType;

    public OpportunityCreated(String aggregateId, String customerName, String opportunityType) {
        super(Opportunity.class, aggregateId);
        this.customerName = customerName;
        this.opportunityType = opportunityType;
    }

    public static OpportunityCreated from(CreateOpportunity command) {
        return new OpportunityCreated(command.aggregateId, command.customerName, command.opportunityType);
    }

}