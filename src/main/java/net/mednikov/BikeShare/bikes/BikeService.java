package net.mednikov.BikeShare.bikes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.UUID;

public class BikeService extends AbstractVerticle {

    private IBikeDAO dao;

    public BikeService(IBikeDAO dao){
        this.dao = dao;
    }

    @Override
    public void start() throws Exception {
        super.start();
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("bikes.one").handler(message->{
            JsonObject payload = JsonObject.mapFrom(message.body());
            String bikeId = payload.getString("bike_id");

            vertx.executeBlocking(future->{
                Bike bike = dao.findBikeById(bikeId);
                if (bike==null){
                    future.fail("Nothing found");
                } else {
                    future.complete(JsonObject.mapFrom(bike));
                }
            }, result->{
                if (result.succeeded()){
                    message.reply(result.result());
                } else {
                    message.fail(404, "Nothing found");
                }
            });
        });
        eventBus.consumer("bikes.near").handler(message -> {
            JsonObject payload = JsonObject.mapFrom(message.body());
            double lon = payload.getDouble("lon");
            double lat = payload.getDouble("lat");
            vertx.executeBlocking(future->{
                List<Bike> bikes = dao.findBikesNear(lat, lon);
                if (bikes!=null){
                    future.complete(JsonObject.mapFrom(bikes));
                } else {
                    future.fail("Nothing found");
                }
            }, result->{
                if (result.succeeded()){
                    message.reply(result.result());
                } else {
                    message.fail(404, "Nothing found");
                }
            });
        });
        eventBus.consumer("bikes.add").handler(message -> {
            JsonObject payload = JsonObject.mapFrom(message.body());
            Bike bike = Json.decodeValue(payload.toString(), Bike.class);
            String bikeId = UUID.randomUUID().toString();
            bike.setBikeId(bikeId);
            vertx.executeBlocking(future->dao.add(bike),
                    result->System.out.println("Bike added"));
        });

        eventBus.consumer("bikes.remove").handler(message -> {
            JsonObject payload = JsonObject.mapFrom(message.body());
            String bikeId = payload.getString("bike_id");
            vertx.executeBlocking(future->dao.remove(bikeId),
                    result->System.out.println("Bike removed"));
        });
        eventBus.consumer("bikes.update").handler(message -> {
            JsonObject payload = JsonObject.mapFrom(message.body());
            Bike bike = Json.decodeValue(payload.toString(), Bike.class);
            vertx.executeBlocking(future->dao.update(bike),
                    result->System.out.println("Bike update"));
        });
    }
}
