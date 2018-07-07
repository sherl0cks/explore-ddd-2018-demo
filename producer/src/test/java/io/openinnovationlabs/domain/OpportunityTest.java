package io.openinnovationlabs.domain;

import io.openinnovationlabs.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.domain.opportunity.Opportunity;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * OpportunityTest
 */
@RunWith(VertxUnitRunner.class)
public class OpportunityTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityTest.class);

    private static DomainModel domainModel;

    @BeforeClass
    public static void init(TestContext context) throws IOException {
        domainModel = new DomainModel(Vertx.vertx());
    }

    @Test
    public void shouldCreateAnOpportunity(TestContext context) {
        // given
        Async async = context.async();

        MessageConsumer<JsonObject> testConsumer = domainModel.subscribeToEventStream(Opportunity.class, "1");
        testConsumer.handler( message -> {
            LOGGER.info("message received " + message.body());
            async.complete();
        });


        // when
        domainModel.issueCommand( new CreateOpportunity("1", "test", "foo"));


        // then await success
        // there is message listener that will complete the async context when the event is received
        // else the test will fail
        async.awaitSuccess(1000);

    }

    @Test
    public void shouldDeleteAnOpportunity() {
        // TODO
    }
}