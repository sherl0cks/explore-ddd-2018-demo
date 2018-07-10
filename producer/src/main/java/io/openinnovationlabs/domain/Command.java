package io.openinnovationlabs.domain;

/**
 * TODO dunno what to do with this interface or even if its needed. maybe shouldn't have public final variables on commands / events?
 *
 */
public interface Command {

    AggregateIdentity aggregateIdentity();
}