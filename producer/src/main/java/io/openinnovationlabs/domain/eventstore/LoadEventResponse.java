package io.openinnovationlabs.domain.eventstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.application.SimpleEventLogger;
import io.openinnovationlabs.domain.Event;

import java.util.List;

public class LoadEventResponse {

    public final List<Event> events;

    @JsonCreator
    public LoadEventResponse(@JsonProperty("events") List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {

        return "LoadEventResponse{" +
                "events=[\n" + SimpleEventLogger.log(events) +
                "]}";
    }
}
