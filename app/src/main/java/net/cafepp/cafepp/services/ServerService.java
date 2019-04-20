package net.cafepp.cafepp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;
import android.util.Log;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.DevicesActivity;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.CommunicationThread;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.connection.ServerThread;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.objects.PairedDevice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ServerService extends Service {
  
  private static final String TAG = "ServerService";
  private ServerThread serverThread;
  private Thread threadServer;
  private ServerSocket serverSocket;
  private Device myDevice;
  private NsdManager nsdManager;
  private NsdManager.RegistrationListener registrationListener;
  private List<PairedDevice> pairWaitingDevices = new ArrayList<>();
  private List<Device> pairedDevices = new ArrayList<>();
  
  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "Server Service Start Action entered.");
  
    Intent notificationIntent = new Intent(this, DevicesActivity.class);
    notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pNotificationIntent = PendingIntent.getActivity(
        this, 0, notificationIntent, 0);
  
    Intent stopIntent = new Intent(this, ServerService.class);
    stopIntent.setAction(Constants.ACTION.STOP_ACTION);
    PendingIntent pStopIntent = PendingIntent.getActivity(
        this, 0, stopIntent, 0);
  
    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon_round);
  
    Notification notification = new NotificationCompat.Builder(this,
        Constants.NOTIFICATION_ID.SERVER_SERVICE + "")
                                    .setContentTitle(getString(R.string.cafépp_allcaps))
                                    .setTicker(getString(R.string.cafépp_allcaps))
                                    .setContentText(getString(R.string.server))
                                    .setSmallIcon(R.drawable.wifi)
                                    .setLargeIcon(Bitmap.createScaledBitmap(
                                        icon, 128, 128, false))
                                    .setContentIntent(pNotificationIntent)
                                    .setOngoing(true)
                                    .addAction(R.drawable.stop, getString(R.string.stop), pStopIntent)
                                    .build();
    startForeground(Constants.NOTIFICATION_ID.SERVER_SERVICE, notification);
  
    LocalBroadcastManager.getInstance(this).registerReceiver(
        mBroadcastReceiver, new IntentFilter("ServerService"));
  
  
    initializeRegistrationListener();
    try {
      registerService();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void onDestroy() {
    Log.d(TAG, "In onDestroy");
    if (threadServer != null) unregisterService();
    super.onDestroy();
  }
  
  private void registerService() throws UnknownHostException {
    myDevice = new Device();
    myDevice.setDeviceName(getDeviceName())
        .setTablet(getResources().getBoolean(R.bool.isTablet))
        .setIpAddress(getIpAddress())
        .setMacAddress(getMacAddress());
  
    try {
      serverSocket = new ServerSocket(0);
      int port = serverSocket.getLocalPort();
      myDevice.setPort(port);
      Log.d(TAG, "Server socket created! Port: " + port);
    
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "Error Starting Server: " + e.getMessage());
    }
    
    NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
    nsdServiceInfo.setServiceName(myDevice.getDeviceName());
    nsdServiceInfo.setServiceType(myDevice.getServiceType());
    nsdServiceInfo.setHost(InetAddress.getByName(myDevice.getIpAddress()));
    nsdServiceInfo.setPort(myDevice.getPort());
    
    Log.d(TAG, "Registering to nsd service.");
    nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
    nsdManager.registerService(
        nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
  }
  
  public void unregisterService() {
    nsdManager.unregisterService(registrationListener);
  }
  
  private void initializeRegistrationListener() {
    registrationListener = new NsdManager.RegistrationListener() {
      @Override
      public void onServiceRegistered(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Service registered: " + serviceInfo.getServiceName());
        Log.d(TAG, "ServerService is starting!");
        
        serverThread = new ServerThread(serverSocket);
        serverThread.setPackage(new Package(Command.LISTEN, myDevice, null));
        serverThread.setOnPackageInputListener(
            aPackage -> resolvePackage(aPackage));
        
        threadServer = new Thread(serverThread);
        threadServer.start();
      }
  
      @Override
      public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Service unregistered: " + serviceInfo.getServiceName());
        if (!serverSocket.isClosed()){
          threadServer.interrupt();
          threadServer = null;
        }
      }
      
      @Override
      public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(TAG, "Registration failed!: " + serviceInfo.getServiceName() +
                       " Error Code: " + errorCode);
      }
      
      @Override
      public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(TAG, "Unregistration failed!: " + serviceInfo.getServiceName() +
                       " Error Code: " + errorCode);
      }
    };
  }
  
  private void resolvePackage(Package aPackage) {
    Command command = aPackage.getCommand();
    Device targetDevice;
  
    switch (command) {
      case PAIR_REQ:
        Log.d(TAG, "PAIR_REQ");
        PairedDevice pairedDevice = new PairedDevice(aPackage.getSendingDevice());
        pairedDevice.setOnPairedListener(onPairedListener);
        pairWaitingDevices.add(pairedDevice);
        sendPackageToDeviceActivity(aPackage);
        break;
        
      case PAIR_SERVER_ACCEPT:
        Log.d(TAG, "PAIR_SERVER_ACCEPT");
        targetDevice = aPackage.getTargetDevice();
        for (int i = 0; i < pairWaitingDevices.size(); i++) {
          if (pairWaitingDevices.get(i).getMacAddress().equals(targetDevice.getMacAddress())) {
            pairWaitingDevices.get(i).setServerPaired(true);
            break;
          }
        }
  
        // Inform the client that server has accepted to pair.
        sendPackageToClient(aPackage);
        break;
        
      case PAIR_SERVER_DENY:
        Log.d(TAG, "PAIR_SERVER_DENY");
        targetDevice = aPackage.getTargetDevice();
        for (int i = 0; i < pairWaitingDevices.size(); i++) {
          if (pairWaitingDevices.get(i).getMacAddress().equals(targetDevice.getMacAddress())) {
            pairWaitingDevices.remove(i);
            break;
          }
        }
        
        // Inform the client that server has denied to pair.
        sendPackageToClient(aPackage);
        break;
        
      case PAIR_CLIENT_ACCEPT:
        Log.d(TAG, "PAIR_CLIENT_ACCEPT");
        targetDevice = aPackage.getSendingDevice();
        for (int i = 0; i < pairWaitingDevices.size(); i++) {
          if (pairWaitingDevices.get(i).getMacAddress().equals(targetDevice.getMacAddress())) {
            pairWaitingDevices.get(i).setServerPaired(true);
            break;
          }
        }
        break;
        
      case PAIR_CLIENT_DENY:
        Log.d(TAG, "PAIR_CLIENT_DENY");
        break;
    }
  }
  
  private PairedDevice.OnPairedListener onPairedListener = (isBothConfirmed, device) -> {
    if (isBothConfirmed) {
      pairedDevices.add(device);
      
      for (int i = 0; i < pairWaitingDevices.size(); i++) {
        if (pairWaitingDevices.get(i).getMacAddress().equals(device.getMacAddress())) {
          //noinspection SuspiciousListRemoveInLoop
          pairWaitingDevices.remove(i);
          break;
        }
      }
    }
  };
  
  private void sendPackageToClient(Package aPackage) {
      Command command = aPackage.getCommand();
      
      CommunicationThread communicationThread = new CommunicationThread(command);
      communicationThread.setMyDevice(aPackage.getSendingDevice());
      communicationThread.setTargetDevice(aPackage.getTargetDevice());
      new Thread(communicationThread).start();
  }
  
  private void sendPackageToDeviceActivity(Package aPackage) {
    Log.d(TAG, "Sending pair request to device activity");
    Intent intent = new Intent("DeviceActivity");
    intent.putExtra("Message", aPackage);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }
  
  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Object obj = intent.getSerializableExtra("Message");
  
      if (obj != null) {
        if (obj instanceof Package) {
          Package aPackage = (Package) obj;
          resolvePackage(aPackage);
        }
      }
    
    }
  };
  
  private String getIpAddress() {
    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
  }
  
  private String getMacAddress() {
    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    WifiInfo wInfo = wifiManager.getConnectionInfo();
    return wInfo.getMacAddress();
  }
  
  private String getDeviceName() {
    SharedPreferences sharedPreferences = getSharedPreferences("ConnectSettings", Context.MODE_PRIVATE);
    return sharedPreferences.getString("deviceName", getString(R.string.cafepp_device));
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
