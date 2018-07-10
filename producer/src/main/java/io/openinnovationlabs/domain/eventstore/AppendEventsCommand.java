package io.openinnovationlabs.domain.eventstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.domain.Event;

import java.util.List;

public class AppendEventsCommand {

    public final List<Event> events;

    @JsonCreator
    public AppendEventsCommand(@JsonProperty("events") List<Event> events) {
        this.events = events;
    }
}
