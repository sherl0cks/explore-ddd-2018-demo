package io.openinnovationlabs.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface Event {

    Instant occurredOn();

    AggregateIdentity aggregateIdentity();

    long index();


}