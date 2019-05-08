package net.cafepp.cafepp.objects;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import net.cafepp.cafepp.connection.ClientType;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Device implements Serializable {
  private int id;
  private String deviceName;
  private Socket socket;
  private String serviceType = "_cafepp._tcp.";
  private int port = 0;
  private String ipAddress;
  private String macAddress;
  private boolean isTablet;
  private int pairKey;
  private boolean allowPairReq = false;
  private boolean isFound = false;
  private boolean isConnected = false;
  private ClientType clientType;
  
  public Device() {
  }
  
  public Device(String deviceName) {
    this.deviceName = deviceName;
  }
  
  public Device(String deviceName, String macAddress, String ipAddress) {
    this.deviceName = deviceName;
    this.macAddress = macAddress;
    this.ipAddress = ipAddress;
  }
  
  public Device(String deviceName, String ipAddress, int port) {
    this.deviceName = deviceName;
    this.ipAddress = ipAddress;
    this.port = port;
  }
  
  public int getId() {
    return id;
  }
  
  public Device setId(int id) {
    this.id = id;
    return this;
  }
  
  public String getDeviceName() {
    return deviceName;
  }
  
  public Device setDeviceName(String name) {
    deviceName = name;
    return this;
  }
  
  public Socket getSocket() {
    return socket;
  }
  
  public void setSocket(Socket socket) {
    this.socket = socket;
  }
  
  public String getServiceType() {
    return serviceType;
  }
  
  public Device setServiceType(String type) {
    serviceType = type;
    return this;
  }
  
  public String getIpAddress() {
    return ipAddress;
  }
  
  public Device setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }
  
  public int getPort() {
    return port;
  }
  
  public Device setPort(int port) {
    this.port = port;
    return this;
  }
  
  public String getMacAddress() {
    return macAddress;
  }
  
  public Device setMacAddress(String macAddress) {
    this.macAddress = macAddress;
    return this;
  }
  
  public int getPairKey() {
    return pairKey;
  }
  
  public Device setPairKey(int pairKey) {
    this.pairKey = pairKey;
    return this;
  }
  
  public boolean isAllowPairReq() {
    return allowPairReq;
  }
  
  public void setAllowPairReq(boolean allowPairReq) {
    this.allowPairReq = allowPairReq;
  }
  
  public boolean isFound() {
    return isFound;
  }
  
  public void setFound(boolean found) {
    isFound = found;
  }
  
  public boolean isConnected() {
    return isConnected;
  }
  
  public Device setConnected(boolean connected) {
    isConnected = connected;
    return this;
  }
  
  public boolean isTablet() {
    return isTablet;
  }
  
  public Device setTablet(boolean tablet) {
    isTablet = tablet;
    return this;
  }
  
  public ClientType getClientType() {
    return clientType;
  }
  
  public void setClientType(ClientType clientType) {
    this.clientType = clientType;
  }
  
  public void setClientType(String clientType) {
    ClientType type = ClientType.WAITER;
    switch (clientType) {
      case "Cashier":
        type = ClientType.CASHIER;
        break;
      case "CASHIER":
        type = ClientType.CASHIER;
        break;
      case "Cook":
        type = ClientType.COOK;
        break;
      case "COOK":
        type = ClientType.COOK;
        break;
      case "Customer":
        type = ClientType.CUSTOMER;
        break;
      case "CUSTOMER":
        type = ClientType.CUSTOMER;
        break;
      case "Manager":
        type = ClientType.MANAGER;
        break;
      case "MANAGER":
        type = ClientType.MANAGER;
        break;
      case "Waiter":
        type = ClientType.WAITER;
        break;
      case "WAITER":
        type = ClientType.WAITER;
        break;
      default:
        Log.e("Device", "Wrong client Type!");
        break;
    }
    this.clientType = type;
  }
  
  public NsdServiceInfo getNsdServiceInfo() {
    NsdServiceInfo info = new NsdServiceInfo();
    info.setServiceName(deviceName);
    info.setServiceType(serviceType);
    info.setPort(port);
    try {
      info.setHost(InetAddress.getByName(ipAddress));
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return info;
  }
  
  public Device setNsdServiceInfo(NsdServiceInfo info) {
    deviceName = info.getServiceName();
    serviceType = info.getServiceType();
    ipAddress = info.getHost().getHostAddress();
    port = info.getPort();
    return this;
  }
}
