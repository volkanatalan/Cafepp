package net.cafepp.cafepp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.ConnectActivity;
import net.cafepp.cafepp.connection.NSDHelper;

import java.util.ArrayList;
import java.util.List;

public class ClientService extends Service {
  
  private static final String TAG = "ClientService";
  private Context context;
  private String deviceName;
  private NSDHelper nsdHelper;
  private boolean isRestartingDiscovery = false;
  private List<NsdServiceInfo> foundDevices = new ArrayList<>();
  
  
  ClientService(Context context) {
    this.context = context;
  }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent.getAction()!=null) {
      Log.d(TAG, intent.getAction());
      if (intent.getAction().equals(Constants.ACTION.START_ACTION)) {
  
        Log.i(TAG, "Client Service Start Action entered.");
  
        deviceName = intent.getStringExtra("deviceName");
        
        Intent notificationIntent = new Intent(this, ConnectActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0);
        
        Intent stopIntent = new Intent(this, ClientService.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        PendingIntent pstopIntent = PendingIntent.getActivity(
            this, 0, stopIntent, 0);
        
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon_round);
        
        Notification notification = new NotificationCompat.Builder(this,
            Constants.NOTIFICATION_ID.CLIENT_SERVICE + "")
                                        .setContentTitle(getString(R.string.cafépp_allcaps))
                                        .setTicker(getString(R.string.cafépp_allcaps))
                                        .setContentText(getString(R.string.client))
                                        .setSmallIcon(R.drawable.wifi)
                                        .setLargeIcon(Bitmap.createScaledBitmap(
                                            icon, 128, 128, false))
                                        .setContentIntent(pendingIntent)
                                        .setOngoing(true)
                                        .addAction(R.drawable.stop, getString(R.string.stop), pstopIntent)
                                        .build();
        startForeground(Constants.NOTIFICATION_ID.CLIENT_SERVICE, notification);
  
        nsdHelper = new NSDHelper(this);
        nsdHelper.register(deviceName);
        setDiscoveryListener();
        setResolveListener();
        nsdHelper.startDiscovery();
        
      } else if (intent.getAction().equals(Constants.ACTION.STOP_ACTION)) {
        Log.i(TAG, "Client Service Stop Action entered.");
        
        nsdHelper.unregister();
        stopForeground(true);
        stopSelf();
      }
    }
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
    Log.d(TAG, "In onDestroy");
    nsdHelper.unregister();
    super.onDestroy();
  }
  
  private void setDiscoveryListener() {
    if (nsdHelper.getDiscoveryListener() != null) return;
    
    nsdHelper.setDiscoveryListener(new NSDHelper.DiscoveryListener() {
      @Override
      public void onDiscoveryStarted(String regType) {
      }
      
      @Override
      public void onServiceFound(NsdServiceInfo service) {
      }
      
      @Override
      public void onServiceLost(NsdServiceInfo service) {
        Log.d(TAG, "Lost service: " + service.getServiceName());
        foundDevices.clear();
        if (!isRestartingDiscovery) restartDiscovery();
      }
      
      @Override
      public void onDiscoveryStopped(String serviceType) {
        if (isRestartingDiscovery) {
          isRestartingDiscovery = false;
          nsdHelper.startDiscovery();
        }
      }
      
      @Override
      public void onStartDiscoveryFailed(String serviceType, int errorCode) {
      }
      
      @Override
      public void onStopDiscoveryFailed(String serviceType, int errorCode) {
      }
    });
  }
  
  private void restartDiscovery() {
    Log.d(TAG, "Restarting discovery!");
    isRestartingDiscovery = true;
    nsdHelper.stopDiscovery();
  }
  
  private void setResolveListener() {
    if (nsdHelper.getResolveListener() != null) return;
    
    nsdHelper.setResolveListener(new NSDHelper.ResolveListener() {
      @Override
      public void onResolved(final NsdServiceInfo serviceInfo) {
        foundDevices.add(serviceInfo);
      }
      
      @Override
      public void onResolvedMyDevice(NsdServiceInfo serviceInfo) {
      }
      
      @Override
      public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
      }
    });
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}