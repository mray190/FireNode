package co.hmika.firenode;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

public class FireNode extends MultiDexApplication {

    private Intent background_service;

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
}
