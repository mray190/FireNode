package co.hmika.firenode;

import android.app.Activity;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

import java.util.ArrayList;

public class FireNode extends MultiDexApplication {

    private Intent background_service;
    private Activity currActivity;

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

    public void setDatapacketArray(ArrayList<DataPacket> datapacket_array) {
        if (this.currActivity instanceof Home) {
            if (((Home)this.currActivity).currFragment instanceof DebugFragment) {
                ((DebugFragment)((Home)this.currActivity).currFragment).updateArray(datapacket_array);
            }
        }
    }

    public void setCurrActivity(Activity currActivity) {
        this.currActivity = currActivity;
    }
}
