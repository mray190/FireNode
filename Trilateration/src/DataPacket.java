public class DataPacket {
    private String userID;
    private double gps_lat;
    private double gps_lon;
    private double gps_acc;
    private double wifi_strength;
    private String wifi_bssid;
    private int wifi_freq;
    private String wifi_ssid;
    private double wifi_dist;
    private String wifi_ip;
    public String getWifi_ip() {
		return wifi_ip;
	}
	public void setWifi_ip(String wifi_ip) {
		this.wifi_ip = wifi_ip;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
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
	public double getGps_acc() {
		return gps_acc;
	}
	public void setGps_acc(double gps_acc) {
		this.gps_acc = gps_acc;
	}
	public double getWifi_strength() {
		return wifi_strength;
	}
	public void setWifi_strength(double wifi_strength) {
		this.wifi_strength = wifi_strength;
	}
	public String getWifi_bssid() {
		return wifi_bssid;
	}
	public void setWifi_bssid(String wifi_bssid) {
		this.wifi_bssid = wifi_bssid;
	}
	public int getWifi_freq() {
		return wifi_freq;
	}
	public void setWifi_freq(int wifi_freq) {
		this.wifi_freq = wifi_freq;
	}
	public String getWifi_ssid() {
		return wifi_ssid;
	}
	public void setWifi_ssid(String wifi_ssid) {
		this.wifi_ssid = wifi_ssid;
	}
	public double getWifi_dist() {
		return wifi_dist;
	}
	public void setWifi_dist(double wifi_dist) {
		this.wifi_dist = wifi_dist;
	}
	
	@Override
	public boolean equals(Object other) {
		return (((DataPacket)other).gps_lat==gps_lat && ((DataPacket)other).gps_lon==gps_lon && ((DataPacket)other).wifi_dist==wifi_dist);
	}
}
