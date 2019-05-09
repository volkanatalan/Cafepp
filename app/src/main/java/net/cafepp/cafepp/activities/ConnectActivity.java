package net.cafepp.cafepp.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.AvailableDevicesListViewAdapter;
import net.cafepp.cafepp.adapters.PairedDevicesAdapter;
import net.cafepp.cafepp.enums.ClientType;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.enums.Command;
import net.cafepp.cafepp.databases.DeviceDatabase;
import net.cafepp.cafepp.fragments.ConnectFragment;
import net.cafepp.cafepp.fragments.PairClientFragment;
import net.cafepp.cafepp.fragments.ChangeDeviceNameFragment;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.services.ClientService;

import java.util.List;
import java.util.Random;

public class ConnectActivity extends AppCompatActivity {
  
  private final String TAG = "ConnectActivity";
  public TextView deviceNameTextView;
  private ImageView rediscoverImageView;
  private String deviceName;
  private Device myDevice;
  private Device connectedDevice;
  private ListView pairedDevicesListView, availableDevicesListView;
  private PairedDevicesAdapter pairedDevicesAdapter;
  private AvailableDevicesListViewAdapter availableDevicesAdapter;
  public FrameLayout interlayer;
  private DeviceDatabase deviceDatabase;
  private List<Device> pairedDevices;
  private PairClientFragment pairClientFragment;
  private Menu mMenu;
  private boolean allowInflateMenu = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect);
    deviceNameTextView = findViewById(R.id.deviceNameTextView);
    pairedDevicesListView = findViewById(R.id.pairedDevicesListView);
    availableDevicesListView = findViewById(R.id.availableDevicesListView);
    interlayer = findViewById(R.id.interlayer);
    Switch connectSwitch = findViewById(R.id.connectSwitch);
    rediscoverImageView = findViewById(R.id.rediscoverImageView);
    rediscoverImageView.setOnClickListener(
        v -> sendCommandToClientService(Command.REFRESH));
    
    configureListViews();
    getMyDevice();
  
    boolean isServiceRunning = isServiceRunningInForeground(this, ClientService.class);
    connectSwitch.setChecked(isServiceRunning);
    Log.d(TAG, "Is Client Service running: " + isServiceRunning);
  
    connectSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
  
    deviceNameTextView.setOnClickListener(deviceNameTextViewOnClickListener);
    
    int visibility = isServiceRunning ? View.VISIBLE : View.INVISIBLE;
    rediscoverImageView.setVisibility(visibility);
  
    // Listen for the commands from Client Service.
    LocalBroadcastManager.getInstance(ConnectActivity.this).registerReceiver(
        mMessageReceiver, new IntentFilter("ConnectActivity"));
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    deviceName = getDeviceName();
    deviceNameTextView.setText(deviceName);
  }
  
  @Override
  protected void onDestroy() {
    // Stop listening for the commands from Client Service.
    LocalBroadcastManager.getInstance(ConnectActivity.this).unregisterReceiver(mMessageReceiver);
    availableDevicesAdapter.clear();
    deviceDatabase.close();
    
    super.onDestroy();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    mMenu = menu;
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_done) {
      Intent intent = new Intent(this, ClientActivity.class);
      intent.putExtra("connectedDevice", connectedDevice);
      startActivity(intent);
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
  
        rediscoverImageView.setVisibility(View.VISIBLE);
    
      } else {
        Log.d(TAG, "Switch checked false.");
  
        mMenu.clear();
        rediscoverImageView.setVisibility(View.GONE);
        
        availableDevicesAdapter.clear();
    
        // Stop ClientService.
        stopService(clientServiceIntent);
        pairedDevicesAdapter.setAllDisconnected();
      }
    };
  
  private void configureListViews() {
    // Configure paired devices list view
    deviceDatabase = new DeviceDatabase(this);
    pairedDevices = deviceDatabase.getDevicesAsClient();
    pairedDevicesAdapter = new PairedDevicesAdapter(this, pairedDevices);
    pairedDevicesListView.setOnItemClickListener((parent, view, position, id) -> {
      Device device = pairedDevicesAdapter.getItem(position);
    
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
          R.anim.enter_from_bottom, R.anim.exit_to_top)
          .replace(R.id.fragmentContainer,
              ConnectFragment.newInstance(device, mOnButtonClickListenerCF), "ConnectFragment")
          .addToBackStack("ConnectFragment")
          .commit();
  
      interlayer.setVisibility(View.VISIBLE);
    });
    pairedDevicesListView.setAdapter(pairedDevicesAdapter);
  
  
    // Configure available devices list view
    availableDevicesAdapter = new AvailableDevicesListViewAdapter();
    availableDevicesListView.setOnItemClickListener((parent, view, pos, id) -> {
      
      Device targetDevice = availableDevicesAdapter.getItem(pos);
      myDevice.setPairKey(generatePairKey());
      myDevice.setPort(targetDevice.getPort());
      Package aPackage = new Package(Command.PAIR_REQ, myDevice, targetDevice);
  
      sendPackageToClientService(aPackage);
      
      pairClientFragment = PairClientFragment.newInstance(
          getApplicationContext(), aPackage, mOnButtonClickListener);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
          R.anim.enter_from_bottom, R.anim.exit_to_top);
      ft.replace(R.id.fragmentContainer, pairClientFragment, "PairClientFragment");
      ft.addToBackStack("PairClientFragment");
      ft.commit();
      
      interlayer.setVisibility(View.VISIBLE);
    });
    
    availableDevicesListView.setAdapter(availableDevicesAdapter);
  }
  
  private View.OnClickListener deviceNameTextViewOnClickListener = v -> {
    
    // Set OnButtonClick listener of the ChangeDeviceNameFragment.
    ChangeDeviceNameFragment.OnButtonClickListener listener =
        new ChangeDeviceNameFragment.OnButtonClickListener() {
          @Override
          public void onClickCancel() {
            getSupportFragmentManager().popBackStack();
            interlayer.setVisibility(View.GONE);
          }
          
          @Override
          public void onClickConfirm(String deviceName) {
            SharedPreferences.Editor editor = getSharedPreferences(
                "ConnectSettings", Context.MODE_PRIVATE).edit();
            editor.putString("deviceNameClient", deviceName);
            editor.apply();
            
            ConnectActivity.this.deviceName = deviceName;
            deviceNameTextView.setText(deviceName);
            getSupportFragmentManager().popBackStack();
            interlayer.setVisibility(View.GONE);
          }
        };
    
    // Open ChangeDeviceNameFragment.
    interlayer.setVisibility(View.VISIBLE);
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
        R.anim.enter_from_bottom, R.anim.exit_to_top);
    ft.replace(R.id.fragmentContainer, ChangeDeviceNameFragment.newInstance(deviceName, listener));
    ft.addToBackStack("ConnectActivity");
    ft.commit();
  };
  
  private PairClientFragment.OnButtonClickListener mOnButtonClickListener =
      new PairClientFragment.OnButtonClickListener() {
    @Override
    public void onClickPair(Package aPackage) {
      aPackage.setCommand(Command.PAIR_CLIENT_ACCEPT);
      sendPackageToClientService(aPackage);
      
      getSupportFragmentManager().popBackStack();
      interlayer.setVisibility(View.GONE);
    }
  
    @Override
    public void onClickCancel(Package aPackage) {
      aPackage.setCommand(Command.PAIR_CLIENT_DECLINE);
      sendPackageToClientService(aPackage);
      
      getSupportFragmentManager().popBackStack();
      interlayer.setVisibility(View.GONE);
    }
  };
  
  private ConnectFragment.OnButtonClickListener mOnButtonClickListenerCF =
      new ConnectFragment.OnButtonClickListener() {
        @Override
        public void onClickConnect(Device device) {
          allowInflateMenu = true;
          
          sendPackageToClientService(new Package(Command.CONNECT_CLIENT, myDevice, device));
          
          getSupportFragmentManager().popBackStack();
          interlayer.setVisibility(View.GONE);
        }
        
        @Override
        public void onClickDisconnect(Device device) {
          mMenu.clear();
          allowInflateMenu = true;
          
          sendPackageToClientService(new Package(Command.DISCONNECT_CLIENT, myDevice, device));
          pairedDevicesAdapter.setConnected(device, false);
  
          getSupportFragmentManager().popBackStack();
          interlayer.setVisibility(View.GONE);
        }
        
        @Override
        public void onClickCancel() {
          getSupportFragmentManager().popBackStack();
          interlayer.setVisibility(View.GONE);
        }
        
        @Override
        public void onClickUnpair(Device device) {
          mMenu.clear();
          allowInflateMenu = true;
          
          sendPackageToClientService(new Package(Command.UNPAIR_CLIENT, myDevice, device));
          pairedDevicesAdapter.remove(device);
          deviceDatabase.removeAsClient(device.getMacAddress());
          
          getSupportFragmentManager().popBackStack();
          interlayer.setVisibility(View.GONE);
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
      Log.d(TAG, "Message received.");
      Package aPackage = (Package) intent.getSerializableExtra("Message");
  
      if (aPackage != null) {
        Command command = aPackage.getCommand();
        Device sendingDevice = aPackage.getSendingDevice();
        Device receivingDevice = aPackage.getReceivingDevice();
        Log.d(TAG, "Command: " + command);
  
        switch (command) {
          case FOUND:
            int pos = pairedDevicesAdapter.getPositionByMac(receivingDevice.getMacAddress());
            
            // If there is not any device with the same MAC address in the paired devices
            // list and the device allows to pair, add the found device to available devices.
            if (pos < 0) {
              if (receivingDevice.isAllowPairReq()) {
                availableDevicesAdapter.add(receivingDevice);
                Log.d(TAG, "Device added to availableDevices list: " + receivingDevice.getDeviceName());
              }
            }
            
            else {
              // If there is a device with the same MAC address in the paired
              // devices list and its name is also the same, set it as found.
              boolean hasSameName = pairedDevices.get(pos).getDeviceName().equals(receivingDevice.getDeviceName());
              if (hasSameName) {
                pairedDevicesAdapter.setFound(receivingDevice, true);
                Log.d(TAG, "Device added to pairedDevices list: " + receivingDevice.getDeviceName());
              }
              else {
                // If there is a device with the same MAC address in the paired
                // devices list but its name is different, change the name.
                receivingDevice.setId(pairedDevices.get(pos).getId());
                deviceDatabase.updateAsClient(receivingDevice);
                pairedDevicesAdapter.setFound(receivingDevice, true);
              }
            }
            break;
  
          case LOST:
            pairedDevicesAdapter.setFoundByName(receivingDevice.getDeviceName(), false);
            break;
            
          case CLEAR:
            availableDevicesAdapter.clear();
            break;
            
          case PAIR_SERVER_ACCEPT:
            ClientType clientType = receivingDevice.getClientType();
            if (pairClientFragment != null) pairClientFragment.setClientType(clientType);
            break;
            
          case PAIR_SERVER_DECLINE:
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (int i = 0; i < fragments.size(); i++) {
              if (fragments.get(i) instanceof PairClientFragment) {
                String fragmentMac = ((PairClientFragment)fragments.get(i)).getPackage().getReceivingDevice().getMacAddress();
                String packageMac = sendingDevice.getMacAddress();
                if (fragmentMac.equals(packageMac)) {
                  if (i == fragments.size() - 1) {
                    getSupportFragmentManager().popBackStack();
                    interlayer.setVisibility(View.GONE);
                  } else {
                    fragments.remove(i);
                    break;
                  }
                }
              }
            }
            break;
            
          case PAIRED:
            // Remove paired device from available devices list view.
            availableDevicesAdapter.remove(receivingDevice.getMacAddress());
            // Add paired device to paired devices list view.
            receivingDevice.setFound(true);
            pairedDevicesAdapter.add(receivingDevice);
            break;
            
          case CONNECT_SERVER:
            connectedDevice = sendingDevice;
            pairedDevicesAdapter.setConnected(sendingDevice, true);
            if (allowInflateMenu) {
              allowInflateMenu = false;
              getMenuInflater().inflate(R.menu.done, mMenu);
            }
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
  
  @SuppressLint("HardwareIds")
  private String getMacAddress() {
    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    WifiInfo wInfo = wifiManager.getConnectionInfo();
    return wInfo.getMacAddress();
  }
}
