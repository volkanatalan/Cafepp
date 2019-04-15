package net.cafepp.cafepp.connection;

import android.content.Context;
import android.util.Log;

import net.cafepp.cafepp.objects.Device;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerThread implements Runnable {
  private final String TAG = "ServerThread";
  private Context context;
  private ServerSocket serverSocket;
  private Device myDevice;
  
  public ServerThread(Context context, ServerSocket serverSocket) {
    this.context = context;
    this.serverSocket = serverSocket;
  }
  
  @Override
  public void run() {
  
    while (!Thread.currentThread().isInterrupted()) {
      if (serverSocket != null && !serverSocket.isClosed()) {
        try {
          Log.d(TAG, "Listening for the inputs...");
          CommunicationThread communicationThread = new CommunicationThread(
              context, serverSocket.accept(), CommunicationThread.Command.LISTEN);
          communicationThread.setMyDevice(myDevice);
          new Thread(communicationThread).start();
        
        } catch (IOException e) {
          Log.e(TAG, "Error Communicating to Client!");
          e.printStackTrace();
        }
        
      } else Log.e(TAG, "Server socket is null!");
    }
  
    Log.d(TAG, "ServerThread is interrupted");
    
  }
  
  public Device getMyDevice() {
    return myDevice;
  }
  
  public void setMyDevice(Device myDevice) {
    this.myDevice = myDevice;
  }
}
