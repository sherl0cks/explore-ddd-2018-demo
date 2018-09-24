package io.cabit;

import lombok.*;

import java.time.Instant;

@Builder
@Data
public class RideRequested implements DomainEvent {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Instant occurredOn = Instant.now();
    
    private String passengerId;
    private String rideId;
    private String destination;
    private String pickupLocation;
    private String rideType;

    @Override
    public Instant occurredOn() {
        return occurredOn;

    }

}
