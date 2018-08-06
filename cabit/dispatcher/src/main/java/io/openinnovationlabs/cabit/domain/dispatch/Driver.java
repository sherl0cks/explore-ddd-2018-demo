package io.openinnovationlabs.cabit.domain.dispatch;

import io.openinnovationlabs.cabit.domain.Aggregate;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Driver implements Aggregate {

    private String id;

    public Driver(JsonObject jsonObject) {

    }

    public Driver(String id) {
        this.id = id;
    }

    protected Driver() {

    }

    @Override
    public String identifier() {
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        return json;
    }
}
