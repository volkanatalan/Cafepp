package net.cafepp.cafepp.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.AvailableDevicesListViewAdapter;
import net.cafepp.cafepp.fragments.ConnectFragment;
import net.cafepp.cafepp.fragments.ConnectSettingsDeviceNameFragment;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.services.ClientService;

public class ConnectActivity extends AppCompatActivity {
  
  private final String TAG = "ConnectActivity";
  public TextView deviceNameTextView;
  private ListView availableDevicesListView;
  private AvailableDevicesListViewAdapter foundDevicesAdapter;
  public FrameLayout interlayer;
  private Messenger messenger = null;
  private boolean bound = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect);
    deviceNameTextView = findViewById(R.id.deviceNameTextView);
    availableDevicesListView = findViewById(R.id.availableDevicesListView);
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
          
          // Stop listening for the commands from Client Service.
          LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
          foundDevicesAdapter.clear();
          
          unbindService(serviceConnection);
          bound = false;
        }
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
      Device device = (Device) intent.getBundleExtra("Message").getSerializable("Message");
      Log.d(TAG, "BroadcastReceiver command: " + command);
      
      if (command.equals("ADD")) foundDevicesAdapter.add(device);
      else if (command.equals("CLEAR")) foundDevicesAdapter.clear();
    }
  };
  
  private void configureRecyclerView() {
    foundDevicesAdapter = new AvailableDevicesListViewAdapter();
    availableDevicesListView.setOnItemClickListener((parent, view, pos, id) -> {
      Log.d(TAG, "position: " + pos);

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
    
    availableDevicesListView.setAdapter(foundDevicesAdapter);
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
}
