package io.vertx.example;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

    MongoClient mongo; 
    DBUtils db;
    @Override
    public void start() {
        final Router routes = Router.router(vertx);
        routes.route().handler(BodyHandler.create());

        routes.route().handler(rc -> {
            final String name = rc.request().getHeader("SKEY");
            if (name != null && "12345fly".contentEquals(name)) {
                rc.next();
            } else {
                rc.response().putHeader(HttpHeaders.ACCEPT, HttpHeaderValues.APPLICATION_JSON).setStatusCode(403)
                        .setStatusMessage("Not allowed")
                        .end(new JsonObject().put("error", "Insufficient privilidges").encode());
            }
        });
        // Database test
        final JsonObject config = new JsonObject()
                // .put("keepAlive", true)
                .put("db_name", "demo").put("connection_string", "mongodb://localhost:27017");
        System.setProperty("org.mongodb.async.type", "netty");
        this.mongo = MongoClient.createShared(vertx, config);

        routes.get("/").handler(this::home);
        routes.route("/testSave").handler(BodyHandler.create());
        routes.post("/testSave").handler(this::testSave);
        vertx.createHttpServer().requestHandler(routes).listen(8000);
    }

    void home(final RoutingContext rc) {
        rc.response().setStatusCode(200).setStatusMessage("OK").end(new JsonObject().put("message", "OK").encode());
    }

    void testSave(final RoutingContext rc) {
        final JsonObject bodyQuery = rc.getBodyAsJson();
        System.out.println(bodyQuery);
        // this.db.insertOne("users", this.mongo, bodyQuery,HttpServerResponse resp);
        
        this.mongo.save("users", bodyQuery, res -> {
        if (res.succeeded()) {
          String id = res.result();
          System.out.println("Saved book with id " + id);
            rc.response()
                .setStatusCode(200)
                .setStatusMessage("success")
                .end(bodyQuery.encodePrettily());
        } else {
            rc.response()
            .setStatusCode(400)
            .setStatusMessage("failed")
            .end(new JsonObject().put("Payload", res.cause().getMessage()).encode());
        }
      });
    }
}
