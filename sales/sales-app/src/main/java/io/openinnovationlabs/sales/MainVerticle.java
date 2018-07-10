package io.openinnovationlabs.sales;

import io.openinnovationlabs.ddd.eventstore.EventStore;
import io.openinnovationlabs.sales.adapters.HttpAdapter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;


public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        DeploymentOptions options = deploymentOptions();
        deployEventStore(options).compose(v -> deployHttpAdapter(options)).setHandler(startFuture.completer());
    }

    private Future deployEventStore(DeploymentOptions options) {
        Future future = Future.future();
        vertx.deployVerticle(EventStore.class, options, future.completer());
        return future;
    }

    private Future deployHttpAdapter(DeploymentOptions options) {
        Future future = Future.future();
        vertx.deployVerticle(HttpAdapter.class, options, future.completer());
        return future;
    }

    private DeploymentOptions deploymentOptions() {
        DeploymentOptions options = new DeploymentOptions();
        JsonObject config = new JsonObject().put("appendOnlyStoreType", "InMemory");
        options.setConfig(config);
        return options;
    }
}