package io.openinnovationlabs.ddd.eventstore;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.Event;

public class PersistenceEnvelope {

    public final Event event;

    @JsonCreator
    public PersistenceEnvelope(@JsonProperty("event") Event event) {
        this.event = event;
    }
}
