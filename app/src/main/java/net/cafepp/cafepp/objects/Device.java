package net.cafepp.cafepp.objects;

import android.net.nsd.NsdServiceInfo;

import java.net.InetAddress;

public class Device {
  private NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
  private String hostAddress;
  private String mac;
  private boolean isConnected = false;
  
  public Device() {
  }
  
  public Device(String serviceName) {
    setServiceName(serviceName);
  }
  
  public Device(NsdServiceInfo info){
    nsdServiceInfo = info;
  }
  
  public NsdServiceInfo getNsdServiceInfo() {
    return nsdServiceInfo;
  }
  
  public void setNsdServiceInfo(NsdServiceInfo nsdServiceInfo) {
    this.nsdServiceInfo = nsdServiceInfo;
  }
  
  public String getServiceName() {
    return nsdServiceInfo.getServiceName();
  }
  
  public void setServiceName(String name) {
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
  
  public String getHostAddress() {
    return hostAddress;
  }
  
  public void setHostAddress(String hostAddress) {
    this.hostAddress = hostAddress;
  }
  
  public int getPort() {
    return nsdServiceInfo.getPort();
  }
  
  public void setPort(int port) {
    nsdServiceInfo.setPort(port);
  }
  
  public String getMac() {
    return mac;
  }
  
  public void setMac(String mac) {
    this.mac = mac;
  }
  
  public boolean isConnected() {
    return isConnected;
  }
  
  public void setConnected(boolean connected) {
    isConnected = connected;
  }
}
