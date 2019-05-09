package net.cafepp.cafepp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.enums.ClientType;
import net.cafepp.cafepp.objects.Device;

import java.util.ArrayList;
import java.util.List;

public class PairedDevicesAdapter extends BaseAdapter {
  
  private final String TAG = "PairedDevicesLVAdapter";
  private Context mContext;
  private List<Device> mDevices = new ArrayList<>();
  
  public PairedDevicesAdapter() {
    // Empty constructor.
  }
  
  public PairedDevicesAdapter(Context context, List<Device> devices) {
    mContext = context;
    mDevices = devices;
  }
  
  private static class ViewHolder{
    TextView deviceNameTextView;
    TextView clientTypeTextView;
    ImageView deviceTypeImageView;
    ImageView isConnectedImageView;
  }
  
  @Override
  public int getCount() {
    return mDevices.size();
  }
  
  @Override
  public Device getItem(int position) {
    return mDevices.get(position);
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
      viewHolder.clientTypeTextView = convertView.findViewById(R.id.text2);
      viewHolder.deviceTypeImageView = convertView.findViewById(R.id.deviceType);
      viewHolder.isConnectedImageView = convertView.findViewById(R.id.connectImageView);
      
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
      
    } else {
      // If View Holder is available, reuse it.
      viewHolder = (PairedDevicesAdapter.ViewHolder) convertView.getTag();
    }
    
    Device device = mDevices.get(position);
    boolean isTablet = device.isTablet();
    viewHolder.deviceNameTextView.setText(device.getDeviceName());
  
    int deviceTypeDrawable = isTablet ? R.drawable.tablet_with_logo : R.drawable.phone_with_logo;
    viewHolder.deviceTypeImageView.setImageResource(deviceTypeDrawable);
  
    String clientType = "";
    if (mContext != null) {
      switch (device.getClientType()) {
        case COOK:
          clientType = mContext.getResources().getString(R.string.cook);
          break;
        case CASHIER:
          clientType = mContext.getResources().getString(R.string.cashier);
          break;
        case CUSTOMER:
          clientType = mContext.getResources().getString(R.string.customer);
          break;
        case MANAGER:
          clientType = mContext.getResources().getString(R.string.manager);
          break;
        case WAITER:
          clientType = mContext.getResources().getString(R.string.waiter);
          break;
      }
      viewHolder.clientTypeTextView.setText(clientType);
    }
    
    // Show the type of device (phone or tablet).
    if (device.isConnected())
      viewHolder.isConnectedImageView.setImageResource(R.drawable.wifi_connected);
    else if (device.isFound())
      viewHolder.isConnectedImageView.setImageResource(R.drawable.wifi_found);
    else
      viewHolder.isConnectedImageView.setImageResource(R.drawable.wifi_disconnected);
    
    return convertView;
  }
  
  public List<Device> getmDevices() {
    return mDevices;
  }
  
  public void setPairedDevices(List<Device> devices) {
    this.mDevices = devices;
    Log.d(TAG, devices.size() + " devices are added to the list.");
    notifyDataSetChanged();
  }
  
  public void add(Device device) {
    mDevices.add(0, device);
    Log.d(TAG, "Paired device is added: " + device.getDeviceName());
    notifyDataSetChanged();
  }
  
  public void remove(Device device) {
    int pos = getPositionByMac(device.getMacAddress());
    if (pos > -1) {
      mDevices.remove(pos);
      notifyDataSetChanged();
    }
  }
  
  public void clear() {
    mDevices.clear();
    Log.d(TAG, "List is clear.");
    notifyDataSetChanged();
  }
  
  public void setConnected(Device device, boolean isConnected) {
    int pos = getPositionByMac(device.getMacAddress());
    ClientType type = mDevices.get(pos).getClientType();
    if (pos > -1) {
      device.setConnected(isConnected);
      device.setClientType(type);
      mDevices.set(pos, device);
      notifyDataSetChanged();
    }
  }
  
  public void setConnected(int pos, boolean isConnected) {
    mDevices.get(pos).setConnected(isConnected);
    notifyDataSetChanged();
  }
  
  public void setAllDisconnected() {
    for (Device device : mDevices) {
      device.setConnected(false);
      device.setFound(false);
    }
    notifyDataSetChanged();
  }
  
  public boolean isContain(String macAddress) {
    for (Device device : mDevices) {
      if (device.getMacAddress().equals(macAddress))
        return true;
    }
    return false;
  }
  
  public void setFound(Device device, boolean isFound) {
    String mac = device.getMacAddress();
    int pos = getPositionByMac(mac);
    ClientType type = mDevices.get(pos).getClientType();
    
    // If there is a device with the same MAC address in database, set it as found
    if (pos > -1) {
      device.setFound(isFound);
      device.setClientType(type);
      mDevices.set(pos, device);
      if (!isFound) mDevices.get(pos).setConnected(false);
      notifyDataSetChanged();
    }
  }
  
  public void setFoundByName(String deviceName, boolean isFound) {
    int pos = getPositionByName(deviceName);
    
    // If there is a device with the same MAC address in database, set it as found
    if (pos > -1) {
      mDevices.get(pos).setFound(isFound);
      if (!isFound) mDevices.get(pos).setConnected(false);
      notifyDataSetChanged();
    }
  }
  
  public int getPositionByMac(String macAddress) {
    if (mDevices.size() > 0) {
      for (int i = 0; i < mDevices.size(); i++) {
        if (mDevices.get(i).getMacAddress().equals(macAddress))
          return i;
      }
    }
    Log.d(TAG, "Not found a device with the same MAC address.");
    return -1;
  }
  
  public int getPositionByName(String deviceName) {
    if (mDevices.size() > 0) {
      for (int i = 0; i < mDevices.size(); i++) {
        if (mDevices.get(i).getDeviceName().equals(deviceName))
          return i;
      }
    }
    Log.d(TAG, "Device not found: " + deviceName);
    return -1;
  }
}
