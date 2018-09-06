package io.openinnovationlabs.cabit.domain;

import io.openinnovationlabs.cabit.domain.dispatch.Driver;
import io.openinnovationlabs.cabit.domain.dispatch.DriverRepository;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.junit.Assert;
import org.junit.Test;

public class DispatcherDomainModelTest extends AbstractDomainModelTest {

    @Test
    public void shouldSaveAndRetrieveADriver(TestContext testContext) {
        Async async = testContext.async();

        // given
        DriverRepository repository = new ServiceProxyBuilder(vertx).setAddress("test").build(DriverRepository.class);
        Driver driver = new Driver("1");
        repository.save(driver, ar -> {
            if ( ar.succeeded() ){
                // when
                repository.load("1", ar2 -> {
                    if (ar2.succeeded()) {
                        Assert.assertNotNull(ar2.result());
                        async.complete();
                    } else {
                        testContext.fail(ar2.cause().getMessage());
                    }
                });
            }
        });



    }
}
