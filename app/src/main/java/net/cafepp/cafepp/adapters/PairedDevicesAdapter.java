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
  
  public PairedDevicesAdapter() {
    // Empty constructor.
  }
  
  public PairedDevicesAdapter(List<Device> devices) {
    this.devices = devices;
  }
  
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
    else if (device.isFound())
      viewHolder.isConnectedImageView.setImageResource(R.drawable.wifi_found);
    else
      viewHolder.isConnectedImageView.setImageResource(R.drawable.wifi_disconnected);
    
    return convertView;
  }
  
  public List<Device> getDevices() {
    return devices;
  }
  
  public void setPairedDevices(List<Device> devices) {
    this.devices = devices;
    Log.d(TAG, devices.size() + " devices are added to the list.");
    notifyDataSetChanged();
  }
  
  public void add(Device device) {
    devices.add(0, device);
    Log.d(TAG, "Paired device is added: " + device.getDeviceName());
    notifyDataSetChanged();
  }
  
  public void remove(Device device) {
    int pos = getPositionByMac(device.getMacAddress());
    if (pos > -1) {
      devices.remove(pos);
      notifyDataSetChanged();
    }
  }
  
  public void clear() {
    devices.clear();
    Log.d(TAG, "List is clear.");
    notifyDataSetChanged();
  }
  
  public void setConnected(Device device, boolean isConnected) {
    int pos = getPositionByMac(device.getMacAddress());
    if (pos > -1) {
      device.setConnected(isConnected);
      devices.set(pos, device);
      notifyDataSetChanged();
    }
  }
  
  public void setConnected(int pos, boolean isConnected) {
    devices.get(pos).setConnected(isConnected);
    notifyDataSetChanged();
  }
  
  public void setAllDisconnected() {
    for (Device device : devices) {
      device.setConnected(false);
      device.setFound(false);
    }
    notifyDataSetChanged();
  }
  
  public boolean isContain(String macAddress) {
    for (Device device : devices) {
      if (device.getMacAddress().equals(macAddress))
        return true;
    }
    return false;
  }
  
  public void setFoundByMac(Device device, boolean isFound) {
    String mac = device.getMacAddress();
    int pos = getPositionByMac(mac);
    
    // If there is a device with the same MAC address in database, set it as found
    if (pos > -1) {
      device.setFound(isFound);
      devices.set(pos, device);
      if (!isFound) devices.get(pos).setConnected(false);
      notifyDataSetChanged();
    }
  }
  
  public void setFoundByName(String deviceName, boolean isFound) {
    int pos = getPositionByName(deviceName);
    
    // If there is a device with the same MAC address in database, set it as found
    if (pos > -1) {
      devices.get(pos).setFound(isFound);
      if (!isFound) devices.get(pos).setConnected(false);
      notifyDataSetChanged();
    }
  }
  
  public int getPositionByMac(String macAddress) {
    if (devices.size() > 0) {
      for (int i = 0; i < devices.size(); i++) {
        if (devices.get(i).getMacAddress().equals(macAddress))
          return i;
      }
    }
    Log.d(TAG, "Unable to find a device with the MAC address " + macAddress);
    return -1;
  }
  
  public int getPositionByName(String deviceName) {
    if (devices.size() > 0) {
      for (int i = 0; i < devices.size(); i++) {
        if (devices.get(i).getDeviceName().equals(deviceName))
          return i;
      }
    }
    Log.d(TAG, "Device not found: " + deviceName);
    return -1;
  }
}
