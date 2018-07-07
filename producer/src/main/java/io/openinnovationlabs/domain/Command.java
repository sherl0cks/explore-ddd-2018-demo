package io.openinnovationlabs.domain;

/**
 * TODO
 * Command
 */
public abstract class Command {

    public final Class aggregateType;
    public final String aggregateId;

    public Command(Class aggregateType, String aggregateId) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }
}