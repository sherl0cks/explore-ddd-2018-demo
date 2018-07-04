package io.openinnovationlabs.adapters;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openinnovationlabs.ProducerConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;

public class KafkaAdapter extends AbstractVerticle {

    // TODO setup logging properly, for now everything is info
    private static Logger LOGGER = LoggerFactory.getLogger(KafkaAdapter.class);
    private KafkaProducer<String, String> kafkaProducer;

    /**
     * Initialization
     */
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        initializeKafkaProducer();
        initializeEventBusConsumers();
        startFuture.complete();
        // TODO perhaps handle a failure case here?
        LOGGER.info("initialized");
    }

    public void initializeEventBusConsumers() {
        MessageConsumer<String> messageConsumer = vertx.eventBus().consumer(ProducerConstants.KAFKA_PRODUCER_ADDRESS);
        messageConsumer.handler(message -> onTestMessage(message));
    }

    // TODO add configuration loader https://vertx.io/docs/vertx-config/java/ kube store
    public boolean initializeKafkaProducer() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");
        this.kafkaProducer = KafkaProducer.create(vertx, config);
        return true;
    }

    /**
     * Event bus consumers
     */
    public void onTestMessage(Message<String> message) {
        LOGGER.info(String.format("message received: %s", message.body()));
        KafkaProducerRecord<String, String> record = KafkaProducerRecord.create(ProducerConstants.KAFKA_PRODUCER_TOPIC, message.body());
        this.kafkaProducer.write(record, ar -> handleKafkaWriteAsyncResult(message, ar));
    }

    /**
     * AsyncResult handlers
     */
    private void handleKafkaWriteAsyncResult(Message<String> message, AsyncResult<RecordMetadata> ar) {
        if (ar.succeeded()) {
            RecordMetadata recordMetadata = ar.result();
            LOGGER.info(String.format("Message %s written on topic=%s, partition=%s, offset=%s", message.body(),
                    recordMetadata.getTopic(), recordMetadata.getPartition(), recordMetadata.getOffset()));
            message.reply("success");
        } else {
            message.fail(1, ar.cause().getMessage());
        }
    }

}