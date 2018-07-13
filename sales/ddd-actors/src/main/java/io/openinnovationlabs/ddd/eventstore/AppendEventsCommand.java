package io.openinnovationlabs.ddd.eventstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.Event;

import java.util.List;

public class AppendEventsCommand {

    public final List<Event> events;

    @JsonCreator
    public AppendEventsCommand(@JsonProperty("events") List<Event> events) {
        this.events = events;
    }
}
