package io.cabit;

import lombok.*;

import java.time.Instant;

@Data
@Builder
public class PassengerPickedUp implements DomainEvent {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Instant occurredOn = Instant.now();

    private String rideId;
    private String driverId;
    private String passengerId;
    private String pickupLocation;

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
