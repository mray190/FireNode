package co.hmika.firenode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class FirebaseManager {

    private FireNode fireNode;
    private Firebase ref;

    public void notifyUser(String notificationTitle, String notificationText) {
        Intent intent = new Intent(fireNode, Home.class);
        PendingIntent contentIntent = PendingIntent.getActivity(fireNode, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(fireNode);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setTicker("FireNode")
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("FireNode");


        NotificationManager notificationManager = (NotificationManager) fireNode.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }

    public FirebaseManager(final FireNode fireNode0) {
        this.fireNode = fireNode0;
        ref = new Firebase("https://firenodemhacks.firebaseio.com/");
        ref.child("notifications").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CustomNotification customNotification = dataSnapshot.getValue(CustomNotification.class);
                notifyUser(customNotification.getTitle(),customNotification.getText());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CustomNotification customNotification = dataSnapshot.getValue(CustomNotification.class);
                notifyUser(customNotification.getTitle(),customNotification.getText());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
        ref.child("parse_data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Router new_router = dataSnapshot.getValue(Router.class);
                new_router.setBssid(dataSnapshot.getKey());
                if (new_router.getGps_lat() == 0 || new_router.getGps_lon() == 0 || new_router.getRange() == 0)
                    return;
                if (fireNode.map != null) {
                    new_router.setCircle(fireNode.map.addCircle(new CircleOptions().center(new LatLng(new_router.getGps_lat(),
                            new_router.getGps_lon())).radius(new_router.getRange())
                            .strokeColor(Color.GREEN)
                            .strokeWidth(1)
                            .fillColor(Color.parseColor("#3300ff00"))));
                }
                fireNode.router_list.put(new_router.getBssid(), new_router);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Router new_router = dataSnapshot.getValue(Router.class);
                new_router.setBssid(dataSnapshot.getKey());
                if (new_router.getGps_lat() == 0 || new_router.getGps_lon() == 0 || new_router.getRange() == 0)
                    return;
                if (fireNode.map != null) {
                    new_router.setCircle(fireNode.router_list.get(new_router.getBssid()).getCircle());
                    new_router.getCircle().setRadius(new_router.getRange());
                    new_router.getCircle().setFillColor(Color.parseColor("#3300ff00"));
                    MarkerAnimation.animateMarkerToICS(fireNode.router_list.get(new_router.getBssid()).getCircle(), new LatLng(new_router.getGps_lat(),
                            new_router.getGps_lon()), new LatLngInterpolator() {
                        @Override
                        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                            double lng = (b.longitude - a.longitude) * fraction + a.longitude;
                            return new LatLng(lat, lng);
                        }
                    });
                }
                fireNode.router_list.put(new_router.getBssid(), new_router);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                fireNode.router_list.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void sendEvent(DataPacket packet) {
        long unixTime = System.currentTimeMillis() / 1000L;
        Firebase curr_ref = ref.child("raw_data").child(Long.toString(unixTime)).child(packet.wifi_bssid);
        curr_ref.setValue(packet);
    }


}
