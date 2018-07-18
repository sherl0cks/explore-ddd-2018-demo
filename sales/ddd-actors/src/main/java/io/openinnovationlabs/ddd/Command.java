package io.openinnovationlabs.ddd;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface Command {

    AggregateIdentity aggregateIdentity();
}