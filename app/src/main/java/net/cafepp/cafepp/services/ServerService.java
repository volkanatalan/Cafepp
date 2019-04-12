package net.cafepp.cafepp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.DevicesActivity;
import net.cafepp.cafepp.connection.ServerThread;

public class ServerService extends Service {
  
  private static final String TAG = "ServerService";
  private ServerThread serverThread;
  private Thread threadServer;
  
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent.getAction()!=null) {
      Log.d(TAG, intent.getAction());
      if (intent.getAction().equals(Constants.ACTION.START_ACTION)) {
        Log.i(TAG, "Server Service Start Action entered.");
        
        Intent notificationIntent = new Intent(this, DevicesActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pNotificationIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0);
    
        Intent stopIntent = new Intent(this, ServerService.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        PendingIntent pStopIntent = PendingIntent.getActivity(
            this, 0, stopIntent, 0);
        
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon_round);
    
        Notification notification = new NotificationCompat.Builder(this,
            Constants.NOTIFICATION_ID.SERVER_SERVICE + "")
                                        .setContentTitle(getString(R.string.cafépp_allcaps))
                                        .setTicker(getString(R.string.cafépp_allcaps))
                                        .setContentText(getString(R.string.server))
                                        .setSmallIcon(R.drawable.wifi)
                                        .setLargeIcon(Bitmap.createScaledBitmap(
                                            icon, 128, 128, false))
                                        .setContentIntent(pNotificationIntent)
                                        .setOngoing(true)
                                        .addAction(R.drawable.stop, getString(R.string.stop), pStopIntent)
                                        .build();
        startForeground(Constants.NOTIFICATION_ID.SERVER_SERVICE, notification);
  
        serverThread = new ServerThread(ServerService.this);
        threadServer = new Thread(serverThread);
        threadServer.start();
        
      } else if (intent.getAction().equals(Constants.ACTION.STOP_ACTION)) {
        Log.i(TAG, "Server Service Stop Action entered.");
  
        if (threadServer != null) {
          serverThread.unregisterService();
          threadServer.interrupt();
          threadServer = null;
        }
        
        stopForeground(true);
        stopSelf();
      }
    }
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "In onDestroy");
  }
  
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
