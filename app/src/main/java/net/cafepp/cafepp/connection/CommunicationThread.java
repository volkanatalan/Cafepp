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
  private Socket mSocket;
  private Command mCommand;
  private Device mSendingDevice;
  private Device mReceivingDevice;
  
  public CommunicationThread(Package aPackage) {
    mCommand = aPackage.getCommand();
    mSendingDevice = aPackage.getSendingDevice();
    mReceivingDevice = aPackage.getReceivingDevice();
    
    Socket socket = mReceivingDevice.getSocket();
    if (socket != null && !socket.isClosed()) {
      mSocket = socket;
    }
  }
  
  public CommunicationThread(Socket socket, Command command, Device sendingDevice) {
    mSocket = socket;
    mCommand = command;
    mSendingDevice = sendingDevice;
  }
  
  @Override
  public void run() {
    Log.d(TAG, "In run. Command: " + mCommand);
  
    if (mSendingDevice != null) mSendingDevice.setSocket(null);
    if (mReceivingDevice != null) mReceivingDevice.setSocket(null);
  
    if (mReceivingDevice != null && mSocket == null) {
      Log.d(TAG, "Socket creating...");
      try {
        // If the socket is null, initialize it.
        InetAddress ip = InetAddress.getByName(mReceivingDevice.getIpAddress());
        int port = mReceivingDevice.getPort();
        mSocket = new Socket(ip, port);
        
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    if (mSocket != null) {
      Log.d(TAG, "IP: " + mSocket.getInetAddress().getHostAddress());
      Log.d(TAG, "Port: " + mSocket.getPort());
      if (mReceivingDevice != null)
        Log.d(TAG, "Receiving device port: " + mReceivingDevice.getPort());
    }
    
    while (!Thread.currentThread().isInterrupted()) {
      Log.d(TAG, "In while");
      Package aPackage;
  
      switch (mCommand) {
        case LISTEN:
          Log.d(TAG, "In LISTEN");
          try {
            // Listen for the inputs.
            ObjectInputStream in = new ObjectInputStream(mSocket.getInputStream());
            Object input = in.readObject();
            
            if (input != null) {
              Log.d(TAG, "There is an input!");
              Log.d(TAG, "Port: " + mSocket.getPort());
              
              if (input instanceof Command) {
                Log.d(TAG, "Input is a Command.");
                Command command = (Command) input;
                yesSir(command);
              }
              
              else if (input instanceof Package) {
                Log.d(TAG, "Input is a Package.");
                Package receivedPackage = (Package) input;
                Device sendingDevice = receivedPackage.getSendingDevice();
                Device receivingDevice = receivedPackage.getReceivingDevice();
                Log.d(TAG, "Package command: " + receivedPackage.getCommand());
                Log.i(TAG, "Device Name: " + sendingDevice.getDeviceName());
  
                if (receivingDevice != null) receivingDevice.setSocket(mSocket);
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
          send(mCommand);
  
          // Listen for the answer.
          mCommand = Command.LISTEN;
          break;
    
        case INFO:
          Log.d(TAG, "In INFO");
          // Send device info to the client.
          aPackage = new Package(mCommand, mSendingDevice, mReceivingDevice);
          send(aPackage);
  
          try {
            mSocket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
          Thread.currentThread().interrupt();
          break;
  
        case PAIR_REQ:
          Log.d(TAG, "In PAIR_REQ");
          // Send pair request to server.
          aPackage = new Package(mCommand, mSendingDevice, mReceivingDevice);
          send(aPackage);
  
          // Listen for the answer.
          mCommand = Command.LISTEN;
          break;
    
        default:
          Log.d(TAG, "In default. Command: " + mCommand);
          aPackage = new Package(mCommand, mSendingDevice, mReceivingDevice);
          send(aPackage);
          Thread.currentThread().interrupt();
          break;
      }
    }
  
    Log.d(TAG, "Thread interrupted. Command: " + mCommand);
  }
  
  private void yesSir(Command command) {
    Log.d(TAG, "Command is " + command);
    
    switch (command) {
      case INFO_REQ:
        Log.d(TAG, "In yesSir INFO_REQ");
        this.mCommand = Command.INFO;
        break;
    }
  }
  
  private void send(Object object) {
    try {
      ObjectOutputStream out = new ObjectOutputStream(mSocket.getOutputStream());
      out.writeObject(object);
    } catch (IOException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
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
