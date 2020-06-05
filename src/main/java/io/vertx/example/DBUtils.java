package io.vertx.example;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class DBUtils {
    void insertOne(String table,MongoClient mongo, JsonObject body, final HttpServerResponse resp ) {
        System.out.println("table");
        mongo.insert(table, body, res -> {
                if (res.succeeded()) { 
                    resp.end(new JsonObject()
                            .put("result",res.result()).encode());
                } else {
                    resp.end(new JsonObject()
                            .put("result",res.result()).encode());
                }
          });
    }
}