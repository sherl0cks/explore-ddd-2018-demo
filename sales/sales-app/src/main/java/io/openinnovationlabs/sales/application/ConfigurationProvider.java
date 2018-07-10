package io.openinnovationlabs.sales.application;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * ConfigurationProvider
 */
public class ConfigurationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProvider.class);
    public static final JsonObject CONFIG = retrieveConfig();

    private static JsonObject retrieveConfig() {
        String kubeNamespace = System.getenv("KUBERNETES_NAMESPACE");
        if (kubeNamespace == null || kubeNamespace.isEmpty()) {
            return retrieveConfigForLocalEnvironment();
        } else {
            return retrieveConfigForOpenShift();
        }
    }

    private static JsonObject retrieveConfigForLocalEnvironment() {
        LOGGER.info("loading configuration for local environment");

        JsonObject config = new JsonObject();

        JsonObject kafkaProducerConfig = new JsonObject();
        kafkaProducerConfig.put("bootstrap.servers", "localhost:9092");
        kafkaProducerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerConfig.put("acks", "1");
        config.put("kafka.producer", kafkaProducerConfig);

        JsonObject kafkaBrokerConfig = new JsonObject();
        kafkaBrokerConfig.put("zookeeper.port", 2181);
        kafkaBrokerConfig.put("kafka.firstPort", 9092);
        kafkaBrokerConfig.put("kafka.brokers", 1);
        config.put("kafka.broker", kafkaBrokerConfig);
        return config;
    }


    // TODO add configuration loader https://vertx.io/docs/vertx-config/java/ kube
    private static JsonObject retrieveConfigForOpenShift() {
        throw new RuntimeException("todo");
    }

    public static Map<String, String> getKafkaBrokerConfigAsMap() {
        Map<String, String> map = new HashMap<>();
        for (String key : getKafkaBrokerConfig().getMap().keySet()) {
            map.put(key, getKafkaBrokerConfig().getString(key));
        }
        return map;
    }

    public static JsonObject getKafkaBrokerConfig() {
        return CONFIG.getJsonObject("kafka.producer");
    }
}