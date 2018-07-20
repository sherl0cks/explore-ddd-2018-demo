package io.openinnovationlabs.ddd.eventstore;

import io.openinnovationlabs.ddd.AggregateIdentity;
import io.openinnovationlabs.ddd.Event;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * I've moved this code from https://vertx.io/docs/vertx-mysql-postgresql-client/java/ to https://vertx.io/docs/vertx-jdbc-client/java/
 * because of https://github.com/vert-x3/vertx-mysql-postgresql-client/issues/57, and also because JDBC is supported
 * in RHOAR.
 *
 * TODO look at https://github.com/reactiverse/reactive-pg-client
 * TODO refactor this out to a generic JDBC impl
 */
public class PostgresqlAppendOnlyEventStore extends AbstractEventStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresqlAppendOnlyEventStore.class);
    private SQLClient sqlClient;

    private static final String LOAD_EVENTS_STATEMENT_FORMAT = "SELECT data FROM es_events WHERE name='%s'";
    private static final String APPEND_STATEMENT = "INSERT INTO es_events (name, getEventStreamIndex, occurred_on, data) " +
                                                        "VALUES(?, ?, cast(? as timestamp), cast(? as json))";
    private static final String CREATE_TABLE_STATEMENT =   "CREATE TABLE IF NOT EXISTS es_events( " +
                                                                "id serial primary key," +
                                                                "name text not null," +
                                                                "getEventStreamIndex integer not null," +
                                                                "occurred_on timestamp with time zone," +
                                                                "data json not null," +
                                                                "UNIQUE (name, getEventStreamIndex)" +
                                                            ");";

    // TODO handle other params
    @Override
    public void start(Future<Void> startFuture) throws Exception {

        if (config().containsKey("postgresql")) {
            sqlClient = JDBCClient.createShared(vertx, config().getJsonObject("postgresql"));

            // TODO document this config
            createEventsTableIfRequired(config().getBoolean("createTablesIfMissing", true)).setHandler(ar2
                    -> {
                if (ar2.succeeded()) {
                    initializeEventBusConsumers();
                    LOGGER.info("PostgreSQL EventStore up");
                    startFuture.complete();
                } else {
                    startFuture.fail(ar2.cause());
                }
            });

        } else {
            startFuture.fail(new IllegalStateException("postgresql property must be set on verticle config. See https://vertx.io/docs/vertx-mysql-postgresql-client/java/#_configuration"));
        }

    }


    private Future<Void> createEventsTableIfRequired(Boolean required) {
        Future<Void> future = Future.future();

        if (required) {
            sqlClient.getConnection(ar -> {
                if (ar.succeeded()) {
                    ar.result().execute(CREATE_TABLE_STATEMENT, ar2 -> {
                        if (ar2.succeeded()) {
                            LOGGER.info("Events table created or already exists");
                            future.complete();
                        } else {
                            future.fail(ar2.cause());
                        }
                        ar.result().close();
                    });
                } else {
                    ar.result().close();
                    future.fail(ar.cause());
                }
            });
        } else {
            LOGGER.info("Skipping Events table creation");
            future.complete();
        }

        return future;
    }

    /**
     * Append
     */

    @Override
    public Future<Void> append(List<Event> events) {
        Future<Void> future = Future.future();

        sqlClient.getConnection(ar -> {
            if (ar.succeeded()) {
                ar.result().batchWithParams(APPEND_STATEMENT, serializeEvents(events), ar2 -> {
                    if (ar2.succeeded()) {
                        LOGGER.debug(String.format("Successfully wrote %s events to postgresql", ar2.result()));
                        future.complete();
                    } else {
                        future.fail(ar2.cause());
                    }
                    ar.result().close();
                });
            } else {
                ar.result().close();
                future.fail(ar.cause());
            }
        });

        return future;
    }

    private List<JsonArray> serializeEvents(List<Event> events) {
        List<JsonArray> batch = new ArrayList<>();
        for (Event e : events) {
            JsonArray array = new JsonArray();
            array.add(e.getAggregateIdentity().toString());
            array.add(e.getEventStreamIndex());
            array.add(OffsetDateTime.ofInstant(e.getOccurredOn(), ZoneId.systemDefault()).toString());
            array.add(JsonObject.mapFrom(new PersistenceEnvelope(e)).toString());
            batch.add(array);
        }
        return batch;
    }

    /**
     * Load
     *
     */

    @Override
    public Future<List<Event>> loadEvents(AggregateIdentity aggregateIdentity) {
        Future<List<Event>> future = Future.future();
        sqlClient.getConnection(ar -> {
            if (ar.succeeded()) {
                ar.result().query(String.format(LOAD_EVENTS_STATEMENT_FORMAT, aggregateIdentity), ar2 -> {
                    if (ar2.succeeded()) {
                        future.complete(deserializeEvents(ar2.result()));
                    } else {
                        future.fail(ar2.cause());
                    }
                    ar.result().close();
                });
            } else {
                ar.result().close();
                future.fail(ar.cause());
            }
        });
        return future;
    }

    private List<Event> deserializeEvents(ResultSet resultSet) {

        List<Event> events = new ArrayList<>();
        for (JsonObject jsonObject : resultSet.getRows()) {
            Event e = Json.decodeValue(jsonObject.getString("data"), PersistenceEnvelope.class).event;
            events.add(e);
        }
        return events;
    }



}
