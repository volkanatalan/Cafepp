package net.cafepp.cafepp.connection;

import net.cafepp.cafepp.objects.Device;

import java.io.Serializable;
import java.net.Socket;

/**
 * Provides communication between server, clients, service and activity.
 */

public class Package implements Serializable {
  private Command command;
  private Device sendingDevice;
  private Device receivingDevice;
  private Socket socket;
  
  public Package() {
  }
  
  public Package(Command command, Device sendingDevice, Device receivingDevice) {
    this.command = command;
    this.sendingDevice = sendingDevice;
    this.receivingDevice = receivingDevice;
  }
  
  public Package(Command command, Device sendingDevice, Device receivingDevice, Socket socket) {
    this.command = command;
    this.sendingDevice = sendingDevice;
    this.receivingDevice = receivingDevice;
    this.socket = socket;
  }
  
  public Command getCommand() {
    return command;
  }
  
  public Package setCommand(Command command) {
    this.command = command;
    return this;
  }
  
  public Device getSendingDevice() {
    return sendingDevice;
  }
  
  public Package setSendingDevice(Device sendingDevice) {
    this.sendingDevice = sendingDevice;
    return this;
  }
  
  public Device getReceivingDevice() {
    return receivingDevice;
  }
  
  public Package setReceivingDevice(Device receivingDevice) {
    this.receivingDevice = receivingDevice;
    return this;
  }
  
  public Socket getSocket() {
    return socket;
  }
  
  public void setSocket(Socket socket) {
    this.socket = socket;
  }
}
