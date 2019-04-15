package net.cafepp.cafepp.connection;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.cafepp.cafepp.objects.Device;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommunicationThread implements Runnable {
  
  private Context context;
  private static final String TAG = "CommunicationThread";
  private Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private Command command;
  private Device myDevice;
  private NsdServiceInfo nsdServiceInfo;
  
  public enum Command {
    LISTEN, INFO_REQ, INFO
  }
  
  public CommunicationThread(Context context, Socket socket, Command command) {
    Log.d(TAG, "In CommunicationThread. Command: " + command);
    this.context = context;
    this.socket = socket;
    this.command = command;
  }
  
  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      Log.d(TAG, "In run");
  
      switch (command) {
        case LISTEN:
          // Listen for the inputs.
          Log.d(TAG, "Listening for the inputs.");
          try {
            in = new ObjectInputStream(socket.getInputStream());
            Object input = in.readObject();
            if (input != null) {
              Log.d(TAG, "There is an input!");
              
              if (input instanceof Command) {
                Log.d(TAG, "Input is a Command.");
                Command command = (Command) input;
                yesSir(command);
                
              } else if (input instanceof Device) {
                Log.d(TAG, "Input is a Device.");
                Device device = (Device) input;
                Log.i(TAG, "Device Name: " + device.getDeviceName());
                Log.i(TAG, "MAC Address: " + device.getMacAddress());
                Log.i(TAG, "isTablet: " + device.isTablet());
  
                sendDeviceToClientService("ADD", device);
                
                Thread.currentThread().interrupt();
                
              } else Thread.currentThread().interrupt();
            }
  
          } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
          }
          break;
    
        case INFO_REQ:
          Log.d(TAG, "In INFO_REQ");
          // Request device info from server.
          try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(command);
            out.flush();
  
            command = Command.LISTEN;
  
          } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
          }
          break;
    
        case INFO:
          Log.d(TAG, "In INFO");
          // Send device info to the client.
          try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(myDevice);
            out.flush();
            Thread.currentThread().interrupt();
          } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
          }
          break;
    
        default:
          Log.d(TAG, "In default");
          Log.e(TAG, "No command!");
          Thread.currentThread().interrupt();
          break;
      }
    }
  
    try {
      if (!socket.isClosed()) socket.close();
    } catch (IOException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }
  
  private void yesSir(Command command) {
    Log.d(TAG, "Command is " + command);
    
    switch (command) {
      case INFO_REQ:
        Log.d(TAG, "In yesSir INFO_REQ");
        this.command = Command.INFO;
        break;
    }
  }
  
  private void sendDeviceToClientService(String command, Device device) {
    Log.i(TAG, "Message sending to ClientService");
    Intent intent = new Intent("ClientService");
    intent.putExtra("Command", command);
    Bundle bundle = new Bundle();
    bundle.putSerializable("Message", device);
    intent.putExtra("Message", bundle);
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
  }
  
  public Device getMyDevice() {
    return myDevice;
  }
  
  public void setMyDevice(Device myDevice) {
    this.myDevice = myDevice;
  }
  
  public NsdServiceInfo getNsdServiceInfo() {
    return nsdServiceInfo;
  }
  
  public void setNsdServiceInfo(NsdServiceInfo nsdServiceInfo) {
    this.nsdServiceInfo = nsdServiceInfo;
  }
}
