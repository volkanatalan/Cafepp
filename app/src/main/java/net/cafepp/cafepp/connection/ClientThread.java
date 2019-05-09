package net.cafepp.cafepp.connection;

import android.util.Log;

import net.cafepp.cafepp.enums.Command;
import net.cafepp.cafepp.objects.Device;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ClientThread's main purpose is to listen to the server.
 */

public class ClientThread implements Runnable {
  private final String TAG = "ClientThread";
  private Package mPackage;
  private Socket mSocket;
  private boolean allowNewThread = true;
  
  public ClientThread(Package aPackage) {
    mPackage = aPackage;
  }
  
  @Override
  public void run() {
    Thread thread;
    Command command = Command.LISTEN;
    Device myDevice = mPackage.getSendingDevice();
    Device sendingDevice = mPackage.getSendingDevice();
    
    try {
      InetAddress ip = InetAddress.getByName(sendingDevice.getIpAddress());
      int port = sendingDevice.getPort();
      mSocket = new Socket(ip, port);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    
    while (!Thread.currentThread().isInterrupted()) {
      if (mSocket != null && !mSocket.isClosed() && allowNewThread) {
        Log.d(TAG, "Listening for the inputs...");
        allowNewThread = false;
        
        CommunicationThread communicationThread = new CommunicationThread(mSocket, command, myDevice);
        communicationThread.setOnInputListener(aPackage -> {
          allowNewThread = true;
          if (onPackageInputListener != null) onPackageInputListener.onReceive(aPackage);
        });
        
        thread = new Thread(communicationThread);
        thread.start();
      }
      
      else {
        Log.d(TAG, "Socket is null. Thread interrupting.");
        Thread.currentThread().interrupt();
      }
    }
    
    Log.d(TAG, "Thread interrupted.");
  }
  
  private OnPackageInputListener onPackageInputListener;
  
  public interface OnPackageInputListener {
    void onReceive(Package aPackage);
  }
  
  public void setOnPackageInputListener(OnPackageInputListener listener) {
    onPackageInputListener = listener;
  }
  
  public void setPackage(Package pkg) {
    mPackage = pkg;
  }
  
  public Socket getSocket() {
    return mSocket;
  }
  
  public void closeSocket() {
    if (mSocket != null && !mSocket.isClosed()) {
      try {
        mSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
