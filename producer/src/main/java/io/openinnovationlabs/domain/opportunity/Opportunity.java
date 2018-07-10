package io.openinnovationlabs.domain.opportunity;

import io.openinnovationlabs.domain.Aggregate;
import io.openinnovationlabs.domain.Event;
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
    private String status = "";


    /**
     * Command Processors must be public for reflection to work
     */

    public List<Event> process(CreateOpportunity command) {
        if (status.equals("created")) {
            return Collections.emptyList();
        } else {
            List<Event> events = Arrays.asList(
                    new OpportunityCreated(
                            command.opportunityId,
                            command.customerName,
                            command.opportunityType,
                            Instant.now().toString(),
                            eventIndex++
                    )
            );
            return events;
        }
    }

    public List<Event> process(WinOpportunity command) {
        if (status.isEmpty()){
            // TODO better exception than this
            throw new RuntimeException("Opportunity must be created first");
        } else if (status.equals("won")) {
            return Collections.emptyList();
        } else {
            List<Event> events = Arrays.asList(
                    new OpportunityWon(
                            command.opportunityId,
                            Instant.now().toString(),
                            eventIndex++
                    )
            );
            return events;
        }
    }

    /**
     * Event Appliers must be public for reflection to work
     */

    public void apply(OpportunityCreated event) {
        this.customerName = event.customerName;
        this.opportunityType = event.opportunityType;
        this.status = "created";
    }

    public void apply(OpportunityWon event) {
        this.status = "won";
    }

}