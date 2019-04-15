package net.cafepp.cafepp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.DevicesActivity;
import net.cafepp.cafepp.connection.ServerThread;
import net.cafepp.cafepp.objects.Device;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ServerService extends Service {
  
  private static final String TAG = "ServerService";
  private ServerThread serverThread;
  private Thread threadServer;
  private ServerSocket serverSocket;
  private Device myDevice;
  private NsdManager nsdManager;
  private NsdManager.RegistrationListener registrationListener;
  
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent.getAction() != null) {
      Log.d(TAG, intent.getAction());
      if (intent.getAction().equals(Constants.ACTION.START_ACTION)) {
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
        
  
        initializeRegistrationListener();
        try {
          registerService();
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
  
      } else if (intent.getAction().equals(Constants.ACTION.STOP_ACTION)) {
        Log.i(TAG, "Server Service Stop Action entered.");
  
        if (threadServer != null) unregisterService();
        
        stopForeground(true);
        stopSelf();
      }
    }
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "In onDestroy");
  }
  
  private void registerService() throws UnknownHostException {
    String macAddress = getMacAddress();
    myDevice = new Device();
    myDevice.setDeviceName(getDeviceName())
        .setServiceType("_cafepp._tcp.")
        .setTablet(getResources().getBoolean(R.bool.isTablet))
        .setMacAddress(macAddress);
    Log.d(TAG, "MAC Address: " + macAddress);
  
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
        serverThread = new ServerThread(ServerService.this, serverSocket);
        serverThread.setMyDevice(myDevice);
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
