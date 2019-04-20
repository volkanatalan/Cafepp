package net.cafepp.cafepp.objects;

import android.net.nsd.NsdServiceInfo;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Device implements Serializable {
  private String deviceName;
  private String serviceType = "_cafepp._tcp.";
  private int port;
  private String ipAddress;
  private String macAddress;
  private boolean isTablet;
  private int pairKey;
  private boolean isConnected = false;
  
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
  
  public String getDeviceName() {
    return deviceName;
  }
  
  public Device setDeviceName(String name) {
    deviceName = name;
    return this;
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
