package co.hmika.firenode;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

import java.util.ArrayList;

public class FireNode extends MultiDexApplication {

    private Intent background_service;
    private ArrayList<DataPacket> datapacket_array;

    public FireNode() { }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        background_service = new Intent(getBaseContext(), BackgroundTasks.class);
    }

    public void startLocationUpdates() {
        startService(background_service);
    }

    public void stopLocationUpdates() {
        stopService(background_service);
    }

    public void setDatapacket_array(ArrayList<DataPacket> datapacket_array) {
        this.datapacket_array = datapacket_array;
    }

    public ArrayList<DataPacket> getDatapacket_array() {
        return datapacket_array;
    }
}
