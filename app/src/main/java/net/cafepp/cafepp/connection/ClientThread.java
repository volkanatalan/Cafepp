package net.cafepp.cafepp.connection;

import android.util.Log;

import net.cafepp.cafepp.objects.Device;

import java.net.Socket;

/**
 * ClientThread's main purpose is to listen to the server.
 */

public class ClientThread implements Runnable {
  private final String TAG = "ClientThread";
  private Package mPackage;
  
  public ClientThread(Package aPackage) {
    mPackage = aPackage;
  }
  
  @Override
  public void run() {
    Command command = mPackage.getCommand();
    Device myDevice = mPackage.getSendingDevice();
    Socket socket = mPackage.getSocket();
    Thread thread;
    
    while (!Thread.currentThread().isInterrupted()) {
      if (socket != null && !socket.isClosed()) {
        Log.d(TAG, "Listening for the inputs...");

        CommunicationThread communicationThread =
            new CommunicationThread(socket, command, myDevice);
        communicationThread.setOnInputListener(aPackage -> {
          if (onPackageInputListener != null)
            onPackageInputListener.onReceive(aPackage);
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
    return mPackage.getSocket();
  }
}
