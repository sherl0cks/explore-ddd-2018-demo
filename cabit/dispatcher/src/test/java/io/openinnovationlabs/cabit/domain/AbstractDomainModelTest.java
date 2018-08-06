package io.openinnovationlabs.cabit.domain;

import io.openinnovationlabs.cabit.DispatcherApplication;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

/**
 * perhaps we shouldn't be creating vertx on each test? so far no issues with that and removes and state bleed issues
 */
@RunWith(VertxUnitRunner.class)
public abstract class AbstractDomainModelTest {

    @ClassRule
    public static RunTestOnContext rule = new RunTestOnContext();
    public static Vertx vertx;

    @BeforeClass
    public static void init(TestContext context) {
        vertx = rule.vertx();
        vertx.deployVerticle(DispatcherApplication.class.getName(), ar -> {
            if (ar.failed()){
                context.fail(ar.result());
            }
        });
    }

    @After
    public void afterTest(TestContext context) {
    }

    @Before
    public void beforeTest(TestContext context) {


    }


}
