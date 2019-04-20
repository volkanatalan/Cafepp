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
  
      while (!Thread.currentThread().isInterrupted()) {
        if (serverSocket != null && !serverSocket.isClosed()) {
          try {
            Log.d(TAG, "Listening for the inputs...");
        
            CommunicationThread communicationThread =
                new CommunicationThread(serverSocket.accept(), command);
            communicationThread.setMyDevice(myDevice);
            communicationThread.setOnInputListener(aPackage -> {
              if (onPackageInputListener != null) onPackageInputListener.onReceive(aPackage);
            });
        
            new Thread(communicationThread).start();
        
          } catch (IOException e) {
            Log.e(TAG, "Error Communicating to Client!");
            e.printStackTrace();
          }
      
        } else Log.e(TAG, "Server socket is null!");
      }
    }
  
    Log.d(TAG, "ServerThread is interrupted");
    
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
