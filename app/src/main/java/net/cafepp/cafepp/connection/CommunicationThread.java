package net.cafepp.cafepp.connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationThread implements Runnable {
  
  private final String TAG = "CommunicationThread";
  private BufferedReader in;
  private PrintWriter out;
  
  CommunicationThread(Socket socket) {
    try {
      
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(new BufferedWriter(
          new OutputStreamWriter(socket.getOutputStream())), true);
      
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG,"Error Connecting to Client!");
    }
    
    Log.d(TAG, "Connected to Client!!");
  }
  
  @Override
  public void run() {
  
    while (!Thread.currentThread().isInterrupted()) {
      try {
        String read = in.readLine();
        if (read  == null|| "Disconnect".contentEquals(read)) {
          Thread.interrupted();
          read = "Client Disconnected";
          Log.d(TAG, "Client: " + read);
          break;
        }
        Log.d(TAG, "Client : " + read);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
