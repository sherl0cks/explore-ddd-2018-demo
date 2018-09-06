package io.openinnovationlabs.cabit.domain.dispatch;


import io.openinnovationlabs.cabit.domain.Repository;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
public interface DriverRepository  {

    void load(String aggregateIdentifier, Handler<AsyncResult<Driver>> resultHandler);

    void save(Driver aggregate, Handler<AsyncResult<Void>> resultHandler);
}
