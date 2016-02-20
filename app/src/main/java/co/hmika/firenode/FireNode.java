package co.hmika.firenode;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by mray on 2/20/16.
 */
public class FireNode extends Application {

    public FireNode() { }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
