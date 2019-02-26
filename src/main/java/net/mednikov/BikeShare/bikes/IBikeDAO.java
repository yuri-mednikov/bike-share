package net.mednikov.BikeShare.bikes;

import java.util.List;

public interface IBikeDAO {

    List<Bike> findBikesNear (double lat, double lon);

    Bike findBikeById (String id);

    void add (Bike bike);

    void remove (String bikeId);

    void update (Bike bike);
}
