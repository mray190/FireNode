package co.hmika.firenode;

import android.app.Fragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapsFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

        private GoogleMap mMap;
        private Location myLocation;
        private LocationClient mLocationClient;
        private boolean mapSet;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_map, container, false);
            super.onCreate(savedInstanceState);
            mLocationClient = new LocationClient(getActivity(), this, this);
            mapSet = false;
            setUpMapIfNeeded();
            return view;
        }

        private void setUpMapIfNeeded() {
            if (mMap == null) {
                mMap = ((SupportMapFragment) BusUMichActivity.fm.findFragmentById(R.id.location_map)).getMap();
                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.276946, -83.738220), 14));
                if (mMap != null) {
                    setUpMap();
                }
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if (mMap != null) {
                mMap = null;
            }
        }

        private void setUpMap() {
            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            //new MapRefresh().execute();
                        }
                    });
                }
            };
            timer.schedule(task, 0, 4000);
        }

      /*  class MapRefresh extends AsyncTask<Void, Void, ArrayList<Bus>> {
            @Override
            protected ArrayList<Bus> doInBackground(Void...params) {
                Parser parser = new Parser(getActivity());
                return parser.getBuses();
            }

            @Override
            protected void onPostExecute(ArrayList<Bus> buses) {
                mMap.clear();
                for (int i = 0; i < buses.size(); i++)
                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(buses.get(i).getLat(), buses.get(i).getLon()))
                                    .title(buses.get(i).getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker((int) ((Integer.parseInt(buses.get(i).getBusRoute().getColor(), 16) / 16777215.0) * 360)))
                                    .visible(buses.get(i).getBusRoute().getActive()));
                if (!mapSet) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 17));
                    mapSet = true;
                }
            }
        }
*/
        @Override
        public void onConnected(Bundle bundle) {
            myLocation = mLocationClient.getLastLocation();
        }

        @Override
        public void onDisconnected() { }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) { }
    }