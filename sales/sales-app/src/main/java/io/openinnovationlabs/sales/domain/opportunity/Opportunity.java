package io.openinnovationlabs.sales.domain.opportunity;


import io.openinnovationlabs.ddd.Aggregate;
import io.openinnovationlabs.ddd.DomainModelException;
import io.openinnovationlabs.ddd.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * OpportunityAggregate
 */
public class Opportunity extends Aggregate {

    private static Logger LOGGER = LoggerFactory.getLogger(Opportunity.class);

    private String customerName = "";
    private String opportunityType = "";
    private String opportunityName = "";
    private String status = "";


    /**
     * Command Processors must be public for reflection translate work
     */

    public List<Event> process(CreateOpportunity command) {
        if (status.equals("created")) {
            return Collections.emptyList();
        } else if (command.opportunityName == null || command.opportunityName.isEmpty()){
            throw new DomainModelException("opportunity name cannot be null or empty");
        } {
            List<Event> events = Arrays.asList(
                    new OpportunityCreated(
                            command.aggregateIdentity(),
                            command.customerName,
                            command.opportunityType,
                            Instant.now().toString(),
                            eventStreamIndex++,
                            command.opportunityName
                    )
            );
            return events;
        }
    }

    public List<Event> process(WinOpportunity command) {
        if (status.isEmpty()){
            throw new DomainModelException("Opportunity must be created first");
        } else if (status.equals("won")) {
            return Collections.emptyList();
        } else {
            List<Event> events = Arrays.asList(
                    new OpportunityWon(
                            command.aggregateIdentity(),
                            Instant.now().toString(),
                            eventStreamIndex++
                    )
            );
            return events;
        }
    }

    /**
     * Event Appliers must be public for reflection translate work
     */

    public void apply(OpportunityCreated event) {
        this.customerName = event.customerName;
        this.opportunityType = event.opportunityType;
        this.status = "created";
        this.opportunityName = event.opportunityName;
    }

    public void apply(OpportunityWon event) {
        this.status = "won";
    }

}