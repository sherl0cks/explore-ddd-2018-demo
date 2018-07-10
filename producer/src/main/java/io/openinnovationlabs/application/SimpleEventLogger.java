package io.openinnovationlabs.application;

import io.openinnovationlabs.domain.Event;

import java.util.List;

/**
 * TODO I'd put this in a base class but those are giving me fits with Jackson at the moment
 */
public class SimpleEventLogger {

    public static String log(Event e){
        return String.format("%s :: %s :: %d", e.aggregateIdentity(), e.getClass().getSimpleName(), e.index());
    }

    public static String log(List<Event> events){
        StringBuilder b = new StringBuilder();
        for (Event e : events){
            b.append(SimpleEventLogger.log(e)).append("\n");
        }
        return b.toString();
    }
}
