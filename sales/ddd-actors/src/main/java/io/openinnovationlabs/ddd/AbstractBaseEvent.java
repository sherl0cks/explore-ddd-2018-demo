package io.openinnovationlabs.ddd;

import java.time.Instant;

public abstract class AbstractBaseEvent implements Event {

    public final AggregateIdentity aggregateIdentity;
    public final Instant occurredOn;
    public final long streamIndex;

    public AbstractBaseEvent(AggregateIdentity aggregateIdentity,
                             String occurredOn,
                             long streamIndex) {
        this.aggregateIdentity = aggregateIdentity;
        this.occurredOn = Instant.parse(occurredOn);
        this.streamIndex = streamIndex;
    }

    public AbstractBaseEvent(AggregateIdentity aggregateIdentity,
                             Instant occurredOn,
                             long streamIndex) {
        this.aggregateIdentity = aggregateIdentity;
        this.occurredOn = occurredOn;
        this.streamIndex = streamIndex;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public AggregateIdentity aggregateIdentity() {
        return aggregateIdentity;
    }

    @Override
    public long stream_index() {
        return streamIndex;
    }

    @Override
    public String toString() {
        return String.format("%s :: %s :: %d", aggregateIdentity, this.getClass().getSimpleName(), streamIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractBaseEvent that = (AbstractBaseEvent) o;

        if (streamIndex != that.streamIndex) return false;
        if (aggregateIdentity != null ? !aggregateIdentity.equals(that.aggregateIdentity) : that.aggregateIdentity != null)
            return false;
        return occurredOn != null ? occurredOn.equals(that.occurredOn) : that.occurredOn == null;
    }

    @Override
    public int hashCode() {
        int result = aggregateIdentity != null ? aggregateIdentity.hashCode() : 0;
        result = 31 * result + (occurredOn != null ? occurredOn.hashCode() : 0);
        result = 31 * result + (int) (streamIndex ^ (streamIndex >>> 32));
        return result;
    }
}
