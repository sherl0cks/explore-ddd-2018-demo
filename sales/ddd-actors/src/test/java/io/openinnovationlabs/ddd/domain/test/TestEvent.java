package io.openinnovationlabs.ddd.domain.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openinnovationlabs.ddd.AbstractBaseEvent;
import io.openinnovationlabs.ddd.AggregateIdentity;

public class TestEvent extends AbstractBaseEvent {
    public String name;

    @JsonCreator
    public TestEvent(@JsonProperty("aggregateIdentity") AggregateIdentity aggregateIdentity,
                     @JsonProperty("occurredOn") String occurredOn,
                     @JsonProperty("streamIndex") long streamIndex,
                     @JsonProperty("name") String name) {
        super(aggregateIdentity, occurredOn, streamIndex);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TestEvent event = (TestEvent) o;

        return name != null ? name.equals(event.name) : event.name == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
