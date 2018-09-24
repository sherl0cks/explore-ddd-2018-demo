package io.cabit;

import lombok.*;

import java.time.Instant;

@Data
@Builder
public class PassengerDroppedOff implements DomainEvent {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Instant occurredOn = Instant.now();

    private String passengerId;
    private String rideId;
    private String driverId;
    private String destination;

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
