package co.hmika.firenode;

import com.firebase.client.Firebase;

public class FirebaseManager {

    private Firebase ref;

    public FirebaseManager() {
        ref = new Firebase("https://firenodemhacks.firebaseio.com/");
    }

    public void sendEvent(DataPacket packet) {
        long unixTime = System.currentTimeMillis() / 1000L;
        Firebase curr_ref = ref.child("raw_data").child(Long.toString(unixTime)).child(packet.wifi_bssid);
        curr_ref.setValue(packet);
    }
}
