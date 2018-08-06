package io.openinnovationlabs.cabit.domain.dispatch;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverRepositoryImpl implements DriverRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverRepositoryImpl.class);

    @Override
    public void load(String aggregateIdentifier, Handler<AsyncResult<Driver>> resultHandler) {
        LOGGER.info("load");
        resultHandler.handle(Future.succeededFuture(new Driver()));
    }

    @Override
    public void save(Driver aggregate, Handler<AsyncResult<Void>> resultHandler) {
        LOGGER.info("save");
        resultHandler.handle(Future.succeededFuture());
    }
}
