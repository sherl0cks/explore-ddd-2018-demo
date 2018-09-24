package org.explore.ddd;

import io.cabit.*;
import org.junit.BeforeClass;
import org.junit.Test;
import rh.demo.kafka.ext.KafkaBridgeProperties;
import rh.demo.kafka.ext.KafkaBridgePropertiesFactory;

import java.util.HashMap;
import java.util.Map;

public class DemoDriver {

    private static KieServerDomainEventAdapter adapter;

    @BeforeClass
    public static void setUp() {

        KafkaBridgeProperties properties = KafkaBridgePropertiesFactory.fromFile("demo-kafka.properties");
        properties.setDomainEventCorrelationKeyProperty("rideId");

        Map<String, String> variableNameMap = new HashMap<>();
        variableNameMap.put("dispatchAccepted", "dispatchAccepted");

        Map<String, Map<String, String>> userTaskMap = new HashMap<>();
        userTaskMap.put("DispatchAccepted", variableNameMap);
        userTaskMap.put("DispatchRejected", variableNameMap);
        properties.setDomainEventUserTaskMapping(userTaskMap);

        adapter = new KieServerDomainEventAdapter(properties);
    }

    @Test
    public void shouldRunTheHappyPath() {
        RideRequested rideRequested = ObjectMother.rideRequested();
        adapter.publish(rideRequested);

        DispatchSent dispatchSent = ObjectMother.dispatchSent(rideRequested);
        adapter.publish(dispatchSent);

        DispatchAccepted dispatchAccepted = ObjectMother.dispatchAccepted(dispatchSent);
        adapter.publish(dispatchAccepted);

// TODO uncomment and re run

        PassengerPickedUp passengerPickedUp = ObjectMother.passengerPickedUp(rideRequested, dispatchAccepted);
        adapter.publish(passengerPickedUp);

        PassengerDroppedOff passengerDroppedOff = ObjectMother.passengerDroppedOff(rideRequested, passengerPickedUp);
        adapter.publish(passengerDroppedOff);
    }


    @Test
    public void shouldRunTheSadPath() {

        RideRequested rideRequested = ObjectMother.rideRequested();
        adapter.publish(rideRequested);

        DispatchSent dispatchSent = ObjectMother.dispatchSent(rideRequested);
        adapter.publish(dispatchSent);

        DispatchRejected dispatchRejected = ObjectMother.dispatchRejected(dispatchSent);
        adapter.publish(dispatchRejected);

        DispatchSent dispatchSent2 = ObjectMother.dispatchSent(rideRequested);
        adapter.publish(dispatchSent2);

        DispatchAccepted dispatchAccepted = ObjectMother.dispatchAccepted(dispatchSent2);
        adapter.publish(dispatchAccepted);

        PassengerPickedUp passengerPickedUp = ObjectMother.passengerPickedUp(rideRequested, dispatchAccepted);
        adapter.publish(passengerPickedUp);

        PassengerDroppedOff passengerDroppedOff = ObjectMother.passengerDroppedOff(rideRequested, passengerPickedUp);
        adapter.publish(passengerDroppedOff);
    }

}
