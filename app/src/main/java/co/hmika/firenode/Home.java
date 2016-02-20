package co.hmika.firenode;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private WifiManager mainWifiObj;
    private static final int LOCATION_PERMISSION_IDENTIFIER = 0;
    private FirebaseManager fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupWifiListener();
    }

    private void setupWifiListener() {
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        fb = new FirebaseManager();
        WifiHandler wifiReciever = new WifiHandler();
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private class WifiHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (hasLocationPermissions()) {
                if (locationSettingsEnabled()) {
                    List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
                    for (int i=0; i<wifiScanList.size(); i++) {
                        String data = wifiScanList.get(i).toString();
                        DataPacket dp = new DataPacket();
                        dp.wifi_bssid = wifiScanList.get(i).BSSID;
                        dp.wifi_strength = wifiScanList.get(i).level;
                        dp.wifi_freq = wifiScanList.get(i).frequency;
                        dp.wifi_ssid = wifiScanList.get(i).SSID;
                        fb.sendEvent(dp, wifiScanList.get(i).timestamp);
                    }
                } else {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            } else {
                requestLocationPermissionsBeforeStart();
            }

        }
    }

    private void requestLocationPermissionsBeforeStart() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_IDENTIFIER
        );
    }

    private boolean hasLocationPermissions() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private boolean locationSettingsEnabled() {
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                int locationMode = Settings.Secure.getInt(
                        getContentResolver(),
                        Settings.Secure.LOCATION_MODE
                );
                return locationMode != 0;
            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
        } else {
            String locationProviders = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            );
            return !(locationProviders == null || locationProviders.equals(""));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
