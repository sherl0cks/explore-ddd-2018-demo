package io.openinnovationlabs.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * TODO split out an interface from the abstract class
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface Event {

    Instant occurredOn();
    AggregateIdentity aggregateIdentity();
    long index();


}