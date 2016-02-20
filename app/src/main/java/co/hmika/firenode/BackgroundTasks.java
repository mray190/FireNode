package co.hmika.firenode;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class BackgroundTasks extends Service implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private WifiManager mainWifiObj;
    private FirebaseManager fb;
    private WifiHandler wifiReciever;
    private WifiInfoHandler wifiInfoHandler;

    private GoogleApiClient locationClient;
    private LocationRequest locationRequest;
    private Location myLocation;

    private String currentBSSID;
    private String currentIP;

    static final int START_WIFI_LISTENER = 0;
    static final int STOP_WIFI_LISTENER = 1;
    static final int START_LOCATION_LISTENER = 2;
    static final int STOP_LOCATION_LISTENER = 3;

    private SharedPreferences sharedPref;

    /**
     * Run when the service has been created but before the service is actually run
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //Start the location tracker
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mainWifiObj.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY,"scan_wifi_lock");
        fb = new FirebaseManager((FireNode)getApplication());
        wifiReciever = new WifiHandler();
        wifiInfoHandler = new WifiInfoHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationClient!=null && locationRequest!=null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, (com.google.android.gms.location.LocationListener) this);
            if (locationClient.isConnected()) {
                locationClient.disconnect();
                unregisterReceiver(wifiReciever);
            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("FireNode", "Info - connected to location services");
        //Location request and handling
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        myLocation = LocationServices.FusedLocationApi.getLastLocation(locationClient);
        if (myLocation==null) {
            myLocation = new Location("");
            myLocation.setLatitude(42.2912);
            myLocation.setLongitude(-83.7161);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("FireNode", "Error - connection to location services failed: " + connectionResult.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("FireNode", "Debug - connection to location services suspended");
    }

    /**
     * Handler of incoming messages from clients (i.e. starting & stopping companion viewer)
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_LOCATION_LISTENER:
                    locationClient.connect();
                    break;
                case STOP_LOCATION_LISTENER:
                    locationClient.disconnect();
                    break;
                case START_WIFI_LISTENER:
                    registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    break;
                case STOP_WIFI_LISTENER:
                    unregisterReceiver(wifiReciever);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger messenger = new Messenger(new IncomingHandler());

    private class WifiHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            ArrayList<DataPacket> dparray = new ArrayList<>();
            if (myLocation==null) return;
            for (int i=0; i<wifiScanList.size(); i++) {
                if (wifiScanList.get(i).SSID.equals("MWireless") || wifiScanList.get(i).SSID.equals("MHacks")) {
                    DataPacket dp = new DataPacket();
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    dp.userID = manufacturer + " " + model;
                    dp.gps_acc = myLocation.getAccuracy();
                    dp.gps_lat = myLocation.getLatitude();
                    dp.gps_lon = myLocation.getLongitude();
                    dp.wifi_bssid = wifiScanList.get(i).BSSID;
                    dp.wifi_strength = wifiScanList.get(i).level;
                    dp.wifi_freq = wifiScanList.get(i).frequency;
                    dp.wifi_ssid = wifiScanList.get(i).SSID;
                    double exp = (27.55-(20.0*Math.log10(dp.wifi_freq))+Math.abs(dp.wifi_strength))/20.0;
                    dp.wifi_dist = Math.pow(10,exp);
                    if (dp.wifi_bssid.equals(currentBSSID)) dp.wifi_ip = currentIP;
                    dparray.add(dp);
                    if (sharedPref.getBoolean("logging",false)) fb.sendEvent(dp);
                }
//                        mainWifiObj.calculateSignalLevel();
            }
            ((FireNode)context.getApplicationContext()).setDatapacketArray(dparray);
        }
    }

    private class WifiInfoHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentBSSID = mainWifiObj.getConnectionInfo().getBSSID();
            currentIP = Formatter.formatIpAddress(mainWifiObj.getConnectionInfo().getIpAddress());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("FireNode", "Info - Background Tasks service started");
        locationClient.connect();
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiInfoHandler, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.myLocation = location;
    }
}
