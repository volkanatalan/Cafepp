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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.ConnectActivity;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.CommunicationThread;
import net.cafepp.cafepp.connection.NSDHelper;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.objects.PairedDevice;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientService extends Service {
  
  private static final String TAG = "ClientService";
  private Context context;
  private NSDHelper nsdHelper;
  private boolean isRestartingDiscovery = false;
  private static Thread threadCommunication;
  private List<Device> foundDevices = new ArrayList<>();
  private static List<PairedDevice> pairWaitingDevices = new ArrayList<>();
  private List<Device> pairedDevices = new ArrayList<>();
  
  
  public ClientService() {
    context = this;
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
    super.onDestroy();
  }
  
  private void sendPackageToServer(Package aPackage, CommunicationThread.OnOutputListener listener) {
    Command command = aPackage.getCommand();
    Device myDevice = aPackage.getSendingDevice();
    Device targetDevice = aPackage.getTargetDevice();
    
    CommunicationThread communicationThread = new CommunicationThread(command);
    communicationThread.setMyDevice(myDevice);
    communicationThread.setTargetDevice(targetDevice);
    communicationThread.setOnOutputListener(listener);
    communicationThread.setOnInputListener(aPackage1 -> resolvePackage(aPackage));
    threadCommunication = new Thread(communicationThread);
    threadCommunication.start();
  }
  
  private PairedDevice.OnPairedListener onPairedListener = (isBothConfirmed, device) -> {
    if (isBothConfirmed) {
      Package aPackage = new Package(Command.PAIRED, null, device);
      sendMessageToActivity(aPackage);
    }
  };
  
  private void addFoundDevice(Device device) {
    for (Device d : foundDevices)
      if (d.getMacAddress().equals(device.getMacAddress())) {
        Log.e(TAG, "Attempt to add a device with the same MAC address.");
        return;
      }
  
    foundDevices.add(device);
    Log.i(TAG, "Add successful!");
  
    Package aPackage = new Package(Command.FOUND, null, device);
  
    sendMessageToActivity(aPackage);
  }
  
  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Object obj = intent.getSerializableExtra("Message");
  
      if (obj != null) {
        if (obj instanceof Device) {
          Device device = (Device) obj;
          Log.i(TAG, device.getDeviceName() + " " + device.getPairKey());
      
        } else if (obj instanceof Command) {
          Command command = (Command) obj;
      
          switch (command) {
            case INFO_REQ:
              break;
              
            case REFRESH:
              if (!isRestartingDiscovery) restartDiscovery();
              break;
              
            default:
              Log.e(TAG, "Unknown command");
          }
      
        } else if (obj instanceof Package) {
          Package aPackage = (Package) obj;
          resolvePackage(aPackage);
      
        }
      }
    }
  };
  
  private void resolvePackage(Package pkg) {
    Command command = pkg.getCommand();
    switch (command) {
      case PAIR_REQ:
        sendPackageToServer(pkg, aPackage -> {
          pkg.setCommand(Command.LISTEN);
          sendPackageToServer(pkg, null);
        });
        
        PairedDevice pairedDevice = new PairedDevice(pkg.getTargetDevice());
        pairedDevice.setOnPairedListener(onPairedListener);
        pairWaitingDevices.add(pairedDevice);
        break;
      
      case PAIR_SERVER_ACCEPT:
        Log.d(TAG, "Server accepted to pair.");
        break;
      
      case PAIR_SERVER_DENY:
        Log.d(TAG, "Server denied to pair.");
        break;
      
      case PAIR_CLIENT_ACCEPT:
        Log.d(TAG, "Client accepted to pair.");
        break;
      
      case PAIR_CLIENT_DENY:
        Log.d(TAG, "Client denied to pair.");
        break;
    }
  }
  
  private void sendMessageToActivity(Package aPackage) {
    Log.i(TAG, "Message sending to activity");
    Intent intent = new Intent("ConnectActivity");
    intent.putExtra("Message", aPackage);
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
    foundDevices.clear();
    sendMessageToActivity(new Package(Command.CLEAR, null, null));
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
          
          CommunicationThread communicationThread = new CommunicationThread(socket, Command.INFO_REQ);
          communicationThread.setOnInputListener(
              aPackage -> addFoundDevice(aPackage.getSendingDevice()));
          
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
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}