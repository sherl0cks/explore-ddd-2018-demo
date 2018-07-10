package io.openinnovationlabs.domain;

import io.openinnovationlabs.domain.eventstore.EventStore;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * perhaps we shouldn't be creating vertx on each test? so far no issues with that and removes and state bleed issues
 */
@RunWith(VertxUnitRunner.class)
public abstract class AbstractDomainModelTest {

    protected static DomainModel domainModel;
    protected static Vertx vertx;


    @After
    public void afterTest(TestContext context) {
        domainModel.close(context.asyncAssertSuccess());
    }

    @Before
    public void beforeTest(TestContext context) {
        vertx = Vertx.vertx();
        domainModel = new DomainModel(vertx);
        JsonObject jsonObject = new JsonObject().put("appendOnlyStoreType", "InMemory");
        vertx.deployVerticle(EventStore.class, new DeploymentOptions().setConfig(jsonObject), context.asyncAssertSuccess());
    }

    public boolean messageBodyOfType(Message<JsonObject> message, Class type) {
        return message.headers().get("eventClassname").equals(type.getName());
    }

}
