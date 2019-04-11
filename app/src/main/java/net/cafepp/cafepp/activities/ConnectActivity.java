package net.cafepp.cafepp.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.fragments.ConnectFragment;
import net.cafepp.cafepp.services.ClientService;
import net.cafepp.cafepp.services.Constants;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {
  
  private final String TAG = "ConnectActivity";
  private String deviceName;
  private TextView deviceNameTextView;
  private RecyclerView recyclerView;
  private FoundDevicesAdapter foundDevicesAdapter;
  private View.OnClickListener mOnClickListener;
  public FrameLayout interlayer;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect);
    deviceNameTextView = findViewById(R.id.deviceNameTextView);
    recyclerView = findViewById(R.id.recyclerView);
    Switch switchFindDevices = findViewById(R.id.switchFindDevices);
    
    configureRecyclerView();
  
    LocalBroadcastManager.getInstance(this).registerReceiver(
        mMessageReceiver, new IntentFilter("ClientService"));
  
  
    boolean isServiceRunning = isServiceRunningInForeground(this, ClientService.class);
    switchFindDevices.setChecked(isServiceRunning);
    Log.d(TAG, "Is Client Service running: " + isServiceRunning);
  
    switchFindDevices.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        Log.d(TAG, "Switch checked true");
        Intent startIntent = new Intent(this, ClientService.class);
        startIntent.putExtra("deviceName", deviceName);
        startIntent.setAction(Constants.ACTION.START_ACTION);
        startService(startIntent);
        
      } else {
        Log.d(TAG, "Switch checked false");
        Intent stopIntent = new Intent(this, ClientService.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        startService(stopIntent);
  
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        foundDevicesAdapter.clear();
      }
    });
  }
  
  @Override
  protected void onResume() {
    super.onResume();
  
    // Get device name.
    SharedPreferences sharedPreferences = getSharedPreferences("ConnectSettings", Context.MODE_PRIVATE);
    deviceName = sharedPreferences.getString("deviceName", getString(R.string.cafepp_device));
    
    deviceNameTextView.setText(deviceName);
  }
  
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get extra data included in the Intent
      String command = intent.getStringExtra("Command");
      NsdServiceInfo info = intent.getBundleExtra("Message").getParcelable("Message");
      Log.d(TAG, "BroadcastReceiver command: " + command);
      if (command.equals("ADD")) foundDevicesAdapter.add(info);
      else if (command.equals("CLEAR")) foundDevicesAdapter.clear();
    }
  };
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.settings, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        Intent intent = new Intent(this, ConnectSettingsActivity.class);
        startActivity(intent);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  private void configureRecyclerView() {
    foundDevicesAdapter = new FoundDevicesAdapter();
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    foundDevicesAdapter.setClickListener(v -> {
      int pos = recyclerView.indexOfChild(v);
      Log.d(TAG, "pos: " + pos);

      FoundDevicesAdapter.Device device = foundDevicesAdapter.getItem(pos);
      String deviceName = device.getNsdServiceInfo().getServiceName();
      
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_left,
          R.anim.enter_from_bottom, R.anim.exit_to_left);
      ft.replace(R.id.fragmentContainer, ConnectFragment.newInstance(deviceName), "ConnectFragment");
      ft.addToBackStack("ConnectFragment");
      ft.commit();

      interlayer = findViewById(R.id.interlayer);
      interlayer.setVisibility(View.VISIBLE);
    });
    recyclerView.setAdapter(foundDevicesAdapter);
  }
  
  public static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return service.foreground;
      }
    }
    return false;
  }
  
  private class FoundDevicesAdapter extends RecyclerView.Adapter<FoundDevicesAdapter.ViewHolder> {
  
    private final String TAG = "FoundDevicesAdapter";
    private List<Device> devices = new ArrayList<>();
  
    class ViewHolder extends RecyclerView.ViewHolder {
  
      TextView deviceName;
      TextView ip;
      ImageView wifi;
      
      ViewHolder(@NonNull View view) {
        super(view);
        deviceName = view.findViewById(R.id.deviceNameTextView);
        ip = view.findViewById(R.id.ipTextView);
        wifi = view.findViewById(R.id.connectImageView);
      }
    }
  
    class Device {
      private NsdServiceInfo nsdServiceInfo;
      private boolean isConnected = false;
      
      Device(NsdServiceInfo info){
        nsdServiceInfo = info;
      }
  
      NsdServiceInfo getNsdServiceInfo() {
        return nsdServiceInfo;
      }
  
      void setNsdServiceInfo(NsdServiceInfo nsdServiceInfo) {
        this.nsdServiceInfo = nsdServiceInfo;
      }
  
      boolean isConnected() {
        return isConnected;
      }
  
      void setConnected(boolean connected) {
        isConnected = connected;
      }
    }
    
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(viewGroup.getContext())
                      .inflate(R.layout.list_row_connect_devices, viewGroup, false);
      ViewHolder holder = new ViewHolder(view);
      holder.itemView.setOnClickListener(v -> mOnClickListener.onClick(v));
      return new ViewHolder(view);
    }
  
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
      NsdServiceInfo service = devices.get(i).getNsdServiceInfo();
      viewHolder.deviceName.setText(service.getServiceName());
      viewHolder.ip.setText(service.getHost().getHostAddress());
      
      // If a device is connected, show a wifi image to indicate this.
      if (devices.get(i).isConnected())
        viewHolder.wifi.setVisibility(View.VISIBLE);
    }
  
    @Override
    public int getItemCount() {
      return devices.size();
    }
  
    void add(NsdServiceInfo serviceInfo) {
      if (hasIP(serviceInfo.getHost())){
        // If a service with the same ip has found, don't add it to the list.
        Log.d(TAG, "Same service: " + serviceInfo.getServiceName());
        return;
        
      } else if (hasName(serviceInfo.getServiceName())){
        // If a service with the same name but different ip has found, change its name.
        Log.d(TAG, "Service with the same name but different ip: " + serviceInfo.getServiceName());
        String serviceName = serviceInfo.getServiceName();
        int i = 1;
  
        while (hasName(serviceName)) {
          i++;
          serviceName += " (" + i + ")";
        }
  
        serviceInfo.setServiceName(serviceName);
      }
      
      devices.add(new Device(serviceInfo));
      Log.d(TAG, "Add service successful: " + serviceInfo.getServiceName());
      notifyDataSetChanged();
    }
  
    Device getItem(int pos) {
      return devices.get(pos);
    }
    
    void clear() {
      devices.clear();
      notifyDataSetChanged();
    }
  
    void setConnected(int pos) {
      devices.get(pos).setConnected(true);
      notifyDataSetChanged();
    }
  
    boolean hasIP(InetAddress ip) {
      for (Device device : devices)
        if (device.nsdServiceInfo.getHost().getHostAddress().equals(ip.getHostAddress()))
          return true;
      return false;
    }
  
    boolean hasName(String serviceName) {
      for (Device device : devices) {
        if (device.nsdServiceInfo.getServiceName().equals(serviceName)) return true;
      }
      return false;
    }
  
    private void setClickListener(View.OnClickListener listener) {
      mOnClickListener = listener;
    }
  }
}
