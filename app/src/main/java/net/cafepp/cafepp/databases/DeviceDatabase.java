package net.cafepp.cafepp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.cafepp.cafepp.objects.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceDatabase extends SQLiteOpenHelper {
  
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "DeviceDatabase.db";
  
  private static final String TABLE_DEVICES = "Devices";
  private static final String COLUMN_DEVICE_ID = "_id";
  private static final String COLUMN_DEVICE_NAME = "DeviceName";
  private static final String COLUMN_DEVICE_MAC = "DeviceMacAddress";
  private static final String COLUMN_DEVICE_IP = "DeviceIPAddress";
  
  public DeviceDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
    String createTableDevices =
        "CREATE TABLE " + TABLE_DEVICES + "(" +
            COLUMN_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DEVICE_NAME + " TEXT, " +
            COLUMN_DEVICE_MAC + " TEXT, " +
            COLUMN_DEVICE_IP + " TEXT " + ");";
  
    db.execSQL(createTableDevices);
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);
    onCreate(db);
  }
  
  public void add(Device device) {
    String deviceName = device.getDeviceName();
    String mac = device.getMacAddress();
    String ip = device.getIpAddress();
    
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_DEVICE_NAME, deviceName==null ? "" : deviceName);
    values.put(COLUMN_DEVICE_MAC, mac==null ? "" : mac);
    values.put(COLUMN_DEVICE_IP, ip==null ? "" : ip);
    db.insert(TABLE_DEVICES, "", values);
    db.close();
  }
  
  public List<Device> getDevices() {
    SQLiteDatabase db = this.getReadableDatabase();
    List<Device> devices = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_DEVICES + ";", null);
  
    while (c.moveToNext()) {
      Device device = new Device();
      
      device.setDeviceName(c.getString(c.getColumnIndex(COLUMN_DEVICE_NAME)));
      device.setIpAddress(c.getString(c.getColumnIndex(COLUMN_DEVICE_IP)));
      device.setMacAddress(c.getString(c.getColumnIndex(COLUMN_DEVICE_MAC)));
      
      devices.add(device);
    }
    
    c.close();
    db.close();
    return devices;
  }
}
