package net.cafepp.cafepp.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.DevicesListViewAdapter;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.databases.DeviceDatabase;
import net.cafepp.cafepp.fragments.PairFragment;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.services.ServerService;

import java.util.List;

public class DevicesActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  
  private final String TAG = "DevicesActivity";
  private FrameLayout fragmentContainer;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_devices);
    ListView registeredDevicesListView = findViewById(R.id.registeredDevicesListView);
    Switch connectSwitch = findViewById(R.id.connectSwitch);
    fragmentContainer = findViewById(R.id.fragmentContainerDevicesActivity);
  
    boolean isServiceRunning = isServiceRunningInForeground(this, ServerService.class);
    Log.d(TAG, "isServiceRunning: " + isServiceRunning);
  
    connectSwitch.setChecked(isServiceRunning);
    setCheckedChangeListener(connectSwitch);
    
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
  
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  
    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("DeviceActivity"));
  
    DeviceDatabase deviceDatabase = new DeviceDatabase(getApplicationContext());
  
    if (deviceDatabase.getDevices().size() == 0) {
      deviceDatabase.add(new Device("Aşçı"));
      deviceDatabase.add(new Device("Garson1"));
      deviceDatabase.add(new Device("Garson2"));
      deviceDatabase.add(new Device("Garson3"));
      deviceDatabase.add(new Device("Kasa"));
    }
  
    List<Device> devices = deviceDatabase.getDevices();
    DevicesListViewAdapter adapter = new DevicesListViewAdapter(devices, false);
  
    registeredDevicesListView.setAdapter(adapter);
    adapter.setConnected(1, true);
  }
  
  @Override
  protected void onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    super.onDestroy();
  }
  
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
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
  
  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    
    if (id == R.id.nav_devices) {
      
    } else if (id == R.id.nav_gallery) {
    
    } else if (id == R.id.nav_slideshow) {
    
    } else if (id == R.id.nav_manage) {
    
    } else if (id == R.id.nav_share) {
    
    } else if (id == R.id.nav_send) {
    
    }
    
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
  
  private void setCheckedChangeListener(Switch s) {
    s.setOnCheckedChangeListener((buttonView, isChecked) -> {
      Intent serverServiceIntent = new Intent(this, ServerService.class);
      
      if (isChecked) {
        Log.d(TAG, "Switch checked true");
        startService(serverServiceIntent);
        
      } else {
        Log.d(TAG, "Switch checked false");
        stopService(serverServiceIntent);
        
      }
    });
  }
  
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get extra data included in the Intent
      Package aPackage = (Package) intent.getSerializableExtra("Message");
      Command command = aPackage.getCommand();
      Log.d(TAG, "BroadcastReceiver command: " + command);
  
      switch (command) {
        case PAIR_REQ:
          Log.d(TAG, "PAIR_REQ");
          Device targetDevice = aPackage.getSendingDevice();
          Package answerPackage = new Package(Command.PAIR_ANSWER, null, targetDevice);
  
          fragmentContainer.setVisibility(View.VISIBLE);
  
          FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          ft.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
              R.anim.enter_from_bottom, R.anim.exit_to_top)
              .replace(R.id.fragmentContainerDevicesActivity, PairFragment.newInstance(
                  answerPackage, mOnButtonClickListener), "PairFragment")
              .addToBackStack("PairFragment")
              .commit();
          break;
          
        case PAIR_CLIENT_DENY:
          Log.d(TAG, "PAIR_CLIENT_DENY");
          fragmentContainer.setVisibility(View.GONE);
          break;
          
        case PAIRED:
          Log.d(TAG, "PAIRED");
          fragmentContainer.setVisibility(View.GONE);
          break;
      }
    }
  };
  
  private PairFragment.OnButtonClickListener mOnButtonClickListener =
      new PairFragment.OnButtonClickListener() {
    @Override
    public void onClickPair(Package aPackage) {
      aPackage.setCommand(Command.PAIR_SERVER_ACCEPT);
      sendMessageToServerService(aPackage);
    }
  
    @Override
    public void onClickCancel(Package aPackage) {
      aPackage.setCommand(Command.PAIR_SERVER_DENY);
      sendMessageToServerService(aPackage);
    }
  };
  
  private void sendMessageToServerService(Package aPackage) {
    Intent intent = new Intent("ServerService");
    intent.putExtra("Message", aPackage);
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
}
