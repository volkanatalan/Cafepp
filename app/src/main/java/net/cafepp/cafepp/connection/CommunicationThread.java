package net.cafepp.cafepp.connection;

import android.util.Log;

import net.cafepp.cafepp.objects.Device;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationThread implements Runnable {
  
  private static final String TAG = "CommunicationThread";
  private Socket socket;
  private Command command;
  private Device myDevice;
  private Device targetDevice;
  
  public CommunicationThread(Command command) {
    Log.d(TAG, "In CommunicationThread. Command: " + command);
    this.command = command;
  }
  
  public CommunicationThread(Socket socket, Command command) {
    Log.d(TAG, "In CommunicationThread. Command: " + command);
    this.socket = socket;
    this.command = command;
  }
  
  @Override
  public void run() {
  
    if (targetDevice != null && socket == null) {
      try {
        // If the socket is null, initialize it.
        socket = new Socket(InetAddress.getByName(targetDevice.getIpAddress()), targetDevice.getPort());
        myDevice.setPort(socket.getPort());
        
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    while (!Thread.currentThread().isInterrupted()) {
      Log.d(TAG, "In run");
      Package aPackage;
  
      switch (command) {
        case LISTEN:
          // Listen for the inputs.
          Log.d(TAG, "Listening for the inputs.");
          try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object input = in.readObject();
            
            if (input != null) {
              Log.d(TAG, "There is an input!");
              
              if (input instanceof Command) {
                Log.d(TAG, "Input is a Command.");
                Command command = (Command) input;
                yesSir(command);
                
              } else if (input instanceof Package) {
                Log.d(TAG, "Input is a Package.");
                Package receivedPackage = (Package) input;
                Command packageCommand = receivedPackage.getCommand();
                Log.d(TAG, "Package command is " + packageCommand);
  
                Device device = receivedPackage.getSendingDevice();
                Log.i(TAG, "Device Name: " + device.getDeviceName());
  
                if (onInputListener != null) onInputListener.onReceive(receivedPackage);
  
                in.close();
                Thread.currentThread().interrupt();
                
              } else {
                in.close();
                Thread.currentThread().interrupt();}
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
          send(command);
  
          // Listen for the answer.
          command = Command.LISTEN;
          break;
    
        case INFO:
          Log.d(TAG, "In INFO");
          // Send device info to the client.
          aPackage = new Package(Command.INFO, myDevice, null);
          send(aPackage);
  
          Thread.currentThread().interrupt();
          break;
  
        case PAIR_REQ:
          Log.d(TAG, "In PAIR_REQ");
          // Send pair request to server.
          aPackage = new Package(Command.PAIR_REQ, myDevice, null);
          send(aPackage);
          if (onOutputListener != null) onOutputListener.onSend(aPackage);
  
          Thread.currentThread().interrupt();
          break;
  
        case PAIR_CLIENT_ACCEPT:
          Log.d(TAG, "In PAIR_CLIENT_ACCEPT");
          aPackage = new Package(Command.PAIR_CLIENT_ACCEPT, myDevice, null);
          send(aPackage);
  
          Thread.currentThread().interrupt();
          break;
          
        case PAIR_SERVER_ACCEPT:
          Log.d(TAG, "In PAIR_SERVER_ACCEPT");
          aPackage = new Package(Command.PAIR_SERVER_ACCEPT, myDevice, null);
          send(aPackage);
  
          Thread.currentThread().interrupt();
          break;
          
        case PAIR_SERVER_DENY:
          Log.d(TAG, "In PAIR_SERVER_DENY");
          aPackage = new Package(Command.PAIR_SERVER_DENY, myDevice, null);
          send(aPackage);
  
          Thread.currentThread().interrupt();
          break;
    
        default:
          Log.d(TAG, "In default");
          Log.e(TAG, "No command!");
          Thread.currentThread().interrupt();
          break;
      }
    }
  
    try {
      if (socket != null && !socket.isClosed()) socket.close();
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
  
  private void send(Object object) {
    try {
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.writeObject(object);
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private OnInputListener onInputListener;
  private OnOutputListener onOutputListener;
  
  public interface OnInputListener {
    void onReceive(Package aPackage);
  }
  
  public interface OnOutputListener {
    void onSend(Package aPackage);
  }
  
  public void setOnInputListener(OnInputListener listener) {
    onInputListener = listener;
  }
  
  public void setOnOutputListener(OnOutputListener onOutputListener) {
    this.onOutputListener = onOutputListener;
  }
  
  public void setMyDevice(Device myDevice) {
    this.myDevice = myDevice;
  }
  
  public void setTargetDevice(Device targetDevice) {
    this.targetDevice = targetDevice;
  }
}
