package io.openinnovationlabs.cabit.domain.dispatch;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DriverRepositoryImpl implements DriverRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverRepositoryImpl.class);

    private Map<String, Driver> data = new HashMap<>();


    @Override
    public void load(String aggregateIdentifier, Handler<AsyncResult<Driver>> resultHandler) {
        LOGGER.debug("load " + aggregateIdentifier);
        if (data.containsKey(aggregateIdentifier)){
            resultHandler.handle(Future.succeededFuture(data.get(aggregateIdentifier)));
        } else {
            resultHandler.handle(Future.failedFuture("Driver " + aggregateIdentifier + " not found"));
        }

    }

    @Override
    public void save(Driver aggregate, Handler<AsyncResult<Void>> resultHandler) {
        LOGGER.debug("save " + aggregate.identifier() );
        data.put(aggregate.identifier(), aggregate);
        resultHandler.handle(Future.succeededFuture());
    }
}
