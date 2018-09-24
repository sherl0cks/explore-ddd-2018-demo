package io.cabit;

import java.util.UUID;

public class ObjectMother {

    public static RideRequested rideRequested() {
        return RideRequested.builder().
                destination("650 15th St, Denver, CO 80202").
                pickupLocation("1701 Wynkoop, Denver, CO 80202").
                passengerId(UUID.randomUUID().toString()).
                rideId(UUID.randomUUID().toString()).
                rideType("shared").
                build();
    }

    public static DispatchSent dispatchSent(RideRequested rideRequested) {
        return DispatchSent.builder().
                rideId(rideRequested.getRideId()).
                rideType(rideRequested.getRideType()).
                pickupLocation(rideRequested.getPickupLocation()).
                dispatchId(UUID.randomUUID().toString()).
                driverId(UUID.randomUUID().toString()).
                build();
    }

    public static DispatchRejected dispatchRejected(DispatchSent dispatchSent) {
        return DispatchRejected.builder().
                rideId(dispatchSent.getRideId()).
                dispatchId(dispatchSent.getDispatchId()).
                driverId(dispatchSent.getDriverId()).
                build();
    }

    public static DispatchAccepted dispatchAccepted(DispatchSent dispatchSent) {
        return DispatchAccepted.builder().
                rideId(dispatchSent.getRideId()).
                dispatchId(dispatchSent.getDispatchId()).
                driverId(dispatchSent.getDriverId()).
                build();
    }

    public static PassengerPickedUp passengerPickedUp(RideRequested rideRequested, DispatchAccepted accepted){
        return PassengerPickedUp.builder().
                rideId(rideRequested.getRideId()).
                pickupLocation(rideRequested.getPickupLocation()).
                driverId(accepted.getDriverId()).
                passengerId(rideRequested.getPassengerId()).
                build();
    }

    public static PassengerPickedUp passengerPickedUp(String rideId){
        return PassengerPickedUp.builder().
                rideId(rideId).
                pickupLocation("1701 Wynkoop, Denver, CO 80202").
                driverId("this is wrong cuz its a demo").
                passengerId("this is wrong cuz its a demo").
                build();
    }


    public static PassengerDroppedOff passengerDroppedOff(RideRequested rideRequested, PassengerPickedUp passengerPickedUp){
        return PassengerDroppedOff.builder().
                rideId(rideRequested.getRideId()).
                destination(rideRequested.getDestination()).
                driverId(passengerPickedUp.getDriverId()).
                passengerId(rideRequested.getPassengerId()).
                build();
    }
}
