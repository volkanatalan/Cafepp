package net.cafepp.cafepp.connection;

import android.util.Log;

import net.cafepp.cafepp.objects.Device;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerThread implements Runnable {
  private final String TAG = "ServerThread";
  private Package mPackage;
  private ServerSocket serverSocket;
  
  public ServerThread(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }
  
  @Override
  public void run() {
  
    if (mPackage != null) {
      Command command = mPackage.getCommand();
      Device myDevice = mPackage.getSendingDevice();
      Thread thread;
  
      while (!Thread.currentThread().isInterrupted()) {
        if (serverSocket != null && !serverSocket.isClosed()) {
          try {
            Log.d(TAG, "Listening for the inputs...");
        
            CommunicationThread communicationThread =
                new CommunicationThread(serverSocket.accept(), command, myDevice);
            communicationThread.setOnInputListener(aPackage -> {
              if (onPackageInputListener != null) onPackageInputListener.onReceive(aPackage);
            });
        
            thread = new Thread(communicationThread);
            thread.start();
        
          } catch (IOException e) {
            Log.d(TAG, "Thread interrupting.");
            e.printStackTrace();
            Thread.currentThread().interrupt();
          }
      
        } else{
          Log.d(TAG, "Server socket is null. Thread interrupting.");
          Thread.currentThread().interrupt();
        }
      }
    }
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
}
