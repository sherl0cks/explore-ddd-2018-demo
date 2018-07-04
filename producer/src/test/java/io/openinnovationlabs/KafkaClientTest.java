package io.openinnovationlabs;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import io.debezium.kafka.KafkaCluster;
import io.debezium.util.Testing;
import io.openinnovationlabs.adapters.KafkaAdapter;
import io.openinnovationlabs.application.ConfigurationProvider;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class KafkaClientTest {

    private static Vertx vertx;
    private static KafkaCluster kafkaCluster;

    @BeforeClass
    public static void init(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        vertx.deployVerticle(KafkaAdapter.class.getName(), context.asyncAssertSuccess());
        spinUpKafka();
    }

    @AfterClass
    public static void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
        kafkaCluster.shutdown();
    }

    public static void spinUpKafka() throws IOException {
        // Kafka setup for the example
        File dataDir = Testing.Files.createTestingDirectory("cluster");
        dataDir.deleteOnExit();

        JsonObject kafkaBrokerConfig = ConfigurationProvider.CONFIG.getJsonObject("kafka.broker");
        kafkaCluster = new KafkaCluster().usingDirectory(dataDir)
                .withPorts(kafkaBrokerConfig.getInteger("zookeeper.port"),
                        kafkaBrokerConfig.getInteger("kafka.firstPort"))
                .addBrokers(kafkaBrokerConfig.getInteger("kafka.brokers")).deleteDataPriorToStartup(true).startup();

        kafkaCluster.createTopic("kafka-test-topic", 1, 1);

    }

    @Test
    public void shouldSendARecordToKafka(TestContext context) {
        for (int i = 0; i < 10; i++) {
            Async async = context.async();
            vertx.eventBus().send(ProducerConstants.KAFKA_PRODUCER_ADDRESS,
                    "test " + String.valueOf(System.currentTimeMillis()), ar -> {
                        if (ar.failed()) {
                            context.fail(ar.cause());
                        }
                        async.complete();
                    });
            async.awaitSuccess();
        }
    }
}