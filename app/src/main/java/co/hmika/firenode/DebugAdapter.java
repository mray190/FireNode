package co.hmika.firenode;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Mine on 2/20/16.
 */

    public class DebugAdapter extends ArrayAdapter<DataPacket> {
        Context context;
        int layoutResourceId;
        ArrayList<DataPacket> data = null;
        DecimalFormat df = new DecimalFormat("00.0000");

        public DebugAdapter(Context context, int layoutResourceId, ArrayList<DataPacket> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            DebugHolder holder = null;

            if (row==null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new DebugHolder();
                holder.lat = (TextView)row.findViewById(R.id.debug_lat);
                holder.lon = (TextView)row.findViewById(R.id.debug_long);
                holder.accuracy = (TextView)row.findViewById(R.id.debug_acc);
                holder.sig_strength = (TextView)row.findViewById(R.id.debug_wifi_strenght);
                holder.bssid = (TextView)row.findViewById(R.id.debug_bssid);
                holder.gate_id = (TextView)row.findViewById(R.id.debug_gate);
                holder.freq = (TextView)row.findViewById(R.id.debug_freq);
                holder.ssid = (TextView)row.findViewById(R.id.debug_ssid);
                row.setTag(holder);
            } else {
                holder = (DebugHolder)row.getTag();
            }
            final DataPacket packet = data.get(position);
            holder.lat.setText("Lat: " + df.format(packet.gps_lat));
            holder.lon.setText("Lon: " + df.format(packet.gps_lon));
            holder.accuracy.setText("Acc: " + Double.toString(packet.gps_acc));
            holder.sig_strength.setText(Double.toString(packet.wifi_strength) + " dBm");
            holder.bssid.setText("BSSID: " + packet.wifi_bssid);
            holder.gate_id.setText("Gate: " + packet.wifi_gate);
            holder.freq.setText(packet.wifi_freq + " MHz");
            holder.ssid.setText("SSID: " + packet.wifi_ssid);
            return row;
        }

        static class DebugHolder {
            TextView lat, lon, accuracy, sig_strength, bssid, gate_id, freq, ssid;
        }
    }


