package io.openinnovationlabs.domain;

import io.openinnovationlabs.adapters.InMemoryAppendOnlyStore;
import io.openinnovationlabs.domain.eventstore.EventStore;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public abstract class AbstractDomainModelTest {

    protected static DomainModel domainModel;
    protected static Vertx vertx;

    @BeforeClass
    public static void init(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        domainModel = new DomainModel(vertx);

        JsonObject jsonObject = new JsonObject().put("appendOnlyStoreType", "InMemory");
        vertx.deployVerticle(EventStore.class, new DeploymentOptions().setConfig(jsonObject));
    }

    @AfterClass
    public static void cleanUp(TestContext context) {
        domainModel.close(context.asyncAssertSuccess());
    }

    @After
    public void afterTest() {
        vertx.eventBus().send("EventStore-Clear", new JsonObject());
    }

    public boolean messageBodyOfType(Message<JsonObject> message, Class type) {
        return message.headers().get("eventClassname").equals(type.getName());
    }

}
