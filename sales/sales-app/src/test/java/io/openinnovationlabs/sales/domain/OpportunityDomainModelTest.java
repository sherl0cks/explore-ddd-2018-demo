package io.openinnovationlabs.sales.domain;

import io.openinnovationlabs.ddd.Command;
import io.openinnovationlabs.ddd.CommandProcessingResponse;
import io.openinnovationlabs.sales.ObjectMother;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityCreated;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityId;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityWon;
import io.openinnovationlabs.sales.domain.opportunity.WinOpportunity;
import io.vertx.core.Future;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

/**
 * OpportunityTest
 */
public class OpportunityDomainModelTest extends AbstractDomainModelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityDomainModelTest.class);


    @Test
    public void shouldCreateAnOpportunity(TestContext context) {

        // background: these are async assertions translate setup first
        Async async = context.async();

        // given nothing

        // when
        Future<CommandProcessingResponse> responseFuture = domainModel.issueCommand(ObjectMother
                .opportunityCommand()).setHandler(ar -> {
            // then
            if (ar.succeeded()) {
                context.assertEquals(ar.result().events.size(), 1);
                context.assertTrue(ar.result().events.get(0) instanceof OpportunityCreated);
                async.complete();
            } else {
                context.fail(ar.cause());
            }
        });


        // await success
        async.awaitSuccess(2000);

    }

    @Test
    public void shouldNotRecreateAnOpportunity(TestContext context) {

        // background: these are async assertions translate setup first
        Async async = context.async();


        // given a create opportunity command that has already been processed
        Command command = ObjectMother.opportunityCommand();
        domainModel.issueCommand(command).setHandler(ar -> {
            if (ar.succeeded()) {

                // when I send the command a second time
                domainModel.issueCommand(command).setHandler(ar2 -> {
                    if (ar2.succeeded()) {
                        // then no events should be created
                        context.assertEquals(ar2.result().events.size(), 0);
                        async.complete();
                    } else {
                        context.fail(ar2.cause());
                    }
                });
            } else {
                context.fail(ar.cause());
            }
        });


        // then await success
        async.awaitSuccess(2000);
    }

    /**
     * This test will exercise the event replay functionality
     *
     * @param context
     */
    @Test
    public void shouldWinAnOpportunity(TestContext context) {

        // background: these are the async asserts we need translate setup first
        Async async = context.async();


        // given an opportunity for customer acme
        OpportunityCreated event = ObjectMother.opportunityCreated();
        domainModel.persistEvents(Arrays.asList(event)).setHandler(ar -> {

            if (ar.succeeded()) {
                // when
                domainModel.issueCommand(new WinOpportunity(event.aggregateIdentity)).setHandler(ar2 -> {

                    // then
                    if (ar2.succeeded()) {
                        context.assertEquals(ar2.result().events.size(), 1);
                        context.assertTrue(ar2.result().events.get(0) instanceof OpportunityWon);
                        async.complete();
                    } else {
                        context.fail(ar.cause());
                    }
                });
            } else {
                context.fail(ar.cause().toString());
            }

        });


        async.awaitSuccess(2000);

    }

    @Test
    public void shouldFailToWinAnOpportunityThatHasNotBeenCreated(TestContext context) {

        // background: these are the async asserts we need translate setup first
        Async async = context.async();

        // given nothing

        // when
        WinOpportunity command = new WinOpportunity(new OpportunityId(UUID.randomUUID().toString()));
        domainModel.issueCommand(command).setHandler(ar -> {

            // then
            if (ar.succeeded()) {
                context.fail("failure translate process command expected");
            } else {
                async.complete();
            }
        });

        async.awaitSuccess(2000);
    }
}