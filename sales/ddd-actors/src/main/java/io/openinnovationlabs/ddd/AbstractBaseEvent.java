package io.openinnovationlabs.ddd;

import java.time.Instant;

public abstract class AbstractBaseEvent implements Event {

    public final AggregateIdentity aggregateIdentity;
    public final Instant occurredOn;
    public final long eventStreamIndex;

    public AbstractBaseEvent(AggregateIdentity aggregateIdentity,
                             String occurredOn,
                             long eventStreamIndex) {
        this.aggregateIdentity = aggregateIdentity;
        this.occurredOn = Instant.parse(occurredOn);
        this.eventStreamIndex = eventStreamIndex;
    }

    public AbstractBaseEvent(AggregateIdentity aggregateIdentity,
                             Instant occurredOn,
                             long eventStreamIndex) {
        this.aggregateIdentity = aggregateIdentity;
        this.occurredOn = occurredOn;
        this.eventStreamIndex = eventStreamIndex;
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }

    @Override
    public AggregateIdentity getAggregateIdentity() {
        return aggregateIdentity;
    }

    @Override
    public long getEventStreamIndex() {
        return eventStreamIndex;
    }

    @Override
    public String toString() {
        return String.format("%s :: %s :: %d", aggregateIdentity, this.getClass().getSimpleName(), eventStreamIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractBaseEvent that = (AbstractBaseEvent) o;

        if (eventStreamIndex != that.eventStreamIndex) return false;
        if (aggregateIdentity != null ? !aggregateIdentity.equals(that.aggregateIdentity) : that.aggregateIdentity != null)
            return false;
        return occurredOn != null ? occurredOn.equals(that.occurredOn) : that.occurredOn == null;
    }

    @Override
    public int hashCode() {
        int result = aggregateIdentity != null ? aggregateIdentity.hashCode() : 0;
        result = 31 * result + (occurredOn != null ? occurredOn.hashCode() : 0);
        result = 31 * result + (int) (eventStreamIndex ^ (eventStreamIndex >>> 32));
        return result;
    }


}
