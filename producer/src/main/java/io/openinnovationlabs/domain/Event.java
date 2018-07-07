package io.openinnovationlabs.domain;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Event
 */
public abstract class Event {

    public final LocalDateTime occurredAt;
    public final String aggregateId;
    public final Class aggregateType;

    public Event(Class aggregateType, String aggregateId){

        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;

        // being selfish with the timezone
        // TODO revist timezone
        this.occurredAt = LocalDateTime.now(TimeZone.getTimeZone("America/Denver").toZoneId()); 
    }
}