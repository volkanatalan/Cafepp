package net.cafepp.cafepp.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.fragments.ConnectFragment;
import net.cafepp.cafepp.fragments.ConnectSettingsDeviceNameFragment;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.services.ClientService;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {
  
  private final String TAG = "ConnectActivity";
  public TextView deviceNameTextView;
  private RecyclerView recyclerView;
  private FoundDevicesAdapter foundDevicesAdapter;
  private View.OnClickListener mOnClickListener;
  public FrameLayout interlayer;
  private Messenger messenger = null;
  private boolean bound = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect);
    deviceNameTextView = findViewById(R.id.deviceNameTextView);
    recyclerView = findViewById(R.id.recyclerView);
    interlayer = findViewById(R.id.interlayer);
    Switch switchFindDevices = findViewById(R.id.switchFindDevices);
    
    configureRecyclerView();
  
  
    boolean isServiceRunning = isServiceRunningInForeground(this, ClientService.class);
    switchFindDevices.setChecked(isServiceRunning);
    Log.d(TAG, "Is Client Service running: " + isServiceRunning);
  
    switchFindDevices.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        Log.d(TAG, "Switch checked true");
        Intent clientServiceIntent = new Intent(this, ClientService.class);
        bindService(clientServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
  
        // Listen for the commands from Client Service.
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, new IntentFilter("ConnectActivity"));
        
      } else {
  
        // Unbind from the service
        if (bound) {
          unbindService(serviceConnection);
          bound = false;
        }
  
        // Stop listening for the commands from Client Service.
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
    String deviceName = sharedPreferences.getString("deviceName", getString(R.string.cafepp_device));
    
    deviceNameTextView.setText(deviceName);
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    
    // Stop listening for the commands from Client Service.
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    foundDevicesAdapter.clear();
  }
  
  private void sendMessageToClientService(Object message) {
    if (!bound) {
      Log.e(TAG, "Service not bound!");
      return;
    }
    
    // Create and send a message to the service, using a supported 'what' value
    Message msg = new Message();
    msg.obj = message;
    try {
      messenger.send(msg);
      Log.i(TAG, "Message sent to Client Service!");
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    
  }
  
  /** Defines callbacks for service binding, passed to bindService() */
  private ServiceConnection serviceConnection = new ServiceConnection() {
    
    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
      // This is called when the connection with the service has been
      // established, giving us the object we can use to
      // interact with the service.  We are communicating with the
      // service using a Messenger, so here we get a client-side
      // representation of that from the raw IBinder object.
      messenger = new Messenger(service);
      bound = true;
    }
    
    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      // This is called when the connection with the service has been
      // unexpectedly disconnected -- that is, its process crashed.
      messenger = null;
      bound = false;
    }
    
  };
  
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get extra data included in the Intent
      String command = intent.getStringExtra("Command");
      Device device = intent.getBundleExtra("Message").getParcelable("Message");
      Log.d(TAG, "BroadcastReceiver command: " + command);
      if (command.equals("ADD")) foundDevicesAdapter.add(device);
      else if (command.equals("CLEAR")) foundDevicesAdapter.clear();
    }
  };
  
  private void configureRecyclerView() {
    foundDevicesAdapter = new FoundDevicesAdapter();
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    foundDevicesAdapter.setClickListener(v -> {
      int pos = recyclerView.indexOfChild(v);
      Log.d(TAG, "pos: " + pos);

      Device device = foundDevicesAdapter.getItem(pos);
      String deviceName = device.getDeviceName();
      
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
          R.anim.enter_from_bottom, R.anim.exit_to_top);
      ft.replace(R.id.fragmentContainer, ConnectFragment.newInstance(deviceName), "ConnectFragment");
      ft.addToBackStack("ConnectFragment");
      ft.commit();

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
  
  public void onClickChangeDeviceName(View view) {
    interlayer.setVisibility(View.VISIBLE);
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
        R.anim.enter_from_bottom, R.anim.exit_to_top);
    ft.replace(R.id.fragmentContainer, new ConnectSettingsDeviceNameFragment());
    ft.addToBackStack("ConnectActivity");
    ft.commit();
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
      viewHolder.deviceName.setText(devices.get(i).getDeviceName());
      viewHolder.ip.setText(devices.get(i).getIpAddress());
      
      // If a device is connected, show a wifi image to indicate this.
      if (devices.get(i).isConnected())
        viewHolder.wifi.setVisibility(View.VISIBLE);
    }
  
    @Override
    public int getItemCount() {
      return devices.size();
    }
  
    void add(Device device) {
      
      if (hasIP(device.getIpAddress())){
        // If a service with the same ip has found, don't add it to the list.
        Log.d(TAG, "Same service: " + device.getDeviceName());
        return;
  
      } else if (hasName(device.getDeviceName())){
        // If a service with the same name but different ip has found, change its name.
        Log.d(TAG, "Service with the same name but different ip: " + device.getDeviceName());
        
        String deviceName = device.getDeviceName();
        int i = 1;
  
        while (hasName(deviceName)) {
          i++;
          deviceName += " (" + i + ")";
        }
  
        device.setDeviceName(deviceName);
      }
      
      devices.add(device);
      Log.d(TAG, "Add service successful: " + device.getDeviceName());
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
  
    boolean hasIP(String ip) {
      for (Device device : devices)
        if (device.getIpAddress().equals(ip))
          return true;
        
      return false;
    }
  
    boolean hasName(String deviceName) {
      for (Device device : devices) {
        if (device.getDeviceName().equals(deviceName)) return true;
      }
      return false;
    }
  
    private void setClickListener(View.OnClickListener listener) {
      mOnClickListener = listener;
    }
  }
}
