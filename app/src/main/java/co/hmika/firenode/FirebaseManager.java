package co.hmika.firenode;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FirebaseManager {

    private FireNode fireNode;
    private Firebase ref;

    public FirebaseManager(final FireNode fireNode0) {
        this.fireNode = fireNode0;
        ref = new Firebase("https://firenodemhacks.firebaseio.com/");
        ref.child("parse_data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Router new_router = dataSnapshot.getValue(Router.class);
                new_router.setBssid(dataSnapshot.getKey());
                new_router.setMarker(new MarkerOptions().position(new LatLng(new_router.getGps_lat(),
                        new_router.getGps_lon())).title(new_router.getName()));
                fireNode.router_list.put(new_router.getBssid(), new_router);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Router new_router = dataSnapshot.getValue(Router.class);
                new_router.setBssid(dataSnapshot.getKey());
                new_router.setMarker(new MarkerOptions().position(new LatLng(new_router.getGps_lat(),
                        new_router.getGps_lon())).title(new_router.getName()));
                fireNode.router_list.put(new_router.getBssid(), new_router);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                fireNode.router_list.remove(dataSnapshot.getKey());
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
