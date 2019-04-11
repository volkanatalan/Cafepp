package net.cafepp.cafepp.connection;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

import static android.content.Context.WIFI_SERVICE;

public class NSDHelper {
  private Context context;
  private final String TAG = "NSDHelper";
  private final String SERVICE_TYPE = "_cafepp._tcp.";
  private String serviceName;
  private String ip;
  private int localPort;
  private ServerSocket serverSocket;
  private NsdManager nsdManager;
  private NsdManager.RegistrationListener NSDRegistrationListener;
  private NsdManager.DiscoveryListener NSDDiscoveryListener;
  private boolean isServiceRegistered = false;
  private boolean isOnDiscovery = false;
  private RegistrationListener registrationListener;
  private DiscoveryListener discoveryListener;
  private ResolveListener resolveListener;
  
  @SuppressWarnings("deprecation")
  public NSDHelper(Context context) {
    this.context = context;
    nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
  
    // Get local ip address
    WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
    ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    Log.d(TAG, "Local IP: " + ip);
  }
  
  
  public void register(String serviceName) {
    this.serviceName = serviceName;
    initializeServerSocket();
    initializeRegistrationListener();
    registerService();
  }
  
  public void unregister() {
    if (isServiceRegistered) {
      nsdManager.unregisterService(NSDRegistrationListener);
      
      try {
        serverSocket.close();
        Log.d(TAG, "Server socket closed.");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void startDiscovery(){
    initializeDiscoveryListener();
    nsdManager.discoverServices(
        SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, NSDDiscoveryListener);
  }
  
  public void stopDiscovery() {
    if (isOnDiscovery)
      nsdManager.stopServiceDiscovery(NSDDiscoveryListener);
  }
  
  /**
   * Initialize a socket to any available port.
   */
  private void initializeServerSocket() {
    // Initialize a server socket on the next available port.
    try {
      serverSocket = new ServerSocket(0);
      Log.d(TAG, "Server socket initialized.");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    // Store the chosen port.
    localPort = serverSocket.getLocalPort();
    Log.d(TAG, "Local Port: " + localPort);
  }
  
  /**
   * To register the service on the local network, first create a {@link NsdServiceInfo} object.
   * This object provides the information that other devices on the network use when they're
   * deciding whether to connect to your service.
   */
  private void registerService() {
    // Create the NsdServiceInfo object, and populate it.
    final NsdServiceInfo serviceInfo = new NsdServiceInfo();
    
    // The name is subject to change based on conflicts
    // with other services advertised on the same network.
    serviceInfo.setServiceName(serviceName);
    serviceInfo.setServiceType(SERVICE_TYPE);
    serviceInfo.setPort(localPort);
    
    nsdManager.registerService(
        serviceInfo, NsdManager.PROTOCOL_DNS_SD, NSDRegistrationListener);
  }
  
  /**
   * {@link NsdManager.RegistrationListener} interface contains callbacks used by Android to alert
   * your application of the success or failure of service registration and unregistration.
   */
  private void initializeRegistrationListener() {
    NSDRegistrationListener = new NsdManager.RegistrationListener() {
      
      @Override
      public void onServiceRegistered(NsdServiceInfo serviceInfo) {
        // Save the service name. Android may have changed it in order to
        // resolve a conflict, so update the name you initially requested
        // with the name Android actually used.
        serviceName = serviceInfo.getServiceName();
        isServiceRegistered = true;
        
        Log.d(TAG, "Service registered: " + serviceName);
  
        if (registrationListener != null) registrationListener.onRegistered(serviceInfo);
      }
      
      @Override
      public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Registration failed!
        isServiceRegistered = false;
        Log.d(TAG, "Registration failed: " + serviceInfo + " " + errorCode);
        if (registrationListener != null) registrationListener.onRegistrationFailed(serviceInfo, errorCode);
      }
      
      @Override
      public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        // Service has been unregistered. This only happens when you call
        // NsdManager.unregisterService() and pass in this listener.
        isServiceRegistered = false;
        Log.d(TAG, "Service unregistered: " + serviceInfo);
        if (registrationListener != null) registrationListener.onUnregistered(serviceInfo);
      }
      
      @Override
      public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        isServiceRegistered = true;
        Log.d(TAG, "Unregistration failed: " + serviceInfo + " " + errorCode);
        if (registrationListener != null) registrationListener.onUnregistrationFailed(serviceInfo, errorCode);
      }
    };
  }
  
  /**
   * Discover services on the network.
   */
  private void initializeDiscoveryListener() {
    
    // Instantiate a new DiscoveryListener
    NSDDiscoveryListener = new NsdManager.DiscoveryListener() {
      
      // Called as soon as service discovery begins.
      @Override
      public void onDiscoveryStarted(String serviceType) {
        isOnDiscovery = true;
        Log.d(TAG, "Service discovery started for the service type \"" + serviceType + "\"");
        if (discoveryListener != null) discoveryListener.onDiscoveryStarted(serviceType);
      }
      
      @Override
      public void onServiceFound(NsdServiceInfo serviceInfo) {
        // A service was found! Do something with it.
        Log.d(TAG, "Service discovery success! " + serviceInfo.getServiceName());
        
        if (discoveryListener != null) discoveryListener.onServiceFound(serviceInfo);
        
        if (serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
          if (serviceInfo.getServiceName().equals(serviceName))
            Log.d(TAG, "It's my device!");
          
          else {
            // Service type is the string containing the protocol and transport layer for this service.
            Log.d(TAG, "Same service type: " + serviceInfo.toString());
            nsdManager.resolveService(serviceInfo, initializeResolveListener());
          }
        }
      }
      
      @Override
      public void onServiceLost(NsdServiceInfo serviceInfo) {
        // When the network service is no longer available.
        Log.d(TAG, "Service lost: " + serviceInfo);
        if (discoveryListener != null) discoveryListener.onServiceLost(serviceInfo);
      }
      
      @Override
      public void onDiscoveryStopped(String serviceType) {
        isOnDiscovery = false;
        Log.i(TAG, "Discovery stopped for service type \"" + serviceType + "\"");
        if (discoveryListener != null) discoveryListener.onDiscoveryStopped(serviceType);
      }
      
      @Override
      public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        isOnDiscovery = false;
        Log.e(TAG, "Discovery failed! Error code: " + errorCode);
        nsdManager.stopServiceDiscovery(this);
        if (discoveryListener != null) discoveryListener.onStartDiscoveryFailed(serviceType, errorCode);
      }
      
      @Override
      public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        isOnDiscovery = true;
        Log.e(TAG, "Discovery failed! Error code: " + errorCode);
        nsdManager.stopServiceDiscovery(this);
        if (discoveryListener != null) discoveryListener.onStopDiscoveryFailed(serviceType, errorCode);
      }
    };
  }
  
  /**
   * Resolves the services on the network.
   */
  private NsdManager.ResolveListener initializeResolveListener() {
    return new NsdManager.ResolveListener() {
  
      @Override
      public void onServiceResolved(NsdServiceInfo serviceInfo) {
        Log.d(TAG, "Resolve succeeded: " + serviceInfo);
  
        if (serviceInfo.getHost() != null) {
          if (serviceInfo.getHost().getHostAddress().equals(ip) && resolveListener != null) {
            Log.d(TAG, "It's my device.");
            resolveListener.onResolvedMyDevice(serviceInfo);
            
          } else if (resolveListener != null)
            resolveListener.onResolved(serviceInfo);
        } else {
          Log.d(TAG, "Host is null!");
        }
      }
      
      @Override
      public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Called when the resolve fails. Use the error code to debug.
        String errorMessage = "Error code " + errorCode + ": ";
        
        switch (errorCode){
          case 0:
            errorMessage += "Internal error. " + serviceInfo.getServiceName();
            break;
          case 3:
            errorMessage += "It is already active: " + serviceInfo.getServiceName();
            
            // Try again.
            if (ip != null && serviceInfo.getHost() != null && !serviceInfo.getHost().getHostAddress().equals(ip))
              nsdManager.resolveService(serviceInfo, this);
            break;
          case 4:
            errorMessage += "Maximum outstanding requests from the applications have reached. " +
                                serviceInfo.getServiceName();
            break;
        }
        Log.e(TAG, "Resolve failed: " + errorMessage);
        if (resolveListener != null) resolveListener.onResolveFailed(serviceInfo, errorCode);
      }
    };
  }
  
  public interface RegistrationListener {
    void onRegistered(NsdServiceInfo serviceInfo);
    void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode);
    void onUnregistered(NsdServiceInfo serviceInfo);
    void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode);
  }
  
  public interface DiscoveryListener {
    void onDiscoveryStarted(String regType);
    void onServiceFound(NsdServiceInfo service);
    void onServiceLost(NsdServiceInfo service);
    void onDiscoveryStopped(String serviceType);
    void onStartDiscoveryFailed(String serviceType, int errorCode);
    void onStopDiscoveryFailed(String serviceType, int errorCode);
  }
  
  public interface ResolveListener {
    void onResolved(NsdServiceInfo serviceInfo);
    void onResolvedMyDevice(NsdServiceInfo serviceInfo);
    void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode);
  }
  
  public void setRegistrationListener(RegistrationListener registrationListener) {
    this.registrationListener = registrationListener;
  }
  
  public RegistrationListener getRegistrationListener() {
    return registrationListener;
  }
  
  public void setDiscoveryListener(DiscoveryListener discoveryListener) {
    this.discoveryListener = discoveryListener;
  }
  
  public DiscoveryListener getDiscoveryListener() {
    return discoveryListener;
  }
  
  public void setResolveListener(ResolveListener resolveListener) {
    this.resolveListener = resolveListener;
  }
  
  public ResolveListener getResolveListener() {
    return resolveListener;
  }
}
