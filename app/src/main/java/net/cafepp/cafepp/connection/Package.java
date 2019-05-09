package net.cafepp.cafepp.connection;

import net.cafepp.cafepp.enums.Command;
import net.cafepp.cafepp.objects.Device;

import java.io.Serializable;

/**
 * Provides communication between server, clients, service and activity.
 */

public class Package implements Serializable {
  private Command command;
  private Device sendingDevice;
  private Device receivingDevice;
  
  public Package() {
  }
  
  public Package(Command command, Device sendingDevice, Device receivingDevice) {
    this.command = command;
    this.sendingDevice = sendingDevice;
    this.receivingDevice = receivingDevice;
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
}
