package io.openinnovationlabs.sales.domain.opportunity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.AbstractBaseEvent;
import io.openinnovationlabs.ddd.AggregateIdentity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * OpportunityEvent
 */


public class OpportunityCreated extends AbstractBaseEvent {

    public final String customerName;
    public final String opportunityType;

    @JsonProperty("newStatus")
    public final String newStatus = "created";

    @JsonCreator
    public OpportunityCreated(@JsonProperty("opportunityId") AggregateIdentity opportunityId,
                              @JsonProperty("customerName") String customerName,
                              @JsonProperty("opportunityType") String opportunityType,
                              @JsonProperty("occurredOn") String occurredOn,
                              @JsonProperty("streamIndex") long stream_index
    ) {
        super(opportunityId, occurredOn, stream_index);
        this.customerName = customerName;
        this.opportunityType = opportunityType;
    }

}