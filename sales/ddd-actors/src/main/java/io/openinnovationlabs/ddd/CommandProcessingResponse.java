package io.openinnovationlabs.ddd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CommandProcessingResponse {

    public final List<Event> events;
    public final DomainModelException exception;

    @JsonCreator
    public CommandProcessingResponse(@JsonProperty("events") List<Event> events, @JsonProperty("exception") DomainModelException exception) {
        this.events = events;
        this.exception = exception;
    }

    public CommandProcessingResponse(DomainModelException exception) {
        this.exception = exception;
        this.events = null;
    }

    public CommandProcessingResponse(List<Event> events) {
        this.events = events;
        this.exception = null;
    }

    public boolean succeeded() {
        if (exception == null) {
            return true;
        } else {
            return false;
        }
    }
}
