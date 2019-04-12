package net.cafepp.cafepp.objects;

import android.net.nsd.NsdServiceInfo;

import java.net.InetAddress;

public class Device {
  private NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
  private String ipAddress;
  private String macAddress;
  private int pairKey;
  private boolean isConnected = false;
  
  public Device() {
  }
  
  public Device(String serviceName) {
    setDeviceName(serviceName);
  }
  
  public Device(NsdServiceInfo nsdServiceInfo){
    this.nsdServiceInfo = nsdServiceInfo;
  }
  
  public NsdServiceInfo getNsdServiceInfo() {
    return nsdServiceInfo;
  }
  
  public void setNsdServiceInfo(NsdServiceInfo nsdServiceInfo) {
    this.nsdServiceInfo = nsdServiceInfo;
  }
  
  public String getDeviceName() {
    return nsdServiceInfo.getServiceName();
  }
  
  public void setDeviceName(String name) {
    nsdServiceInfo.setServiceName(name);
  }
  
  public String getServiceType() {
    return nsdServiceInfo.getServiceType();
  }
  
  public void setServiceType(String type) {
    nsdServiceInfo.setServiceType(type);
  }
  
  public InetAddress getHost() {
    return nsdServiceInfo.getHost();
  }
  
  public void setHost(InetAddress host) {
    nsdServiceInfo.setHost(host);
  }
  
  public String getIpAddress() {
    return ipAddress;
  }
  
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }
  
  public int getPort() {
    return nsdServiceInfo.getPort();
  }
  
  public void setPort(int port) {
    nsdServiceInfo.setPort(port);
  }
  
  public String getMacAddress() {
    return macAddress;
  }
  
  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }
  
  public int getPairKey() {
    return pairKey;
  }
  
  public void setPairKey(int pairKey) {
    this.pairKey = pairKey;
  }
  
  public boolean isConnected() {
    return isConnected;
  }
  
  public void setConnected(boolean connected) {
    isConnected = connected;
  }
}
