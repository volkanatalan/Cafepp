package net.cafepp.cafepp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.ConnectActivity;
import net.cafepp.cafepp.connection.CommunicationThread;
import net.cafepp.cafepp.connection.CommunicationThread.Command;
import net.cafepp.cafepp.connection.NSDHelper;
import net.cafepp.cafepp.objects.Device;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientService extends Service {
  
  private static final String TAG = "ClientService";
  private Context context;
  private NSDHelper nsdHelper;
  private boolean isRestartingDiscovery = false;
  private Thread threadCommunication;
  private List<Device> foundDevices = new ArrayList<>();
  
  
  public ClientService() {
    context = this;
  }
  
  /**
   * Handler of incoming messages from clients.
   */
  static class IncomingHandler extends Handler {
    private Context applicationContext;
    
    IncomingHandler(Context context) {
      applicationContext = context.getApplicationContext();
    }
    
    @Override
    public void handleMessage(Message msg) {
      if (msg.obj != null) {
        if (msg.obj instanceof Device) {
          Device device = (Device) msg.obj;
          Log.i(TAG, device.getDeviceName() + " " + device.getPairKey());
          
        } else if (msg.obj instanceof Command) {
          Command command = (Command) msg.obj;
          
          switch (command) {
            case INFO_REQ:
              
              break;
            default:
              Log.e(TAG, "Unknown command");
          }
        }
      } else super.handleMessage(msg);
    }
  }
  
  /**
   * Target we publish for clients to send messages to IncomingHandler.
   */
  private Messenger mMessenger;
  
  
  @Override
  public IBinder onBind(Intent intent) {
    mMessenger = new Messenger(new IncomingHandler(this));
    return mMessenger.getBinder();
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
  
    Log.i(TAG, "In Client Service onCreate.");
  
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
  
  
    // Listen for the commands from Client Service.
    LocalBroadcastManager.getInstance(context).registerReceiver(
        mMessageReceiver, new IntentFilter("ClientService"));
  
    nsdHelper = new NSDHelper(this);
    setDiscoveryListener();
    setResolveListener();
    nsdHelper.startDiscovery();
  }
  
  @Override
  public void onDestroy() {
    Log.d(TAG, "In onDestroy");
    nsdHelper.stopDiscovery();
    if (threadCommunication != null && !threadCommunication.isInterrupted())
      threadCommunication.interrupt();
    LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    stopForeground(true);
    stopSelf();
    super.onDestroy();
  }
  
  private void addFoundDevice(Device device) {
    Log.i(TAG, "Device \"" + device.getDeviceName() + "\" is being added to the list.");
    for (Device d : foundDevices)
      if (d.getMacAddress().equals(device.getMacAddress())) {
        Log.e(TAG, "Attempt to add a device with the same MAC address.");
        return;
      }
  
    foundDevices.add(device);
    Log.i(TAG, "Add successful!");
  
    sendMessageToActivity("ADD", device);
  }
  
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get extra data included in the Intent
      String command = intent.getStringExtra("Command");
      Device device = (Device) intent.getBundleExtra("Message").getSerializable("Message");
      Log.d(TAG, "BroadcastReceiver command: " + command);
      
      if (command.equals("ADD")) addFoundDevice(device);
    }
  };
  
  private void sendMessageToActivity(String command, Device device) {
    Log.i(TAG, "Message sending to activity");
    Intent intent = new Intent("ConnectActivity");
    intent.putExtra("Command", command);
    Bundle bundle = new Bundle();
    bundle.putSerializable("Message", device);
    intent.putExtra("Message", bundle);
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
    sendMessageToActivity("CLEAR", null);
    foundDevices.clear();
    nsdHelper.stopDiscovery();
  }
  
  private void setResolveListener() {
    if (nsdHelper.getResolveListener() != null) return;
    
    nsdHelper.setResolveListener(new NSDHelper.ResolveListener() {
      @Override
      public void onResolved(final NsdServiceInfo serviceInfo) throws IOException {
        Socket socket = null;
        Log.d(TAG, "In onResolved");
        
        try {
          socket = new Socket(serviceInfo.getHost(), serviceInfo.getPort());
          CommunicationThread communicationThread = new CommunicationThread(context, socket, Command.INFO_REQ);
          communicationThread.setNsdServiceInfo(serviceInfo);
          threadCommunication = new Thread(communicationThread);
          threadCommunication.start();
  
        } catch(Exception e){
          if (socket != null && !socket.isClosed())
          socket.close();
          e.printStackTrace();
        }
      }
      
      @Override
      public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
      }
    });
  }
}