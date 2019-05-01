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
  private Device sendingDevice;
  private Device receivingDevice;
  
  public CommunicationThread(Package aPackage) {
    command = aPackage.getCommand();
    sendingDevice = aPackage.getSendingDevice();
    receivingDevice = aPackage.getReceivingDevice();
  }
  
  public CommunicationThread(Socket socket, Command command, Device sendingDevice) {
    this.socket = socket;
    this.command = command;
    this.sendingDevice = sendingDevice;
  }
  
  @Override
  public void run() {
    Log.d(TAG, "In run. Command: " + command);
  
    if (receivingDevice != null && socket == null) {
      Log.d(TAG, "Socket is null, creating...");
      try {
        // If the socket is null, initialize it.
        InetAddress ip = InetAddress.getByName(receivingDevice.getIpAddress());
        int port = receivingDevice.getPort();
        socket = new Socket(ip, port);
        
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    if (socket != null) {
      Log.d(TAG, "IP: " + socket.getInetAddress().getHostAddress());
      Log.d(TAG, "Port: " + socket.getPort());
      if (receivingDevice != null)
        Log.d(TAG, "Receiving device port: " + receivingDevice.getPort());
    }
    
    while (!Thread.currentThread().isInterrupted()) {
      Log.d(TAG, "In while");
      Package aPackage;
  
      switch (command) {
        case LISTEN:
          Log.d(TAG, "In LISTEN");
          try {
            // Listen for the inputs.
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object input = in.readObject();
            
            if (input != null) {
              Log.d(TAG, "There is an input!");
              Log.d(TAG, "Port: " + socket.getPort());
              
              if (input instanceof Command) {
                Log.d(TAG, "Input is a Command.");
                Command command = (Command) input;
                yesSir(command);
              }
              
              else if (input instanceof Package) {
                Log.d(TAG, "Input is a Package.");
                Package receivedPackage = (Package) input;
                Log.d(TAG, "Package command: " + receivedPackage.getCommand());
                Log.i(TAG, "Device Name: " + receivedPackage.getSendingDevice().getDeviceName());
  
                receivedPackage.getSendingDevice().setSocket(socket);
                if (onInputListener != null) onInputListener.onReceive(receivedPackage);
  
                Thread.currentThread().interrupt();
              }
              
              else Thread.currentThread().interrupt();
            }
            
            else {
              Log.d(TAG, "input null.");
              Thread.currentThread().interrupt();
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
          aPackage = new Package(Command.INFO, sendingDevice, receivingDevice);
          send(aPackage);
  
          try {
            socket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
          Thread.currentThread().interrupt();
          break;
  
        case PAIR_REQ:
          Log.d(TAG, "In PAIR_REQ");
          // Send pair request to server.
          aPackage = new Package(Command.PAIR_REQ, sendingDevice, receivingDevice);
          send(aPackage);
  
          command = Command.LISTEN;
          break;
    
        default:
          Log.d(TAG, "In default. Command: " + command);
          aPackage = new Package(command, sendingDevice, receivingDevice);
          if (sendingDevice != null) sendingDevice.setSocket(null);
          if (receivingDevice != null) receivingDevice.setSocket(null);
          send(aPackage);
          Thread.currentThread().interrupt();
          break;
      }
    }
  
    Log.d(TAG, "Thread interrupted. Command: " + command);
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
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private OnInputListener onInputListener;
  
  public interface OnInputListener {
    void onReceive(Package aPackage);
  }
  
  public void setOnInputListener(OnInputListener listener) {
    onInputListener = listener;
  }
}
