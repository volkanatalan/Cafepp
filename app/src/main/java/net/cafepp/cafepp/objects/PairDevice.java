package net.cafepp.cafepp.objects;

public class PairDevice extends Device {
  private boolean isServerPaired;
  private boolean isClientPaired;
  
  public PairDevice(Device device) {
    setDevice(device);
  }
  
  public Device getDevice() {
    Device device = new Device();
    device.setDeviceName(getDeviceName());
    device.setSocket(getSocket());
    device.setConnected(isConnected());
    device.setIpAddress(getIpAddress());
    device.setPort(getPort());
    device.setServiceType(getServiceType());
    device.setMacAddress(getMacAddress());
    device.setPairKey(getPairKey());
    device.setTablet(isTablet());
    return device;
  }
  
  public PairDevice setDevice(Device device) {
    setDeviceName(device.getDeviceName());
    setSocket(device.getSocket());
    setConnected(device.isConnected());
    setIpAddress(device.getIpAddress());
    setPort(device.getPort());
    setServiceType(device.getServiceType());
    setMacAddress(device.getMacAddress());
    setPairKey(device.getPairKey());
    setTablet(device.isTablet());
    return this;
  }
  
  public boolean isServerPaired() {
    return isServerPaired;
  }
  
  public PairDevice setServerPaired(boolean serverPaired) {
    isServerPaired = serverPaired;
    if (onPairedListener != null && serverPaired) {
      onPairedListener.onPaired(isBothPaired(), this);
    }
    return this;
  }
  
  public boolean isClientPaired() {
    return isClientPaired;
  }
  
  public PairDevice setClientPaired(boolean clientPaired) {
    isClientPaired = clientPaired;
    if (onPairedListener != null && clientPaired) {
      onPairedListener.onPaired(isBothPaired(), this);
    }
    return this;
  }
  
  private boolean isBothPaired() {
    return isServerPaired && isClientPaired;
  }
  
  private OnPairedListener onPairedListener;
  
  public interface OnPairedListener {
    void onPaired(boolean isBothConfirmed, PairDevice device);
  }
  
  public PairDevice setOnPairedListener(OnPairedListener listener) {
    onPairedListener = listener;
    return this;
  }
}
