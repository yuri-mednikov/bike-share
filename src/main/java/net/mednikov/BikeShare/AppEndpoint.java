package net.mednikov.BikeShare;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import net.mednikov.BikeShare.bikes.BikeDAOImpl;
import net.mednikov.BikeShare.bikes.BikeService;

public class AppEndpoint extends AbstractVerticle {

    private int port;

    public AppEndpoint(){

        if (System.getenv("PORT")!=null){
            this.port = Integer.valueOf(System.getenv("PORT"));
        } else {
            this.port = 4567;
        }
    }

    @Override
    public void start() throws Exception {
        super.start();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        EventBus eventBus = vertx.eventBus();

        router.get("/bike/:id").handler(ctx->{
            String bikeId = ctx.pathParam("id");
            JsonObject msg = new JsonObject().put("bike_id", bikeId);
            eventBus.send("bikes.one", msg, result->{
                if (result.succeeded()){
                    //return to user
                    ctx.response().setStatusCode(200).end(Json.encodePrettily(result.result().body()));
                } else {
                    //display 404 error
                    ctx.response().setStatusCode(404).end();
                }
            });
        });

        router.get("/bikes/:lon/:lat").handler(ctx->{
            double lon = Double.valueOf(ctx.pathParam("lon"));
            double lat = Double.valueOf(ctx.pathParam("lat"));
            JsonObject msg = new JsonObject().put("lat",lat).put("lon",lon);
            eventBus.send("bikes.near", msg, result->{
                if (result.succeeded()){
                    ctx.response().setStatusCode(200).end(Json.encodePrettily(result.result().body()));
                } else {
                    ctx.response().setStatusCode(404).end();
                }
            });
        });

        router.route("/bikes").handler(BodyHandler.create());

        router.post("/bikes").handler(ctx->{
            JsonObject payload = ctx.getBodyAsJson();
            eventBus.send("bikes.add", payload);
        });

        router.put("/bikes").handler(ctx->{
            JsonObject payload = ctx.getBodyAsJson();
            eventBus.send("bikes.update", payload);
        });

        router.delete("/bikes/:id").handler(ctx->{
            String bikeId = ctx.pathParam("id");
            JsonObject msg = new JsonObject().put("bike_id", bikeId);
            eventBus.send("bikes.remove", msg);
        });

        server.requestHandler(router).listen(port, res->{
            if (res.succeeded()){
                System.out.println("Server is created");
            } else {
                System.out.println(res.cause().getLocalizedMessage());
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new BikeService(BikeDAOImpl.getInstance()));
        vertx.deployVerticle(new AppEndpoint());
    }
}
