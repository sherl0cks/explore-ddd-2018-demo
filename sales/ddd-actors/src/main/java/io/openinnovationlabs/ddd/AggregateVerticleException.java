package io.openinnovationlabs.ddd;

public class AggregateVerticleException extends Exception {

    public AggregateVerticleException(String message) {
        super(message);
    }

    public AggregateVerticleException(String message, Throwable cause) {
        super(message, cause);
    }

    public AggregateVerticleException(Throwable cause) {
        super(cause);
    }
}
