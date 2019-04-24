package net.cafepp.cafepp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.Device;

import java.util.ArrayList;
import java.util.List;

public class PairedDevicesAdapter extends BaseAdapter {
  
  private final String TAG = "PairedDevicesLVAdapter";
  private List<Device> devices = new ArrayList<>();
  
  private static class ViewHolder{
    TextView deviceNameTextView;
    ImageView isConnectedImageView;
  }
  
  @Override
  public int getCount() {
    return devices.size();
  }
  
  @Override
  public Device getItem(int position) {
    return devices.get(position);
  }
  
  @Override
  public long getItemId(int position) {
    return position;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    PairedDevicesAdapter.ViewHolder viewHolder;
    
    if (convertView == null) {
      viewHolder = new PairedDevicesAdapter.ViewHolder();
      convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_row_connect_devices, parent, false);
      
      viewHolder.deviceNameTextView = convertView.findViewById(R.id.deviceNameTextView);
      viewHolder.isConnectedImageView = convertView.findViewById(R.id.connectImageView);
      
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
      
    } else {
      // If View Holder is available, reuse it.
      viewHolder = (PairedDevicesAdapter.ViewHolder) convertView.getTag();
    }
    
    Device device = devices.get(position);
    
    viewHolder.deviceNameTextView.setText(device.getDeviceName());
    
    // Show the type of device (phone or tablet).
    if (device.isConnected())
      viewHolder.isConnectedImageView.setImageResource(R.drawable.wifi_connected);
    else
      viewHolder.isConnectedImageView.setImageResource(R.drawable.wifi_disconnected);
    
    return convertView;
  }
  
  public void add(Device device) {
    devices.add(device);
    Log.d(TAG, "Add paired device: " + device.getDeviceName());
    notifyDataSetChanged();
  }
  
  public void clear() {
    devices.clear();
    Log.d(TAG, "List is clear.");
    notifyDataSetChanged();
  }
  
  public void setConnected(int pos) {
    devices.get(pos).setConnected(true);
    notifyDataSetChanged();
  }
}
