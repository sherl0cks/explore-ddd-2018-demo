package io.cabit;

import lombok.*;

import java.time.Instant;

@Data
@Builder
public class DispatchRejected implements DomainEvent {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Instant occurredOn = Instant.now();

    private final Boolean dispatchAccepted = false;
    private String rideId;
    private String driverId;
    private String dispatchId;

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
