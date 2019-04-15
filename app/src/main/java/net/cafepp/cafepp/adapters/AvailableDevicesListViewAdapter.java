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

import java.util.List;

public class AvailableDevicesListViewAdapter extends BaseAdapter {
  
  private final String TAG = "AvailableDevicesLVAdptr";
  private List<Device> devices;
  
  private static class ViewHolder{
    TextView deviceName;
    TextView ip;
    ImageView deviceType;
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
    ViewHolder viewHolder;
  
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_row_connect_devices, parent, false);
  
      viewHolder.deviceName = convertView.findViewById(R.id.deviceNameTextView);
      viewHolder.ip = convertView.findViewById(R.id.ipTextView);
      viewHolder.ip.setVisibility(View.VISIBLE);
      viewHolder.deviceType = convertView.findViewById(R.id.connectImageView);
  
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
      
    } else {
      // If View Holder is available, reuse it.
      viewHolder = (ViewHolder) convertView.getTag();
    }
    
      viewHolder.deviceName.setText(devices.get(position).getDeviceName());
      viewHolder.ip.setText(devices.get(position).getIpAddress());
      
      // Show the type of device (phone or tablet).
      if (devices.get(position).isTablet())
        viewHolder.deviceType.setImageResource(R.drawable.tablet_with_logo);
      else
        viewHolder.deviceType.setImageResource(R.drawable.phone_with_logo);
      
    return convertView;
  }
  
  public void add(Device device) {
    devices.add(device);
    Log.d(TAG, "Add service successful: " + device.getDeviceName());
    notifyDataSetChanged();
  }
  
  public void clear() {
    devices.clear();
    notifyDataSetChanged();
  }
  
  public void setConnected(int pos) {
    devices.get(pos).setConnected(true);
    notifyDataSetChanged();
  }
}