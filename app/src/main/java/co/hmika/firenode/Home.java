package co.hmika.firenode;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int LOCATION_PERMISSION_IDENTIFIER = 0;
    private Messenger bgMessenger;
    private boolean bgBound;
    public static FragmentManager fragmentManager;
    public Fragment currFragment;
    private SharedPreferences sharedPref;
    private FloatingActionButton fab;

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        fragmentManager = getSupportFragmentManager();
        currFragment = fragmentManager.findFragmentById(R.id.main_frag);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (sharedPref.getBoolean("logging",false)) fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_dis_log));
        else fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_log));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean logging = !sharedPref.getBoolean("logging",false);
                if (logging) {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_dis_log));
                    Snackbar.make(view, "Enabling logging", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_log));
                    Snackbar.make(view, "Disabling logging", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                sharedPref.edit().putBoolean("logging",logging).apply();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setStatusBarTranslucent(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!hasLocationPermissions()) requestLocationPermissionsBeforeStart();
        if (!locationSettingsEnabled()) startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        ((FireNode) getApplication()).startLocationUpdates();
        bindService(new Intent(this, BackgroundTasks.class), bgConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((FireNode)getApplication()).setCurrActivity(this);
    }

    @Override
    protected void onPause() {
        ((FireNode)getApplication()).setCurrActivity(null);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ((FireNode)getApplication()).stopLocationUpdates();
        if (bgBound) {
            unbindService(bgConnection);
            bgBound = false;
        }
        super.onDestroy();
    }

    private ServiceConnection bgConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            bgMessenger = new Messenger(service);
            bgBound = true;
        }
        public void onServiceDisconnected(ComponentName className) {
            bgMessenger = null;
            bgBound = false;
        }
    };

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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.live_view) {
            MapsFragment fragment = new MapsFragment();
            currFragment = fragment;
            fragmentTransaction.replace(R.id.main_frag, fragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.debug) {
            DebugFragment fragment = new DebugFragment();
            currFragment = fragment;
            fragmentTransaction.replace(R.id.main_frag, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
