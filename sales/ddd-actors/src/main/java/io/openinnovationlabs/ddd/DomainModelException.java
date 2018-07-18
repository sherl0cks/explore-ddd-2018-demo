package io.openinnovationlabs.ddd;

public class DomainModelException extends RuntimeException {


    public DomainModelException(String message) {
        super(message);
    }

    public DomainModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainModelException(Throwable cause) {
        super(cause);
    }
}
