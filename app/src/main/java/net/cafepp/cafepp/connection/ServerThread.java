package net.cafepp.cafepp.connection;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerThread implements Runnable {
  private final String TAG = "ServerThread";
  private Context context;
  private NsdManager nsdManager;
  private NsdManager.RegistrationListener registrationListener;
  private ServerSocket serverSocket;
  private int port;
  
  public ServerThread(Context context) {
    this.context = context;
  }
  
  @Override
  public void run() {
    
    try {
      serverSocket = new ServerSocket(0);
      port = serverSocket.getLocalPort();
      Log.d(TAG, "Server socket created! Port: " + port);
      
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG, "Error Starting Server: " + e.getMessage());
    }
  
    initializeRegistrationListener();
    registerService();
  
    if (serverSocket != null) {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          CommunicationThread commThread = new CommunicationThread(serverSocket.accept());
          new Thread(commThread).start();
        
        } catch (IOException e) {
          e.printStackTrace();
          Log.e(TAG, "Error Communicating to Client:" + e.getMessage());
        }
      }
    } else Log.e(TAG, "Server socket is null!");
    
  }
  
  private void registerService() {
    // Create the NsdServiceInfo object, and populate it.
    NsdServiceInfo serviceInfo = new NsdServiceInfo();
    
    // The name is subject to change based on conflicts
    // with other services advertised on the same network.
    serviceInfo.setServiceName("Main Machine");
    serviceInfo.setServiceType("_cafepp._tcp.");
    serviceInfo.setPort(port);
  
    nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    nsdManager.registerService(
        serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
  }
  
  public void unregisterService() {
    nsdManager.unregisterService(registrationListener);
  }
  
  private void initializeRegistrationListener() {
    registrationListener = new NsdManager.RegistrationListener() {
      @Override
      public void onServiceRegistered(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Service registered: " + serviceInfo.getServiceName());
      }
  
      @Override
      public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(TAG, "Registration failed!: " + serviceInfo.getServiceName() +
                       " Error Code: " + errorCode);
      }
  
      @Override
      public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Service unregistered: " + serviceInfo.getServiceName());
      }
  
      @Override
      public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(TAG, "Unregistration failed!: " + serviceInfo.getServiceName() +
                       " Error Code: " + errorCode);
    
      }
    };
  }
  
  public int getPort() {
    return port;
  }
  
}
