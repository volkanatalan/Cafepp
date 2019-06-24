package net.cafepp.cafepp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.cafepp.cafepp.objects.Table;
import net.cafepp.cafepp.objects.TableLocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TableDatabase extends SQLiteOpenHelper {
  
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "TableDatabase.db";
  
  private static final String TABLE_LOCATIONS = "Locations";
  private static final String TABLE_CONTENTS = "Contents";
  
  private static final String COLUMN_ID = "_id";
  private static final String COLUMN_NUMBER = "Number";
  private static final String COLUMN_NAME = "Name";
  private static final String COLUMN_LOCATION = "Location";
  private static final String COLUMN_OPENING_DATE = "OpeningDate";
  private static final String COLUMN_PRICE = "Price";
  private static final String COLUMN_STATUS = "Status";
  
  public TableDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
    String createPositionsTable =
        "CREATE TABLE " + TABLE_LOCATIONS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_NUMBER + " INTEGER " + ");";
    
    
    String createContentsTable =
        "CREATE TABLE " + TABLE_CONTENTS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " INTEGER, " +
            COLUMN_LOCATION + " TEXT," +
            COLUMN_OPENING_DATE + " TEXT," +
            COLUMN_PRICE + " TEXT," +
            COLUMN_STATUS + " TEXT" + ");";
  
    
    db.execSQL(createPositionsTable);
    db.execSQL(createContentsTable);
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
    onCreate(db);
  }
  
  
  /**
   * Returns id of the table.
   */
  public void add(Table table) {
    int id = table.getId();
    int number = table.getNumber();
    String position = table.getLocation();
    Date date = table.getOpeningDate();
    String openingDate = date == null ? "" : date.toString();
    String price = table.getPrice() + "";
    String situation = table.getStatus().toString();
  
    if (id == -1) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(COLUMN_NAME, number);
      values.put(COLUMN_LOCATION, position == null ? "" : position);
      values.put(COLUMN_OPENING_DATE, openingDate);
      values.put(COLUMN_PRICE, price);
      values.put(COLUMN_STATUS, situation);
      db.insert(TABLE_CONTENTS, "", values);
      db.close();
    }
    
    else {
      updateTable(table);
    }
  }
  
  public void remove(Table table){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CONTENTS +
                   " WHERE " + COLUMN_NAME + " = \"" + table.getNumber() + "\" " +
                   "AND " + COLUMN_LOCATION + " =\"" + table.getLocation() + "\";");
    db.close();
  }
  
  public void updateTable(Table table) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COLUMN_ID, table.getId());
    contentValues.put(COLUMN_NAME, table.getNumber());
    contentValues.put(COLUMN_LOCATION, table.getLocation());
    contentValues.put(COLUMN_OPENING_DATE, table.getOpeningDate().toString());
    contentValues.put(COLUMN_PRICE, table.getPrice());
    contentValues.put(COLUMN_STATUS, table.getStatus().toString());
    db.update(TABLE_CONTENTS, contentValues, COLUMN_ID + " = ?", new String[]{table.getId() + ""});
    db.close();
  }
  
  public int getTableId(Table table) {
    String location = table.getLocation();
    int tableNumber = table.getNumber();
    
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_CONTENTS +
        " WHERE " + COLUMN_LOCATION + "= \"" + location +
        "\" AND " + COLUMN_NAME + " = \"" + tableNumber + "\";",
        null);
  
    while (c.moveToNext()) {
      return c.getInt(c.getColumnIndex(COLUMN_ID));
    }
    
    return -1;
  }
  
  public boolean hasTable(Table table) {
    int tableNumber = table.getNumber();
    String location = table.getLocation();
    
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery(
        "SELECT * FROM " + TABLE_CONTENTS +
            " WHERE " + COLUMN_NAME + " = \"" + tableNumber + "\" " +
            "AND " + COLUMN_LOCATION + " =\"" + location + "\";", null);
    
    
    boolean hasTable = c.moveToNext();
  
    c.close();
    db.close();
    return hasTable;
  }
  
  public boolean hasLocationInContents(String locationName) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery(
        "SELECT * FROM " + TABLE_CONTENTS +
            " WHERE " + COLUMN_LOCATION + " = \"" + locationName + "\";", null);
  
    boolean hasTable = c.moveToNext();
  
    c.close();
    db.close();
    return hasTable;
  }
  
  public List<Table> getTables() {
    List<Table> tables = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CONTENTS + ";", null);
    
    while (c.moveToNext()) {
      Table table = new Table();
  
      table.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
      table.setNumber(c.getInt(c.getColumnIndex(COLUMN_NAME)));
      table.setLocation(c.getString(c.getColumnIndex(COLUMN_LOCATION)));
      table.setPrice(Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_PRICE))));
      table.setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)));
      
      try {
        table.setOpeningDate(new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.getDefault())
                                 .parse(c.getString(c.getColumnIndex(COLUMN_OPENING_DATE))));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      
      tables.add(table);
    }
    
    c.close();
    db.close();
    return tables;
  }
  
  public TableLocation add(TableLocation location) {
    String name = location.getName();
    int position = location.getTotalTable();
    
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_NAME, name == null ? "" : name);
    values.put(COLUMN_NUMBER, position);
    db.insert(TABLE_LOCATIONS, "", values);
    db.close();
    
    return getTableLocation(name);
  }
  
  public void remove(TableLocation location) {
    if (hasLocationInContents(location.getName())) {
      location.setTotalTable(-1);
      updateTableLocation(location);
    }
    
    else {
      SQLiteDatabase db = this.getWritableDatabase();
      db.execSQL("DELETE FROM " + TABLE_LOCATIONS +
                    " WHERE " + COLUMN_NAME + " =\"" + location.getName() + "\";");
      db.close();
    }
  }
  
  public TableLocation getTableLocation(String name) {
    TableLocation location = null;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LOCATIONS +
                               " WHERE " + COLUMN_NAME + " = \"" + name + "\";", null);
    
    while (c.moveToNext()) {
      location = new TableLocation();
      location.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
      location.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
      location.setTotalTable(c.getInt(c.getColumnIndex(COLUMN_NUMBER)));
    }
  
    c.close();
    db.close();
    
    return location;
  }
  
  public List<TableLocation> getTableLocations(){
    List<TableLocation> locations = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LOCATIONS + ";", null);
  
    while (c.moveToNext()) {
      TableLocation loc = new TableLocation();
      loc.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
      loc.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
      loc.setTotalTable(c.getInt(c.getColumnIndex(COLUMN_NUMBER)));
      locations.add(loc);
    }
    
    c.close();
    db.close();
    
    return locations;
  }
  
  public boolean hasTableLocation(String location) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_NAME +
                               " FROM " + TABLE_LOCATIONS +
                               " WHERE " + COLUMN_NAME + " = \"" + location + "\";",
        null);
  
    boolean hasLocation = c.moveToNext();
    c.close();
    db.close();
    
    return hasLocation;
  }
  
  public void updateTableLocation(TableLocation location) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COLUMN_ID, location.getId());
    contentValues.put(COLUMN_NAME, location.getName());
    contentValues.put(COLUMN_NUMBER, location.getTotalTable());
    db.update(TABLE_LOCATIONS, contentValues, COLUMN_ID + " = ?", new String[]{location.getId() + ""});
    db.close();
  }
}
