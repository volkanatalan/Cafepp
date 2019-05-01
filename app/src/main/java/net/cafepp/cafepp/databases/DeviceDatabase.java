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
  
  private static final String TABLE_SERVER = "Server";
  private static final String TABLE_CLIENT = "Client";
  private static final String COLUMN_DEVICE_ID = "_id";
  private static final String COLUMN_DEVICE_NAME = "DeviceName";
  private static final String COLUMN_DEVICE_MAC = "DeviceMacAddress";
  
  public DeviceDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
    String createTableServer =
        "CREATE TABLE " + TABLE_SERVER + "(" +
            COLUMN_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DEVICE_NAME + " TEXT, " +
            COLUMN_DEVICE_MAC + " TEXT" + ");";
    
    
    String createTableClient =
        "CREATE TABLE " + TABLE_CLIENT + "(" +
            COLUMN_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DEVICE_NAME + " TEXT, " +
            COLUMN_DEVICE_MAC + " TEXT" + ");";
  
    db.execSQL(createTableServer);
    db.execSQL(createTableClient);
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);
    onCreate(db);
  }
  
  public void addAsServer(Device device) {
    String deviceName = device.getDeviceName();
    String mac = device.getMacAddress();
    
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_DEVICE_NAME, deviceName==null ? "" : deviceName);
    values.put(COLUMN_DEVICE_MAC, mac==null ? "" : mac);
    db.insert(TABLE_SERVER, "", values);
    db.close();
  }
  
  public void addAsClient(Device device) {
    String deviceName = device.getDeviceName();
    String mac = device.getMacAddress();
    
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_DEVICE_NAME, deviceName==null ? "" : deviceName);
    values.put(COLUMN_DEVICE_MAC, mac==null ? "" : mac);
    db.insert(TABLE_CLIENT, "", values);
    db.close();
  }
  
  public List<Device> getDevicesAsServer() {
    SQLiteDatabase db = this.getReadableDatabase();
    List<Device> devices = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_SERVER + ";", null);
    
    while (c.moveToNext()) {
      Device device = new Device();
  
      device.setId(c.getInt(c.getColumnIndex(COLUMN_DEVICE_ID)));
      device.setDeviceName(c.getString(c.getColumnIndex(COLUMN_DEVICE_NAME)));
      device.setMacAddress(c.getString(c.getColumnIndex(COLUMN_DEVICE_MAC)));
      
      devices.add(device);
    }
    
    c.close();
    db.close();
    return devices;
  }
  
  public List<Device> getDevicesAsClient() {
    SQLiteDatabase db = this.getReadableDatabase();
    List<Device> devices = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLIENT + ";", null);
    
    while (c.moveToNext()) {
      Device device = new Device();
  
      device.setId(c.getInt(c.getColumnIndex(COLUMN_DEVICE_ID)));
      device.setDeviceName(c.getString(c.getColumnIndex(COLUMN_DEVICE_NAME)));
      device.setMacAddress(c.getString(c.getColumnIndex(COLUMN_DEVICE_MAC)));
      
      devices.add(device);
    }
    
    c.close();
    db.close();
    return devices;
  }
  
  public void removeAsServer(String macAddress){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_SERVER + " WHERE " + COLUMN_DEVICE_MAC + "=\"" + macAddress + "\";" );
    db.close();
  }
  
  public void removeAsClient(String macAddress){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CLIENT + " WHERE " + COLUMN_DEVICE_MAC + "=\"" + macAddress + "\";" );
    db.close();
  }
  
  public void updateAsClient(Device device) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COLUMN_DEVICE_ID, device.getId());
    contentValues.put(COLUMN_DEVICE_NAME, device.getDeviceName());
    contentValues.put(COLUMN_DEVICE_MAC, device.getMacAddress());
    db.update(TABLE_CLIENT, contentValues, "id = ?", new String[]{device.getId() + ""});
  }
}
