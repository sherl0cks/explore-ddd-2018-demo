package io.openinnovationlabs.cabit.domain;

import io.openinnovationlabs.cabit.domain.dispatch.DriverRepository;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.junit.Test;

public class DispatcherDomainModelTest extends AbstractDomainModelTest {

    @Test
    public void hi(TestContext testContext) {
        Async async = testContext.async();
        DriverRepository repository = new ServiceProxyBuilder(vertx).setAddress("test").build(DriverRepository.class);
        repository.load("hi", ar -> {
            if (ar.succeeded()) {
                async.complete();
            } else {
                testContext.fail(ar.cause().getMessage());
            }
        });

    }
}
