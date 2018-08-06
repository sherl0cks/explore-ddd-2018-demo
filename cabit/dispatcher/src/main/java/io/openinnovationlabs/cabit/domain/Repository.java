package io.openinnovationlabs.cabit.domain;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Repository<T extends Aggregate> {

    void load(String aggregateIdentifier, Handler<AsyncResult<T>> resultHandler);

    void save(T aggregate, Handler<AsyncResult<Void>> resultHandler);
}
