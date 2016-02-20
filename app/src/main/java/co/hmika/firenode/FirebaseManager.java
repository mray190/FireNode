package co.hmika.firenode;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class FirebaseManager {

    private Firebase ref;

    public FirebaseManager() {
        ref = new Firebase("https://firenodemhacks.firebaseio.com/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Log.d("FireNode","" + snap.getValue());
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }

    public void sendEvent(DataPacket packet) {
        long unixTime = System.currentTimeMillis() / 1000L;
        Firebase curr_ref = ref.child(Long.toString(unixTime));
        packet.send(curr_ref);
    }
}
