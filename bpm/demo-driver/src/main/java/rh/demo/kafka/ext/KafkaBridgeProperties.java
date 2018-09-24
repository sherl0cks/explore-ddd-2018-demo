package rh.demo.kafka.ext;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;


// TODO break this into more than one config file
@Data
@Builder
public class KafkaBridgeProperties {

    private List<String> topics;
    private String processId;
    private List<String> domainEventsThatStartTheProcess;
    private String domainEventCorrelationKeyProperty;
    private Map<String, Map<String, String>> domainEventUserTaskMapping;


}

