package net.cafepp.cafepp.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.AvailableDevicesListViewAdapter;
import net.cafepp.cafepp.connection.Package;
import  net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.fragments.PairFragment;
import net.cafepp.cafepp.fragments.ConnectSettingsDeviceNameFragment;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.services.ClientService;

import java.util.Random;


public class ConnectActivity extends AppCompatActivity {
  
  private final String TAG = "ConnectActivity";
  public TextView deviceNameTextView;
  private Device myDevice;
  private ListView availableDevicesListView;
  private AvailableDevicesListViewAdapter foundDevicesAdapter;
  public FrameLayout interlayer;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect);
    deviceNameTextView = findViewById(R.id.deviceNameTextView);
    availableDevicesListView = findViewById(R.id.availableDevicesListView);
    interlayer = findViewById(R.id.interlayer);
    Switch switchFindDevices = findViewById(R.id.switchFindDevices);
    
    configureListView();
    getMyDevice();
  
    boolean isServiceRunning = isServiceRunningInForeground(this, ClientService.class);
    switchFindDevices.setChecked(isServiceRunning);
    Log.d(TAG, "Is Client Service running: " + isServiceRunning);
  
    switchFindDevices.setOnCheckedChangeListener(onCheckedChangeListener);
  
    // Listen for the commands from Client Service.
    LocalBroadcastManager.getInstance(ConnectActivity.this).registerReceiver(
        mMessageReceiver, new IntentFilter("ConnectActivity"));
  }
  
  @Override
  protected void onResume() {
    super.onResume();
  
    String deviceName = getDeviceName();
    
    deviceNameTextView.setText(deviceName);
  }
  
  @Override
  protected void onDestroy() {
    // Stop listening for the commands from Client Service.
    LocalBroadcastManager.getInstance(ConnectActivity.this).unregisterReceiver(mMessageReceiver);
    foundDevicesAdapter.clear();
    
    super.onDestroy();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.refresh, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_refresh) {
      sendCommandToClientService(Command.REFRESH);
      return true;
    }
    
    return super.onOptionsItemSelected(item);
  }
  
  private CompoundButton.OnCheckedChangeListener onCheckedChangeListener =
    (buttonView, isChecked) -> {
      Intent clientServiceIntent = new Intent(ConnectActivity.this, ClientService.class);
      if (isChecked) {
        Log.d(TAG, "Switch checked true.");
    
        // Start ClientService.
        startService(clientServiceIntent);
    
      } else {
        Log.d(TAG, "Switch checked false.");
        
        foundDevicesAdapter.clear();
    
        // Stop ClientService.
        stopService(clientServiceIntent);
      }
    };
  
  private void configureListView() {
    foundDevicesAdapter = new AvailableDevicesListViewAdapter();
    availableDevicesListView.setOnItemClickListener((parent, view, pos, id) -> {
      
      Device targetDevice = foundDevicesAdapter.getItem(pos);
      myDevice.setPairKey(generatePairKey());
      Package aPackage = new Package(Command.PAIR_REQ, myDevice, targetDevice);
  
      sendPackageToClientService(aPackage);
      
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
          R.anim.enter_from_bottom, R.anim.exit_to_top);
      ft.replace(R.id.fragmentContainer, PairFragment.newInstance(
          aPackage, mOnButtonClickListener), "PairFragment");
      ft.addToBackStack("PairFragment");
      ft.commit();
      
      interlayer.setVisibility(View.VISIBLE);
    });
    
    availableDevicesListView.setAdapter(foundDevicesAdapter);
  }
  
  private PairFragment.OnButtonClickListener mOnButtonClickListener =
      new PairFragment.OnButtonClickListener() {
    @Override
    public void onClickPair(Package aPackage) {
      aPackage.setCommand(Command.PAIR_CLIENT_ACCEPT);
      sendPackageToClientService(aPackage);
    }
  
    @Override
    public void onClickCancel(Package aPackage) {
      aPackage.setCommand(Command.PAIR_CLIENT_DENY);
      sendPackageToClientService(aPackage);
    }
  };
  
  public void sendCommandToClientService(Command command) {
    Intent intent = new Intent("ClientService");
    intent.putExtra("Message", command);
    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
  }
  
  public void sendPackageToClientService(Package aPackage) {
    Intent intent = new Intent("ClientService");
    intent.putExtra("Message", aPackage);
    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
  }
  
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "Received message.");
      Package aPackage = (Package) intent.getSerializableExtra("Message");
  
      if (aPackage != null) {
        Command command = aPackage.getCommand();
  
        switch (command) {
          case FOUND:
            foundDevicesAdapter.add(aPackage.getTargetDevice());
            break;
            
          case CLEAR:
            foundDevicesAdapter.clear();
            break;
        }
      }
    }
  };
  
  private int generatePairKey() {
    final int min = 10000;
    final int max = 99999;
    return new Random().nextInt((max - min) + 1) + min;
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
  
  private void getMyDevice() {
    myDevice = new Device(getDeviceName(), getMacAddress(), getIpAddress());
  }
  
  private String getDeviceName() {
    SharedPreferences sharedPreferences = getSharedPreferences("ConnectSettings", Context.MODE_PRIVATE);
    return sharedPreferences.getString("deviceName",getString(R.string.cafepp_device));
  }
  
  private String getIpAddress() {
    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
  }
  
  private String getMacAddress() {
    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    WifiInfo wInfo = wifiManager.getConnectionInfo();
    return wInfo.getMacAddress();
  }
}
