package net.cafepp.cafepp.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.PairedDevicesAdapter;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.databases.DeviceDatabase;
import net.cafepp.cafepp.fragments.ChangeDeviceNameFragment;
import net.cafepp.cafepp.fragments.PairFragment;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.services.ServerService;

import java.io.Serializable;
import java.util.List;

public class DevicesActivity extends AppCompatActivity {
  
  private final String TAG = "DevicesActivity";
  public FrameLayout interlayer;
  private DeviceDatabase deviceDatabase;
  private PairedDevicesAdapter pairedDevicesAdapter;
  private Switch connectSwitch;
  private CheckedTextView checkedTextView;
  private TextView deviceNameTextView;
  private String deviceName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_devices);
    ListView pairedDevicesListView = findViewById(R.id.pairedDevicesListView);
    connectSwitch = findViewById(R.id.connectSwitch);
    checkedTextView = findViewById(R.id.checkedTextView);
    deviceNameTextView = findViewById(R.id.deviceNameTextView);
    interlayer = findViewById(R.id.interlayer);
  
    boolean isServiceRunning = isServiceRunningInForeground(this, ServerService.class);
    Log.d(TAG, "isServiceRunning: " + isServiceRunning);
  
    connectSwitch.setChecked(isServiceRunning);
    setSwitchCheckedChangeListener(connectSwitch);
  
    checkedTextView.setOnClickListener(v -> setTextViewChecked(checkedTextView.isChecked(), true));
    
    deviceNameTextView.setOnClickListener(deviceNameTextViewOnClickListener);
  
    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("DeviceActivity"));
  
    deviceDatabase = new DeviceDatabase(getApplicationContext());
  
    List<Device> devices = deviceDatabase.getDevicesAsServer();
    pairedDevicesAdapter = new PairedDevicesAdapter(devices);
    pairedDevicesAdapter.setConnected(1, true);
  
    pairedDevicesListView.setAdapter(pairedDevicesAdapter);
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    deviceName = getDeviceName();
    deviceNameTextView.setText(deviceName);
  }
  
  @Override
  protected void onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    deviceDatabase.close();
    super.onDestroy();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.home, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    
    return super.onOptionsItemSelected(item);
  }
  
  private void setSwitchCheckedChangeListener(Switch s) {
    s.setOnCheckedChangeListener((buttonView, isChecked) -> {
      Intent serverServiceIntent = new Intent(this, ServerService.class);
  
      // Check checkedTextView false.
      setTextViewChecked(true, false);
      
      if (isChecked) {
        Log.d(TAG, "Switch checked true");
        checkedTextView.setVisibility(View.VISIBLE);
        startService(serverServiceIntent);
      }
      else {
        Log.d(TAG, "Switch checked false");
        pairedDevicesAdapter.setAllDisconnected();
        stopService(serverServiceIntent);
        checkedTextView.setVisibility(View.GONE);
      }
    });
  }
  
  private void setTextViewChecked(boolean isChecked, boolean sendMessage) {
    // Make views unable.
    connectSwitch.setEnabled(false);
    checkedTextView.setEnabled(false);
  
    // Change checkedTextView's mark.
    if (checkedTextView.isChecked())
      checkedTextView.setCheckMarkDrawable(R.drawable.checkboxtrueunabled);
    else checkedTextView.setCheckMarkDrawable(R.drawable.checkboxfalseunabled);
  
    // Change checkedTextView's text.
    String notCheckedText = getResources().getString(R.string.only_visible_to_paired_devices);
    String checkedText = getResources().getString(R.string.visible_to_every_device);
    String text = isChecked ? notCheckedText : checkedText;
  
    checkedTextView.setText(text);
    checkedTextView.setChecked(!isChecked);
    
    // Send Command to Server Service.
    if (sendMessage) {
      Command command = isChecked ? Command.REFUSE_PAIR : Command.ALLOW_PAIR;
      sendMessageToServerService(command);
    }
  }
  
  private View.OnClickListener deviceNameTextViewOnClickListener = v -> {
  
    // Set OnButtonClick listener of the ChangeDeviceNameFragment.
    ChangeDeviceNameFragment.OnButtonClickListener buttonClickListenerCDNF =
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
            editor.putString("deviceNameServer", deviceName);
            editor.apply();
          
            DevicesActivity.this.deviceName = deviceName;
            deviceNameTextView.setText(deviceName);
            getSupportFragmentManager().popBackStack();
            interlayer.setVisibility(View.GONE);
          }
        };
    
    // Open ChangeDeviceNameFragment.
    interlayer.setVisibility(View.VISIBLE);
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
        R.anim.enter_from_bottom, R.anim.exit_to_top)
        .replace(R.id.fragmentContainer, ChangeDeviceNameFragment.newInstance(
            deviceName, buttonClickListenerCDNF), "ChangeDeviceNameFragment")
        .addToBackStack("ChangeDeviceNameFragment");
    ft.commit();
  };
  
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get extra data included in the Intent.
      Package aPackage = (Package) intent.getSerializableExtra("Message");
      Command command = aPackage.getCommand();
      Device sendingDevice = aPackage.getSendingDevice();
      Device receivingDevice = aPackage.getReceivingDevice();
      if (sendingDevice != null && receivingDevice != null)
        sendingDevice.setSocket(receivingDevice.getSocket());
      Log.d(TAG, "BroadcastReceiver command: " + command);
  
      switch (command) {
        case REGISTERED:
          connectSwitch.setEnabled(true);
          checkedTextView.setCheckMarkDrawable(R.drawable.checkbox_selector);
          checkedTextView.setEnabled(true);
          break;
          
        case UNREGISTERED:
          connectSwitch.setEnabled(true);
          checkedTextView.setCheckMarkDrawable(R.drawable.checkbox_selector);
          checkedTextView.setEnabled(true);
          break;
          
        case PAIR_REQ:
          Log.d(TAG, "PAIR_REQ");
          Package answerPackage = new Package(Command.PAIR_ANSWER, null, sendingDevice);
          
          interlayer.setVisibility(View.VISIBLE);
          
          FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
              R.anim.enter_from_bottom, R.anim.exit_to_top)
              .replace(R.id.fragmentContainer, PairFragment.newInstance(
                  answerPackage, mOnButtonClickListenerPF), "PairFragment")
              .addToBackStack("PairFragment")
              .commit();
          break;
          
        case PAIR_CLIENT_DECLINE:
          Log.d(TAG, "PAIR_CLIENT_DECLINE");
          List<Fragment> fragments = getSupportFragmentManager().getFragments();
          for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i) instanceof PairFragment) {
              String fragmentMac = ((PairFragment)fragments.get(i)).getPackage().getReceivingDevice().getMacAddress();
              String packageMac = "";
              if (sendingDevice != null) packageMac = sendingDevice.getMacAddress();
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
          
        case NOT_PAIRED:
          Log.d(TAG, "PAIRED");
          pairedDevicesAdapter.remove(sendingDevice);
          break;
          
        case PAIRED:
          Log.d(TAG, "PAIRED");
          if (receivingDevice != null) receivingDevice.setFound(true);
          pairedDevicesAdapter.add(receivingDevice);
          break;
          
        case CONNECT:
          Log.d(TAG, "CONNECT");
          if (receivingDevice != null) receivingDevice.setConnected(true);
          pairedDevicesAdapter.setConnected(sendingDevice, true);
          break;
          
        case DISCONNECT_CLIENT:
          Log.d(TAG, "DISCONNECT_CLIENT");
          pairedDevicesAdapter.setConnected(sendingDevice, false);
          break;
          
        case UNPAIR_CLIENT:
          Log.d(TAG, "UNPAIR_CLIENT");
          pairedDevicesAdapter.remove(sendingDevice);
          break;
      }
    }
  };
  
  private PairFragment.OnButtonClickListener mOnButtonClickListenerPF =
      new PairFragment.OnButtonClickListener() {
    @Override
    public void onClickPair(Package aPackage) {
      aPackage.setCommand(Command.PAIR_SERVER_ACCEPT);
      sendMessageToServerService(aPackage);
  
      getSupportFragmentManager().popBackStack();
      interlayer.setVisibility(View.GONE);
    }
  
    @Override
    public void onClickDecline(Package aPackage) {
      aPackage.setCommand(Command.PAIR_SERVER_DECLINE);
      sendMessageToServerService(aPackage);
  
      getSupportFragmentManager().popBackStack();
      interlayer.setVisibility(View.GONE);
    }
  };
  
  private void sendMessageToServerService(Serializable serializable) {
    Intent intent = new Intent("ServerService");
    intent.putExtra("Message", serializable);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
  
  private String getDeviceName() {
    SharedPreferences sharedPreferences = getSharedPreferences("ConnectSettings", Context.MODE_PRIVATE);
    return sharedPreferences.getString("deviceNameServer", getString(R.string.main_device));
  }
}
