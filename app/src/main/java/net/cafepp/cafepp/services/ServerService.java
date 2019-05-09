package net.cafepp.cafepp.services;

import android.annotation.SuppressLint;
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
import net.cafepp.cafepp.enums.Command;
import net.cafepp.cafepp.connection.CommunicationThread;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.connection.ServerThread;
import net.cafepp.cafepp.databases.DeviceDatabase;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.objects.PairDevice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ServerService extends Service {
  
  private static final String TAG = "ServerService";
  private boolean isReregistering = false;
  private boolean allowUnregister = false;
  private ServerThread serverThread;
  private Thread thread;
  private ServerSocket serverSocket;
  private Device myDevice;
  private NsdServiceInfo nsdServiceInfo;
  private NsdManager nsdManager;
  private NsdManager.RegistrationListener registrationListener;
  private List<PairDevice> pairWaitingDevices = new ArrayList<>();
  private List<Device> pairedDevices = new ArrayList<>();
  private List<Device> connectedDevices = new ArrayList<>();
  
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
                                    .setSmallIcon(R.drawable.wifi_connected)
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
    registerService();
  }
  
  @Override
  public void onDestroy() {
    Log.d(TAG, "In onDestroy");
    if (thread != null) unregisterService();
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    super.onDestroy();
  }
  
  private void registerService(){
    myDevice = new Device();
    myDevice.setDeviceName(getDeviceName())
        .setTablet(getResources().getBoolean(R.bool.isTablet))
        .setIpAddress(getIpAddress())
        .setMacAddress(getMacAddress());
  
    try {
      serverSocket = new ServerSocket(myDevice.getPort());
      int port = serverSocket.getLocalPort();
      myDevice.setPort(port);
      Log.d(TAG, "Server socket created! Port: " + port);
    
      nsdServiceInfo = new NsdServiceInfo();
      nsdServiceInfo.setServiceName(myDevice.getDeviceName());
      nsdServiceInfo.setServiceType(myDevice.getServiceType());
      nsdServiceInfo.setHost(InetAddress.getByName(myDevice.getIpAddress()));
      nsdServiceInfo.setPort(myDevice.getPort());
      
      Log.d(TAG, "Registering to NSD service.");
      nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
      nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
  
    } catch (IOException e) {
      Log.e(TAG, "Error Starting Server: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private void unregisterService() {
    Log.d(TAG, "In unregisterService");
    if (allowUnregister){
      Log.d(TAG, "Unregistering is allowed.");
      allowUnregister = false;
      nsdManager.unregisterService(registrationListener);
    } else
      Log.e(TAG, "Unregistering is not allowed.");
  }
  
  private void initializeRegistrationListener() {
    registrationListener = new NsdManager.RegistrationListener() {
      @Override
      public void onServiceRegistered(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Service registered: " + serviceInfo.getServiceName());
        
        serverThread = new ServerThread(serverSocket);
        serverThread.setPackage(new Package(Command.LISTEN, myDevice, null));
        serverThread.setOnPackageInputListener(
            aPackage -> resolvePackage(aPackage));
        
        thread = new Thread(serverThread);
        thread.start();
  
        allowUnregister = true;
        
        sendPackageToDeviceActivity(new Package(Command.REGISTERED, null, null));
      }
  
      @Override
      public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Service unregistered: " + serviceInfo.getServiceName());
        
        if (!serverSocket.isClosed() && thread != null){
          thread.interrupt();
          thread = null;
        }
        
        if (isReregistering) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Log.d(TAG, "Reregistering to NSD service.");
          isReregistering = false;
          nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
          
        } else {
          sendPackageToDeviceActivity(new Package(Command.UNREGISTERED, null, null));
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
  
  private void reregister() {
    Log.d(TAG, "In reregister.");
    isReregistering = true;
    unregisterService();
  }
  
  private void yesSir(Command command) {
    switch (command) {
      case ALLOW_PAIR:
        Log.d(TAG, "In ALLOW_PAIR.");
        myDevice.setAllowPairReq(true);
        reregister();
        break;
        
      case REFUSE_PAIR:
        Log.d(TAG, "In REFUSE_PAIR.");
        myDevice.setAllowPairReq(false);
        reregister();
        break;
    }
  }
  
  private void resolvePackage(Package aPackage) {
    Command command = aPackage.getCommand();
    Device sendingDevice = aPackage.getSendingDevice();
    Device receivingDevice = aPackage.getReceivingDevice();
    int position;
  
    switch (command) {
      case PAIR_REQ:
        Log.d(TAG, "PAIR_REQ");
        PairDevice pairDevice = new PairDevice(sendingDevice);
        pairDevice.setOnPairedListener(onPairedListener);
        pairWaitingDevices.add(pairDevice);
        sendPackageToDeviceActivity(aPackage);
        break;
        
      case NOT_PAIRED:
        Log.d(TAG, "NOT_PAIRED");
        position = getPDListPositionByMac(sendingDevice.getMacAddress());
        if (position > -1) {
          pairedDevices.remove(position);
          sendPackageToDeviceActivity(aPackage);
          DeviceDatabase deviceDatabase = new DeviceDatabase(this);
          deviceDatabase.removeAsServer(sendingDevice.getMacAddress());
        }
        break;
        
      case PAIR_SERVER_ACCEPT:
        Log.d(TAG, "PAIR_SERVER_ACCEPT");
        position = getPWDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) {
          pairWaitingDevices.get(position).setClientType(receivingDevice.getClientType());
          pairWaitingDevices.get(position).setServerPaired(true);
        }
        pairedDevices.add(receivingDevice);
        sendPackageToClient(aPackage);
        break;
        
      case PAIR_SERVER_DECLINE:
        Log.d(TAG, "PAIR_SERVER_DECLINE");
        position = getPWDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) pairWaitingDevices.remove(position);
        sendPackageToClient(aPackage);
        break;
        
      case PAIR_CLIENT_ACCEPT:
        Log.d(TAG, "PAIR_CLIENT_ACCEPT");
        position = getPWDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) {
          pairWaitingDevices.get(position).setClientType(sendingDevice.getClientType());
          pairWaitingDevices.get(position).setClientPaired(true);
        }
        sendPackageToDeviceActivity(aPackage);
        break;
        
      case PAIR_CLIENT_DECLINE:
        Log.d(TAG, "PAIR_CLIENT_DECLINE");
        position = getPWDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) pairWaitingDevices.remove(position);
        sendPackageToDeviceActivity(aPackage);
        break;
        
      case CONNECT_CLIENT:
        Log.d(TAG, "CONNECT_CLIENT");
        connectedDevices.add(sendingDevice);
        sendPackageToDeviceActivity(aPackage);
        
        // Inform client that server has connected.
        Package informPackage = new Package(Command.CONNECT_SERVER, receivingDevice, sendingDevice);
        sendPackageToClientWithSocket(informPackage);
        break;
        
      case DISCONNECT_CLIENT:
        Log.d(TAG, "DISCONNECT_CLIENT");
        position = getCDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) connectedDevices.remove(position);
        sendPackageToDeviceActivity(aPackage);
        break;
        
      case DISCONNECT_SERVER:
        Log.d(TAG, "DISCONNECT_SERVER");
        position = getCDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) connectedDevices.remove(position);
        sendPackageToClient(aPackage);
        break;
        
      case UNPAIR_CLIENT:
        Log.d(TAG, "UNPAIR_CLIENT");
        position = getCDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) connectedDevices.remove(position);
        position = getPDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) pairedDevices.remove(position);
        sendPackageToDeviceActivity(aPackage);
        break;
        
      case UNPAIR_SERVER:
        Log.d(TAG, "UNPAIR_SERVER");
        position = getCDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) connectedDevices.remove(position);
        position = getPDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) pairedDevices.remove(position);
        sendPackageToClient(aPackage);
        break;
    }
  }
  
  private PairDevice.OnPairedListener onPairedListener = (isBothConfirmed, device) -> {
    if (isBothConfirmed) {
      // Add device to pairedDevices list.
      pairedDevices.add(device);
  
      // Remove device from pairWaitingDevices list.
      int pos = getPWDListPositionByMac(device.getMacAddress());
      pairWaitingDevices.remove(pos);
      
      // Add device to database.
      DeviceDatabase deviceDatabase = new DeviceDatabase(this);
      deviceDatabase.addAsServer(device);
  
      // Inform Devices Activity that there is a paired device.
      Package aPackage = new Package(Command.PAIRED, null, device);
      sendPackageToDeviceActivity(aPackage);
    }
  };
  
  private int getPWDListPositionByMac(String macAddress) {
    if (pairWaitingDevices.size() > 0) {
      for (int i = 0; i < pairWaitingDevices.size(); i++) {
        String mac = pairWaitingDevices.get(i).getMacAddress();
        if (mac.equals(macAddress)) {
          return i;
          
        } else {
          Log.e(TAG, "There is not any device with the same MAC address in pairWaitingDevices list.");
        }
      }
    } else {
      Log.e(TAG, "pairWaitingDevices list is empty.");
    }
    return -1;
  }
  
  private int getPDListPositionByMac(String macAddress) {
    if (pairedDevices.size() > 0) {
      for (int i = 0; i < pairedDevices.size(); i++) {
        String mac = pairedDevices.get(i).getMacAddress();
        if (mac.equals(macAddress)) {
          return i;
          
        } else {
          Log.e(TAG, "There is not any device with the same MAC address in pairedDevices list.");
        }
      }
    } else {
      Log.e(TAG, "pairedDevices list is empty.");
    }
    return -1;
  }
  
  private int getCDListPositionByMac(String macAddress) {
    if (connectedDevices.size() > 0) {
      for (int i = 0; i < connectedDevices.size(); i++) {
        String mac = connectedDevices .get(i).getMacAddress();
        if (mac.equals(macAddress)) {
          return i;
        }
        else {
          Log.e(TAG, "There is not any device with the same MAC address in connectedDevices list.");
        }
      }
    }
    else {
      Log.e(TAG, "connectedDevices list is empty.");
    }
    return -1;
  }
  
  private void sendPackageToClient(Package aPackage) {
    aPackage.setSendingDevice(myDevice);
    aPackage.getReceivingDevice().setPort(myDevice.getPort());
    
    CommunicationThread communicationThread = new CommunicationThread(aPackage);
    new Thread(communicationThread).start();
  }
  
  private void sendPackageToClientWithSocket(Package aPackage) {
    aPackage.getReceivingDevice().setPort(myDevice.getPort());
    CommunicationThread communicationThread = new CommunicationThread(aPackage);
    communicationThread.setSocket(aPackage.getSendingDevice().getSocket());
    new Thread(communicationThread).start();
  }
  
  private void sendPackageToDeviceActivity(Package aPackage) {
    Log.d(TAG, "Sending package to device activity: " + aPackage.getCommand());
    Intent intent = new Intent("DeviceActivity");
    intent.putExtra("Message", aPackage);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }
  
  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Object obj = intent.getSerializableExtra("Message");
  
      if (obj != null) {
        if (obj instanceof Command) {
          yesSir((Command) obj);
          
        } else if (obj instanceof Package) {
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
  
  @SuppressLint("HardwareIds")
  private String getMacAddress() {
    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    WifiInfo wInfo = wifiManager.getConnectionInfo();
    return wInfo.getMacAddress();
  }
  
  private String getDeviceName() {
    SharedPreferences sharedPreferences = getSharedPreferences("ConnectSettings", Context.MODE_PRIVATE);
    return sharedPreferences.getString("deviceNameServer", getString(R.string.main_device));
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
