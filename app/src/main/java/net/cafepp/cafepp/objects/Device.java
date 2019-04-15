package net.cafepp.cafepp.objects;

import java.io.Serializable;

public class Device implements Serializable {
  private String deviceName;
  private String serviceType;
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
  
  public Device(String deviceName, String serviceType, String ipAddress, int port) {
    this.deviceName = deviceName;
    this.serviceType = serviceType;
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
}
