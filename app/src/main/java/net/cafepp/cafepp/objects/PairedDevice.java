package net.cafepp.cafepp.objects;

public class PairedDevice extends Device {
  private boolean isServerPaired;
  private boolean isClientPaired;
  
  public PairedDevice(Device device) {
    setDevice(device);
  }
  
  public Device getDevice() {
    Device device = new Device();
    device.setDeviceName(getDeviceName());
    device.setConnected(isConnected());
    device.setIpAddress(getIpAddress());
    device.setPort(getPort());
    device.setServiceType(getServiceType());
    device.setMacAddress(getMacAddress());
    device.setPairKey(getPairKey());
    device.setTablet(isTablet());
    return device;
  }
  
  public PairedDevice setDevice(Device device) {
    setDeviceName(device.getDeviceName());
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
  
  public PairedDevice setServerPaired(boolean serverPaired) {
    isServerPaired = serverPaired;
    if (onPairedListener != null) onPairedListener.onPaired(isPaired(), this);
    return this;
  }
  
  public boolean isClientPaired() {
    return isClientPaired;
  }
  
  public PairedDevice setClientPaired(boolean clientPaired) {
    isClientPaired = clientPaired;
    if (onPairedListener != null) onPairedListener.onPaired(isPaired(), this);
    return this;
  }
  
  private boolean isPaired() {
    return isServerPaired && isClientPaired;
  }
  
  private OnPairedListener onPairedListener;
  
  public interface OnPairedListener {
    void onPaired(boolean isBothConfirmed, PairedDevice device);
  }
  
  public PairedDevice setOnPairedListener(OnPairedListener listener) {
    onPairedListener = listener;
    return this;
  }
}
