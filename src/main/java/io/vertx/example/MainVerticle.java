package io.vertx.example;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router routes = Router.router(vertx);
        routes.route().handler(BodyHandler.create());
        
        routes.route().handler(rc -> { 
                String name = rc.request().getHeader("SKEY");
                if ( name!=null && "12345fly".contentEquals(name) ) {
                        rc.next();
                } else {
                    rc.response()
                        .putHeader(HttpHeaders.ACCEPT,
                            HttpHeaderValues.APPLICATION_JSON)
                        .setStatusCode(403)
                        .setStatusMessage("Not allowed")
                        .end(new JsonObject().put("error", "Insufficient privilidges")
                        .encode());
                }
        });
        routes.get().handler(this::home);
        vertx.createHttpServer().requestHandler(routes).listen(8000);
    }

    void home(RoutingContext rc) { 
        rc.response()
            .setStatusCode(200)
            .setStatusMessage("OK")
            .end(new JsonObject().put("message", "OK").encode());
            
    }

}
