package co.hmika.firenode;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

public class FirebaseManager {

    private FireNode fireNode;
    private Firebase ref;
    public HashMap<String, Router> router_list;

    public FirebaseManager(FireNode fireNode0) {
        this.fireNode = fireNode0;
        ref = new Firebase("https://firenodemhacks.firebaseio.com/");
        ref.child("parse_data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Router new_router = dataSnapshot.getValue(Router.class);
                new_router.setBssid(dataSnapshot.getKey());
                Log.d("FireNode", new_router.toString());
                fireNode.router_list.put(new_router.getBssid(), new_router);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("FireNode", "Child changed: " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("FireNode", "Child removed: " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    public void sendEvent(DataPacket packet) {
        long unixTime = System.currentTimeMillis() / 1000L;
        Firebase curr_ref = ref.child("raw_data").child(Long.toString(unixTime)).child(packet.wifi_bssid);
        curr_ref.setValue(packet);
    }


}
