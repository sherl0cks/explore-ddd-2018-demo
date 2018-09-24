package org.explore.ddd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cabit.DomainEvent;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.*;
import rh.demo.kafka.ext.KafkaBridgeProperties;

import java.util.*;

public class KieServerDomainEventAdapter {
    private final String USER = System.getenv("KIE_SERVER_USER");
    private final String PASSWORD = System.getenv("KIE_SERVER_PASSWORD");
    private final String URL = System.getenv("KIE_SERVER_URL");

    private final MarshallingFormat FORMAT = MarshallingFormat.JSON;
    private final String CONTAINER_ID = "Rides_1.0.28";
    private final String PROCESS_ID = "Rides.Ride";

    private KieServicesConfiguration conf;
    private KieServicesClient kieServicesClient;
    private ProcessServicesClient processServicesClient;
    private UserTaskServicesClient userTaskServicesClient;
    private CorrelationKeyFactory correlationKeyFactory;

    private ObjectMapper objectMapper;
    private KafkaBridgeProperties properties;
    private Map<String, Long> aggregateIdToProcessId;

    public KieServerDomainEventAdapter(KafkaBridgeProperties properties) {
        Objects.requireNonNull(USER, "KIE_SERVER_USER env must be set. should default to admin.");
        Objects.requireNonNull(PASSWORD, "KIE_SERVER_PASSWORD env must be se. should default to admin.");
        Objects.requireNonNull(URL, "KIE_SERVER_URL must be set. get this from your deployed kie server. maybe in OCP?");
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
        processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
        userTaskServicesClient = kieServicesClient.getServicesClient(UserTaskServicesClient.class);

        this.properties = properties;

        correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        objectMapper = new ObjectMapper();
        aggregateIdToProcessId = new HashMap<>();
    }

    public Long publish(DomainEvent event) {

        if (properties.getDomainEventsThatStartTheProcess().contains(event.getClass().getSimpleName())) {
            handleStartProcessDomainEvent(toDataMap(event));
        } else if (properties.getDomainEventUserTaskMapping().containsKey(event.getClass().getSimpleName())) {
            handleUserTaskDomainEvent(toDataMap(event));
        } else {
            handleSignalDomainEvent(toDataMap(event));
        }

        return getCorrelatedProcessId(toDataMap(event));
    }

    private void handleStartProcessDomainEvent(Map<String, Object> eventDataMap) {
        Long processInstanceId = processServicesClient.startProcess(CONTAINER_ID, PROCESS_ID);
        putCorrelatedProcessId(eventDataMap, processInstanceId);
        handleSignalDomainEvent(eventDataMap);
    }

    private void handleUserTaskDomainEvent(Map<String, Object> eventDataMap) {
        // hack for https://issues.jboss.org/browse/JBPM-7735
        setProcessVariables(eventDataMap);

        Long userTaskId = retrieveUserTaskTaskId(eventDataMap);

        // this is the proper way to set user task data, left here for good measure.
        // it will cause a runtime exception that will be caught by the process engine and logged, but without failure
        completeUserTask(userTaskId, eventDataMap);

        handleSignalDomainEvent(eventDataMap);
    }

    private void handleSignalDomainEvent(Map<String, Object> eventDataMap) {
        processServicesClient.signal(CONTAINER_ID, formatSignalName(eventDataMap), eventDataMap);
    }


    private String formatSignalName(Map<String, Object> eventDataMap) {
        return eventDataMap.get("eventType") + "-" + getCorrelatedProcessId(eventDataMap);
    }

    private Map<String, Object> toDataMap(DomainEvent event) {
        Map<String, Object> map = objectMapper.convertValue(event, Map.class);
        map.put("eventType", event.getClass().getSimpleName());
        return map;
    }

    private Long getCorrelatedProcessId(Map<String, Object> eventDataMap) {
        return aggregateIdToProcessId.get(eventDataMap.get(properties.getDomainEventCorrelationKeyProperty()).toString());
    }

    private void putCorrelatedProcessId(Map<String, Object> eventDataMap, Long processInstanceId) {
        aggregateIdToProcessId.put(eventDataMap.get(properties.getDomainEventCorrelationKeyProperty()).toString(), processInstanceId);
    }

    private void setProcessVariables(Map<String, Object> eventDataMap) {
        Map<String, String> variableNameMapping = properties.getDomainEventUserTaskMapping().get(eventDataMap.get("eventType"));

        for (String key : variableNameMapping.keySet()) {
            processServicesClient.setProcessVariable(CONTAINER_ID, getCorrelatedProcessId(eventDataMap), key, eventDataMap.get(variableNameMapping.get(key)));
        }
    }

    // TODO what if there is more than 1 task? probably should not be by convention
    private Long retrieveUserTaskTaskId(Map<String, Object> eventDataMap) {
        List<TaskSummary> tasks = userTaskServicesClient.findTasksByStatusByProcessInstanceId(getCorrelatedProcessId(eventDataMap), Arrays.asList("Reserved", "Ready", "Created"), 0, 10);

        return tasks.get(0).getId();
    }

    private void completeUserTask(Long userTaskId, Map<String, Object> eventDataMap) {
        Map<String, String> variableNameMapping = properties.getDomainEventUserTaskMapping().get(eventDataMap.get("eventType"));

        userTaskServicesClient.startTask(CONTAINER_ID, userTaskId, "admin");
        Map<String, Object> taskData = new HashMap<>();
        for (String key : variableNameMapping.keySet()) {
            taskData.put(key, eventDataMap.get(variableNameMapping.get(key)));
        }
        userTaskServicesClient.completeTask(CONTAINER_ID, userTaskId, "admin", taskData);
    }
}
