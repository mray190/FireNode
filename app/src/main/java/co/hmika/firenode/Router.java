package co.hmika.firenode;

/**
 * Created by mray on 2/20/16.
 */
public class Router {
    private String bssid;
    private double gps_lat;
    private double gps_lon;
    private String name;
    private int num_conn;

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public double getGps_lat() {
        return gps_lat;
    }

    public void setGps_lat(double gps_lat) {
        this.gps_lat = gps_lat;
    }

    public double getGps_lon() {
        return gps_lon;
    }

    public void setGps_lon(double gps_lon) {
        this.gps_lon = gps_lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum_conn() {
        return num_conn;
    }

    public void setNum_conn(int num_conn) {
        this.num_conn = num_conn;
    }

    private double range;

    @Override
    public String toString() {
        return "BSSID: " + bssid + " Lat: " + gps_lat + " Lon: " + gps_lon + " Name: " + name + " Num Connections: " + num_conn + " Range: " + range;
    }
}
