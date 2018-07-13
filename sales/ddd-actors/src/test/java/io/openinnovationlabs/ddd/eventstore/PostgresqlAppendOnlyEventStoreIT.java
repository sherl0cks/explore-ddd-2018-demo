package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.DomainModel;
import io.openinnovationlabs.ddd.Event;
import io.openinnovationlabs.ddd.domain.test.TestAggregate;
import io.openinnovationlabs.ddd.domain.test.TestEvent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * tested locally using https://hub.docker.com/r/centos/postgresql-10-centos7/
 * docker run -d --name postgresql -e POSTGRESQL_USER=user -e POSTGRESQL_PASSWORD=pass -e POSTGRESQL_DATABASE=db -p 5432:5432 rhscl/postgresql-10-rhel7
 */
@RunWith(VertxUnitRunner.class)
public class PostgresqlAppendOnlyEventStoreIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresqlAppendOnlyEventStoreIT.class);
    private static Vertx vertx;
    private static DomainModel domainModel;

    @BeforeClass
    public static void init(TestContext context) {
        vertx = Vertx.vertx();
        domainModel = new DomainModel(vertx);
    }

    @Test
    public void shouldAppendAListOfEventsAndThenRetrieveIt(TestContext context) {

        Async async = context.async();

        // given a list of events
        List<Event> events = generateListOfEvents();

        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("postgresql", getPostgresqlConfig()));

        vertx.deployVerticle(PostgresqlAppendOnlyEventStore.class, options, ar -> {

            // when I persist the events
            domainModel.persistEvents(events).setHandler(ar2 -> {
                // then make sure they persist successfully, which we know happens is the async result succeeded
                if (ar2.succeeded()) {

                    // when I retrieve those events
                    domainModel.loadEvents(events.get(0).aggregateIdentity()).setHandler( ar3 -> {
                        if ( ar3.succeeded() ){
                            List<Event> retrievedEvents = ar3.result().events;
                            for (int i=0; i<10; i++){
                                // then make sure I get the right list back
                                context.assertEquals(retrievedEvents.get(i), events.get(i));
                            }
                            async.complete();
                        }
                    });
                } else {
                    context.fail(ar2.cause());
                }
            });
        });

        // then
        async.awaitSuccess(5000);
    }

    private List<Event> generateListOfEvents() {
        List<Event> events = new ArrayList<>();
        String aggregateId = String.valueOf(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            TestEvent event = new TestEvent(
                    new AggregateIdentity(aggregateId, TestAggregate.class),
                    Instant.now().toString(),
                    i,
                    "test" + String.valueOf(i)
            );
            events.add(event);
        }

        return events;
    }

    private JsonObject getPostgresqlConfig() {
        JsonObject config = new JsonObject();
        config.put("url", "jdbc:postgresql://127.0.0.1:5432/db");
        config.put("user", "user");
        config.put("password", "pass");
        config.put("database", "db");
        return config;
    }

}
