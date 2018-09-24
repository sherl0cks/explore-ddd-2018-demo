package rh.demo.kafka.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class KafkaBridgePropertiesFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBridgePropertiesFactory.class);


    public static KafkaBridgeProperties fromFile(ClassLoader classLoader, String fileName) {
        Properties properties = new Properties();

        InputStream propsFile = classLoader.getResourceAsStream(fileName);
        if (propsFile == null) {
            LOGGER.error("file not found: " + fileName);
            return null;
        } else {
            try {
                properties.load(propsFile);
            } catch (IOException e) {
                LOGGER.error("could not load " + fileName, e);
                return null;
            }
        }


        return KafkaBridgeProperties.builder().
                topics(getTopicList(properties)).
                processId(getProcessId(properties)).
                domainEventsThatStartTheProcess(getStartEvents(properties)).
                build();
    }

    public static KafkaBridgeProperties fromFile(String fileName) {
        return fromFile(LOGGER.getClass().getClassLoader(), fileName);
    }

    private static List<String> getTopicList(Properties properties) {
        String topicList = properties.getProperty("topics");
        List<String> topics;
        if (topicList == null || topicList.isEmpty()) {
            LOGGER.warn("Topic list was empty");
            topics = new ArrayList<>();
        } else {
            topics = Arrays.asList(topicList.split(","));
            LOGGER.info("Topic list: " + topics);
        }
        return topics;
    }


    private static String getProcessId(Properties properties) {
        String processId = properties.getProperty("processId");
        LOGGER.info("ProcessId: " + processId);
        return processId;
    }

    private static List<String> getStartEvents(Properties properties) {
        String startEventsString = properties.getProperty("startEvents");
        if (startEventsString == null || startEventsString.isEmpty()) {
            LOGGER.warn("StartEvents is empty! Kafka bridge cannot start any new processes!");
            return new ArrayList<>();
        } else {
            List<String> startEvents = Arrays.asList(startEventsString.split(","));
            LOGGER.info("StartEvents: " + startEvents);
            return startEvents;
        }
    }
}


