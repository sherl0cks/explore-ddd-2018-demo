package io.openinnovationlabs.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.openinnovationlabs.ddd.DomainModel;
import io.openinnovationlabs.ddd.eventstore.InMemoryAppendOnlyEventStoreForTests;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;



@SpringBootApplication
public class SalesApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesApplication.class);
    @Autowired
    public Vertx vertx;

    public static void main(String[] args) {
        SpringApplication.run(SalesApplication.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        deployEventStore(deploymentOptions());
    }

    private void deployEventStore(DeploymentOptions options) {
        vertx.deployVerticle(InMemoryAppendOnlyEventStoreForTests.class, options, ar -> {
            if (ar.failed()) {
                LOGGER.error(ar.cause().toString());
            } else {
                LOGGER.info("Event Store is up");
            }
        });
    }


    private DeploymentOptions deploymentOptions() {
        DeploymentOptions options = new DeploymentOptions();
        JsonObject config = new JsonObject().put("appendOnlyStoreType", "InMemory");
        options.setConfig(config);
        return options;
    }

    @Bean
    public Vertx vertxProvider() {
        return Vertx.vertx();
    }

    @Bean
    public DomainModel domainModelProvider(Vertx vertx) {
        return new DomainModel(vertx);
    }


    @Bean
    public JacksonJsonProvider jsonProvider(){
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
        return jsonProvider;
    }

}