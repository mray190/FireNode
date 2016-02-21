
public class Router {
	private double gps_lat;
	private double gps_lon;
	private String name;
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
	public double getRange() {
		return range;
	}
	public void setRange(double range) {
		this.range = range;
	}
	private int num_conn;
	private double range;
}
