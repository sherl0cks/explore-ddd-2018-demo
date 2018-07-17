package io.openinnovationlabs.ddd;

public class AbstractBaseCommand implements Command {

    public final AggregateIdentity aggregateIdentity;

    public AbstractBaseCommand(AggregateIdentity aggregateIdentity) {
        this.aggregateIdentity = aggregateIdentity;
    }

    @Override
    public AggregateIdentity aggregateIdentity() {
        return aggregateIdentity;
    }

    @Override
    public String toString() {
        return String.format("%s for %s", this.getClass().getSimpleName(), aggregateIdentity);
    }
}
