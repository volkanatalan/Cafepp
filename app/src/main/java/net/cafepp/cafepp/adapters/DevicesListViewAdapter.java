package net.cafepp.cafepp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.Device;

import java.util.List;

public class DevicesListViewAdapter extends BaseAdapter {
  private List<Device> devices;
  private boolean showIP = false;
  
  private static class ViewHolder{
    private TextView deviceName, ip;
    private ImageView connectImage;
  }
  
  public DevicesListViewAdapter(List<Device> devices, boolean showIP) {
    this.devices = devices;
    this.showIP = showIP;
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
      viewHolder.connectImage = convertView.findViewById(R.id.connectImageView);
      
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
      
    } else {
      // If View Holder is available, reuse it.
      viewHolder = (ViewHolder) convertView.getTag();
    }
    
    Device device = devices.get(position);
    viewHolder.deviceName.setText(device.getDeviceName());
    
    if (device.isConnected()) viewHolder.connectImage.setImageResource(R.drawable.wifi);
    else viewHolder.connectImage.setImageResource(R.drawable.wifi_disconnected);
  
    if (showIP) {
      viewHolder.ip.setText(device.getIpAddress());
      viewHolder.ip.setVisibility(View.VISIBLE);
    } else viewHolder.ip.setVisibility(View.GONE);
    
    return convertView;
  }
  
  public void setConnected(int pos, boolean isConnected) {
    devices.get(pos).setConnected(isConnected);
    notifyDataSetChanged();
  }
}
