package co.hmika.firenode;

import com.firebase.client.Firebase;

public class DataPacket {
    public int userID;
    public double gps_lat;
    public double gps_lon;
    public double gps_acc;
    public double wifi_strength;
    public String wifi_mac;
    public String wifi_bssid;
    public String wifi_gate;

    public DataPacket() { }

    public void send(Firebase curr_ref) {
        curr_ref.child("user_id").setValue(userID);
        curr_ref.child("gps_lat").setValue(gps_lat);
        curr_ref.child("gps_lon").setValue(gps_lon);
        curr_ref.child("gps_acc").setValue(gps_acc);
        curr_ref.child("wifi_strength").setValue(wifi_strength);
        curr_ref.child("wifi_mac").setValue(wifi_mac);
        curr_ref.child("wifi_bssid").setValue(wifi_bssid);
        curr_ref.child("wifi_gate").setValue(wifi_gate);
    }
}
