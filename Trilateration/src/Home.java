import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class Home {
	
	private static HashMap<String, BSSID> hash;

	public static void main(String[] args) {
		hash = new HashMap<String, BSSID>();
		final Firebase ref = new Firebase("https://firenodemhacks.firebaseio.com/");
        ref.child("raw_data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            	for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    DataPacket dp = postSnapshot.getValue(DataPacket.class);
                    BSSID bssid = new BSSID();
                    if (hash.containsKey(dp.getWifi_bssid())) {
                    	bssid = hash.get(dp.getWifi_bssid());
                    	for (int i=0; i<bssid.dataPackets.size(); i++) {
                    		if (bssid.dataPackets.get(i).equals(dp)) return;
                    	}
                    }
                	bssid.dataPackets.add(dp);
                	hash.put(dp.getWifi_bssid(), bssid);
                    double[] output = trilaterate(bssid.dataPackets);
                    if (output!=null) {
                    	Router router = new Router();
                    	router.setGps_lat(output[0]);
                    	router.setGps_lon(output[1]);
                    	router.setRange(output[2]);
                    	ref.child("parse_data").child(dp.getWifi_bssid()).setValue(router);
                    }
            	}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
        while(true) {}
	}
	
	public static double[] trilaterate(ArrayList<DataPacket> dp) {
		if (dp.size()<=1) return null;
		double r = 6371;
		double[][] positions = new double[dp.size()][];
		double[] distances = new double[dp.size()];
		int start = 0;
		if (dp.size()>25) start = dp.size()-25;
		double dist_sum = 0;
		for (int i=start; i<dp.size(); i++) {
			positions[i] = new double[2];
			
			double lat_deg = dp.get(i).getGps_lat();
			double lon_deg = dp.get(i).getGps_lon();
			
			positions[i][0] = (90-lat_deg)*Math.PI*r/180;
			positions[i][1] = ((lon_deg)*2*Math.PI*r/Math.cos(lat_deg))/360;
			
//			positions[i][0] = dp.get(i).getGps_lat();
//			positions[i][1] = dp.get(i).getGps_lon();
			distances[i] = dp.get(i).getWifi_dist()/1000;
			dist_sum += dp.get(i).getWifi_dist();
		}

		NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
		Optimum optimum = solver.solve();

		// the answer
		double[] calculatedPosition = optimum.getPoint().toArray();
		
		double final_lat = -(calculatedPosition[0]*180/(Math.PI*r))+90;
		double final_lon = (calculatedPosition[1]*360*Math.cos(final_lat))/(2*Math.PI*r);
		
		System.out.println(final_lat + " " + final_lon);
		double[] output = {final_lat, final_lon, dist_sum/(dp.size()-start)};
		return output;
	}

}
