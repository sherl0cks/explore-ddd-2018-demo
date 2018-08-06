package io.openinnovationlabs.cabit;

import io.openinnovationlabs.cabit.domain.dispatch.DriverRepository;
import io.openinnovationlabs.cabit.domain.dispatch.DriverRepositoryImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DispatcherApplication extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherApplication.class);

    @Override
    public void start() throws Exception {
        super.start();

        DriverRepository driverRepository = new DriverRepositoryImpl();

        new ServiceBinder(vertx).setAddress("test").register(DriverRepository.class, driverRepository);

    }
}