package co.hmika.firenode;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by mray on 2/20/16.
 */
public class FirebaseManager {
    private Firebase ref;

    public FirebaseManager() {
        ref = new Firebase("https://firenodemhacks.firebaseio.com/");
        // Listen for realtime changes
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Log.d("FireNode","" + snap.getValue());
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }

    public void sendEvent() {
        long unixTime = System.currentTimeMillis() / 1000L;
        Firebase curr_ref = ref.child(Long.toString(unixTime));
        curr_ref.child("user_id").setValue(23);
        curr_ref.child("gps_lat").setValue(42.3245);
        curr_ref.child("gps_lon").setValue(-83.1345);
        curr_ref.child("gps_acc").setValue(15);
        curr_ref.child("wifi_strength").setValue(43.4);
        curr_ref.child("wifi_mac").setValue("A3-FF-34-23-FB-25");
        curr_ref.child("wifi_bssid").setValue("3FAB354E39");
        curr_ref.child("wifi_gate").setValue("idk what this is");
    }
}
