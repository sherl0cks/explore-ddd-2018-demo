package io.openinnovationlabs.domain;

import io.openinnovationlabs.application.SimpleEventLogger;
import io.openinnovationlabs.domain.opportunity.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * OpportunityTest
 */
public class OpportunityModelTest extends AbstractDomainModelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityModelTest.class);


    @Test
    public void shouldCreateAnOpportunity(TestContext context) {

        // background: these are async assertions to setup first
        Async async = context.async();

        OpportunityId opportunityId = new OpportunityId("1");
        MessageConsumer<JsonObject> testConsumer = domainModel.subscribeToEventStream(opportunityId);
        testConsumer.handler(message -> {
            if (messageBodyOfType(message, OpportunityCreated.class)) {
                LOGGER.info(SimpleEventLogger.log(message.body().mapTo(OpportunityCreated.class)));
                async.complete();
            }
        });

        // given nothing


        // when
        domainModel.issueCommand(new CreateOpportunity(new OpportunityId("1"), "test", "residency"));


        // then await success
        // there is message listener that will complete the async context when the event is received
        // else the test will fail
        async.awaitSuccess(2000);

    }

    /**
     * This test will exercise the event replay functionality
     *
     * @param context
     */
    @Test
    public void shouldWinAnOpportunity(TestContext context) {

        // background: these are the async asserts we need to setup first
        Async async = context.async();

        OpportunityId opportunityId = new OpportunityId("1");
        MessageConsumer<JsonObject> testConsumer = domainModel.subscribeToEventStream(opportunityId);
        testConsumer.handler(message -> {
            if (messageBodyOfType(message, OpportunityWon.class)) {
                LOGGER.info(SimpleEventLogger.log(message.body().mapTo(OpportunityWon.class)));

                domainModel.loadEvents(opportunityId, ar -> {
                    if (ar.succeeded()) {
                        Assert.assertEquals(2, ar.result().events.size());
                    }
                    async.complete();
                });
            }

        });

        // given an opportunity for customer acme
        OpportunityCreated event = new OpportunityCreated(new OpportunityId("1"), "acme", "residency", Instant.now().minus(1, ChronoUnit.HOURS).toString(), 0);
        domainModel.persistEvents(Arrays.asList(event), ar -> {
            if (ar.succeeded()){
                // when we win the opportunity
                domainModel.issueCommand(new WinOpportunity(new OpportunityId("1")));

                // then create an opportunity won event
                // and make sure the proper events were persisted
                async.awaitSuccess(2000);
            }
        });






    }
}