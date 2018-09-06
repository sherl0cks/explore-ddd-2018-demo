package io.openinnovationlabs.cabit.domain.dispatch;

import io.openinnovationlabs.cabit.domain.Aggregate;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Driver implements Aggregate {

    private String id;
    private DriverStatus status;

    public Driver(JsonObject jsonObject) {
        DriverConverter.fromJson(jsonObject,this);
    }

    public Driver(String id) {
        this.id = id;
        this.status = DriverStatus.LOGGED_OFF;
    }

    protected Driver() {

    }

    @Override
    public String identifier() {
        return this.id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        DriverConverter.toJson(this, json);
        return json;
    }
}
