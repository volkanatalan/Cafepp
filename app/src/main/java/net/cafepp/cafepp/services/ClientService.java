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
import net.cafepp.cafepp.connection.ClientThread;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.CommunicationThread;
import net.cafepp.cafepp.connection.NSDHelper;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.databases.DeviceDatabase;
import net.cafepp.cafepp.objects.Device;
import net.cafepp.cafepp.objects.PairDevice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientService extends Service {
  
  private static final String TAG = "ClientService";
  private Context context;
  private NSDHelper nsdHelper;
  private boolean isRestartingDiscovery = false;
  private static Thread threadCommunication, threadClient;
  private ClientThread clientThread;
  private List<Device> foundDevices = new ArrayList<>();
  private static List<PairDevice> pairWaitingDevices = new ArrayList<>();
  private List<Device> pairedDevices;
  private List<Device> connectedDevices = new ArrayList<>();
  
  
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
                                    .setSmallIcon(R.drawable.wifi_connected)
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
    
    DeviceDatabase deviceDatabase = new DeviceDatabase(this);
    pairedDevices = deviceDatabase.getDevicesAsClient();
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
    Device sendingDevice = pkg.getSendingDevice();
    Device receivingDevice = pkg.getReceivingDevice();
    int position;
    
    switch (command) {
      case PAIR_REQ:
        Log.d(TAG, "PAIR_REQ");
        pkg.setCommand(Command.PAIR_REQ);
        communicateWithServer(pkg);
        
        PairDevice pairDevice = new PairDevice(receivingDevice);
        pairDevice.setOnPairedListener(onPairedListener);
        pairWaitingDevices.add(pairDevice);
        break;
        
      case PAIR_SERVER_ACCEPT:
        Log.d(TAG, "PAIR_SERVER_ACCEPT");
        position = getPWDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) pairWaitingDevices.get(position).setServerPaired(true);
        break;
        
      case PAIR_SERVER_DECLINE:
        Log.d(TAG, "PAIR_SERVER_DECLINE");
        position = getPWDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          pairWaitingDevices.remove(position);
          
          // Inform ConnectActivity that server declined to pair.
          sendMessageToActivity(pkg);
        }
        break;
        
      case PAIR_CLIENT_ACCEPT:
        Log.d(TAG, "PAIR_CLIENT_ACCEPT");
        position = getPWDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) {
          pairWaitingDevices.get(position).setClientPaired(true);
          communicateWithServer(pkg);
        }
        break;
        
      case PAIR_CLIENT_DECLINE:
        Log.d(TAG, "PAIR_CLIENT_DECLINE");
        position = getPWDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          pairWaitingDevices.remove(position);
          communicateWithServer(pkg);
        }
        break;
        
      case CONNECT:
        Log.d(TAG, "CONNECT");
        connectedDevices.add(receivingDevice);
        listenToServer(pkg);
        communicateWithServer(pkg);
        break;
        
      case DISCONNECT_SERVER:
        Log.d(TAG, "DISCONNECT_SERVER");
        position = getCDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          connectedDevices.remove(position);
          stopListeningToServer();
          sendMessageToActivity(pkg);
        }
        break;
        
      case DISCONNECT_CLIENT:
        Log.d(TAG, "DISCONNECT_CLIENT");
        position = getCDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          connectedDevices.remove(position);
          stopListeningToServer();
          communicateWithServer(pkg);
        }
        break;
        
      case UNPAIR_SERVER:
        Log.d(TAG, "UNPAIR_SERVER");
        position = getCDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          connectedDevices.remove(position);
        }
        position = getPDListPositionByMac(sendingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          pairedDevices.remove(position);
        }
        stopListeningToServer();
        sendMessageToActivity(pkg);
        break;
        
      case UNPAIR_CLIENT:
        Log.d(TAG, "UNPAIR_CLIENT");
        position = getCDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          connectedDevices.remove(position);
        }
        position = getPDListPositionByMac(receivingDevice.getMacAddress());
        if (position != -1) {
          // Remove the device from list.
          pairedDevices.remove(position);
        }
        stopListeningToServer();
        communicateWithServer(pkg);
        restartDiscovery();
        break;
    }
  }
  
  private PairDevice.OnPairedListener onPairedListener = (isBothPaired, device) -> {
    if (isBothPaired) {
      Log.d(TAG, "Device is paired: " + device.getDeviceName());
  
      int pos = getPWDListPositionByMac(device.getMacAddress());
      if (pos > -1) {
        pairWaitingDevices.remove(pos);
      }
  
      DeviceDatabase deviceDatabase = new DeviceDatabase(this);
      deviceDatabase.addAsClient(device);
      
      Package aPackage = new Package(Command.PAIRED, null, device);
      sendMessageToActivity(aPackage);
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
      Log.d(TAG, "pairWaitingDevices list is empty.");
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
        String mac = connectedDevices.get(i).getMacAddress();
        if (mac.equals(macAddress)) {
          return i;
          
        } else {
          Log.e(TAG, "There is not any device with the same MAC address in connectedDevices list.");
        }
      }
    } else {
      Log.e(TAG, "connectedDevices list is empty.");
    }
    return -1;
  }
  
  private void communicateWithServer(Package aPackage) {
    CommunicationThread communicationThread = new CommunicationThread(aPackage);
    communicationThread.setOnInputListener(this::resolvePackage);
    threadCommunication = new Thread(communicationThread);
    threadCommunication.start();
  }
  
  private void listenToServer(Package aPackage) {
    clientThread = new ClientThread(aPackage);
    clientThread.setOnPackageInputListener(this::resolvePackage);
    threadClient = new Thread(clientThread);
    threadClient.setDaemon(true);
    threadClient.start();
  }
  
  private void stopListeningToServer() {
    if (clientThread != null) clientThread.closeSocket();
  }
  
  private void sendMessageToActivity(Package aPackage) {
    Log.i(TAG, "Message sending to activity.");
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
        // Inform activity that a device is lost.
        sendMessageToActivity(new Package(Command.LOST, null, new Device(service.getServiceName())));
        // Restart discovery.
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
          socket = new Socket();
          socket.connect(new InetSocketAddress(serviceInfo.getHost(), serviceInfo.getPort()));
          
          CommunicationThread communicationThread =
              new CommunicationThread(socket, Command.INFO_REQ, null);
          communicationThread.setOnInputListener(aPackage -> {
                addFoundDevice(aPackage.getSendingDevice());
                sendNotPairedMessage(aPackage);
          });
          
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
  
  private void addFoundDevice(Device device) {
    // If found device is paired before, set it as found in pairedDevices list.
    if (pairedDevices.size() > 0) {
      for (int i = 0; i < pairedDevices.size(); i++) {
        if (pairedDevices.get(i).getMacAddress().equals(device.getMacAddress())) {
          Log.d(TAG, "A paired device found: " + pairedDevices.get(i).getDeviceName());
          device.setFound(true);
          pairedDevices.set(i, device);
          
          Package aPackage = new Package(Command.FOUND, null, device);
          sendMessageToActivity(aPackage);
          return;
        }
      }
    }
    
    // If found device is not paired before and there is a device with
    // the same MAC address in foundDevices list, don't add it to any list.
    if (foundDevices.size() > 0) {
      for (Device d : foundDevices) {
        if (d.getMacAddress().equals(device.getMacAddress())) {
          Log.e(TAG, "Attempt to add a device with the same MAC address.");
          return;
        }
      }
    }
    
    // If found device is not paired before and there is not a device with
    // the same MAC address in foundDevices list, add it  to foundDevices list.
    foundDevices.add(device);
    Log.i(TAG, "Found device: " + device.getDeviceName());
    
    Package aPackage = new Package(Command.FOUND, null, device);
    sendMessageToActivity(aPackage);
  }
  
  private void sendNotPairedMessage(Package aPackage) {
    if (pairedDevices.size() > 0) {
      for (Device device : pairedDevices){
        if (aPackage.getSendingDevice().getMacAddress().equals(device.getMacAddress()))
          return;
      }
      
      Package sendingPackage = new Package(
          Command.NOT_PAIRED, aPackage.getReceivingDevice(), aPackage.getSendingDevice());
      communicateWithServer(sendingPackage);
      Log.d(TAG, "NOT_PAIRED command sent: " + aPackage.getReceivingDevice().getDeviceName());
    }
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}