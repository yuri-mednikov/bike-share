package net.mednikov.BikeShare.bikes;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class BikeDAOImpl implements IBikeDAO {

    private MysqlDataSource dataSource;
    private static BikeDAOImpl instance;

    private BikeDAOImpl(MysqlDataSource dataSource){
        this.dataSource = dataSource;
    }

    public static BikeDAOImpl getInstance(){
        if (instance==null){
            //create datasource
            String url = System.getenv("DATABASE_URL");
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUrl(url);
            //inject DS
            //create BikeDAOImpl
            instance = new BikeDAOImpl(dataSource);
        }
        return instance;
    }

    @Override
    public List<Bike> findBikesNear(double lat, double lon) {
        return null;
    }

    @Override
    public Bike findBikeById(String id) {
        try (Connection connection = dataSource.getConnection()){

            PreparedStatement ps = connection.prepareStatement("SELECT bike_id, last_lon, last_lat, is_busy FROM bikes WHERE bike_id=?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                double lon = rs.getDouble("last_lon");
                double lat = rs.getDouble("last_lat");
                boolean isBusy = rs.getBoolean("is_busy");
                Bike bike = new Bike(id, lon, lat, isBusy);
                return bike;
            } else{
                return null;
            }

        } catch (Exception ex){
            System.out.println(ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public void add(Bike bike) {
        try (Connection connection = dataSource.getConnection()){

            PreparedStatement ps = connection.prepareStatement("INSERT INTO bikes (bike_id, last_lon, last_lat, is_busy) VALUES (?,?,?,?)");
            ps.setString(1, bike.getBikeId());
            ps.setDouble(2, bike.getLon());
            ps.setDouble(3, bike.getLat());
            ps.setBoolean(4, bike.isBusy());
            ps.executeUpdate();

        } catch (Exception ex){
            System.out.println(ex.getLocalizedMessage());
        }
    }

    @Override
    public void remove(String bikeId) {
        try (Connection connection = dataSource.getConnection()){

            PreparedStatement ps = connection.prepareStatement("DELETE FROM bikes WHERE bike_id=?");
            ps.setString(1, bikeId);
            ps.executeUpdate();

        } catch (Exception ex){
            System.out.println(ex.getLocalizedMessage());
        }
    }

    @Override
    public void update(Bike bike) {
        try (Connection connection = dataSource.getConnection()){

            PreparedStatement ps = connection.prepareStatement("UPDATE last_lon=?, last_lat=?, is_busy=? WHERE bike_id=?");
            ps.setDouble(1, bike.getLon());
            ps.setDouble(2, bike.getLat());
            ps.setBoolean(3, bike.isBusy());
            ps.setString(4, bike.getBikeId());
            ps.executeUpdate();

        } catch (Exception ex){
            System.out.println(ex.getLocalizedMessage());
        }
    }
}
