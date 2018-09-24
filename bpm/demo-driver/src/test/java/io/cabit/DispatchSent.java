package io.cabit;

import lombok.*;

import java.time.Instant;

@Data
@Builder
public class DispatchSent implements DomainEvent {


    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Instant occurredOn = Instant.now();

    private String rideId;
    private String driverId;
    private String dispatchId;
    private String pickupLocation;
    private String rideType;

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
