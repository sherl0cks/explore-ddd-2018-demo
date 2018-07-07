package io.openinnovationlabs.domain.opportunity;

import io.openinnovationlabs.domain.Aggregate;
import io.openinnovationlabs.domain.DomainModel;
import io.openinnovationlabs.domain.Event;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OpportunityAggregate
 */
public class Opportunity extends Aggregate {

    private static Logger LOGGER = LoggerFactory.getLogger(Opportunity.class);

    private String customerName;
    private String opportunityType;
    private boolean deleted;



    /**
     * Command Processors must be public for reflection to work
     *
     */

    public List<Event> process(CreateOpportunity createOpportunity) {
        if (deleted) {
            return Collections.emptyList();
        } else {
            List<Event> events = new ArrayList();
            events.add(OpportunityCreated.from(createOpportunity));
            return events;
        }
    }

    /**
     * Event Appliers must be public for reflection to work
     */

    public void apply(OpportunityCreated event) {
        this.customerName = event.customerName;
        this.opportunityType = event.opportunityType;
    }

}