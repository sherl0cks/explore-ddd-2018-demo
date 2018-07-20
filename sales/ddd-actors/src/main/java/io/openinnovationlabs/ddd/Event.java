package io.openinnovationlabs.ddd;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface Event {

    Instant getOccurredOn();

    AggregateIdentity getAggregateIdentity();

    long getEventStreamIndex();


}