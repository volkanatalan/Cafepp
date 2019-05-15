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
  private static final String COLUMN_SITUATION = "Situation";
  
  public TableDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
    String createPositionsTable =
        "CREATE TABLE " + TABLE_LOCATIONS + "(" +
            COLUMN_LOCATION + " TEXT, " +
            COLUMN_NUMBER + " INTEGER " + ");";
    
    
    String createContentsTable =
        "CREATE TABLE " + TABLE_CONTENTS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_LOCATION + " TEXT," +
            COLUMN_OPENING_DATE + " TEXT," +
            COLUMN_PRICE + " TEXT," +
            COLUMN_SITUATION + " TEXT" + ");";
  
    
    db.execSQL(createPositionsTable);
    db.execSQL(createContentsTable);
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
    onCreate(db);
  }
  
  public void add(Table table) {
    String name = table.getName();
    String position = table.getLocation();
    String openingDate = table.getOpeningDate().toString();
    String price = table.getPrice() + "";
    String situation = table.getSituation().toString();
  
    if (!hasTable(table)) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(COLUMN_NAME, name == null ? "" : name);
      values.put(COLUMN_LOCATION, position == null ? "" : position);
      values.put(COLUMN_OPENING_DATE, openingDate);
      values.put(COLUMN_PRICE, price);
      values.put(COLUMN_SITUATION, situation);
      db.insert(TABLE_CONTENTS, "", values);
      db.close();
    }
    
    else {
      remove(table);
      add(table);
    }
  }
  
  public boolean hasTable(Table table) {
    String tableName = table.getName();
    String position = table.getLocation();
    
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery(
        "SELECT * FROM " + TABLE_CONTENTS +
            " WHERE " + COLUMN_NAME + " = \"" + tableName + "\" " +
            "AND " + COLUMN_LOCATION + " =\"" + position + "\";", null);
    
    
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
      
      try {
        table.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
        table.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
        table.setLocation(c.getString(c.getColumnIndex(COLUMN_LOCATION)));
        table.setOpeningDate(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                                 .parse(c.getString(c.getColumnIndex(COLUMN_OPENING_DATE))));
        table.setPrice(Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_PRICE))));
        table.setSituation(c.getString(c.getColumnIndex(COLUMN_SITUATION)));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      
      tables.add(table);
    }
    
    c.close();
    db.close();
    return tables;
  }
  
  public void addTableLocation(TableLocation location) {
    String name = location.getName();
    int position = location.getNumber();
    
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_LOCATION, name == null ? "" : name);
    values.put(COLUMN_NUMBER, position);
    db.insert(TABLE_LOCATIONS, "", values);
    db.close();
  }
  
  public List<TableLocation> getTableLocations(){
    List<TableLocation> locations = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LOCATIONS + ";", null);
  
    while (c.moveToNext()) {
      TableLocation loc = new TableLocation();
      loc.setName(c.getString(c.getColumnIndex(COLUMN_LOCATION)));
      loc.setNumber(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_NUMBER))));
      locations.add(loc);
    }
    
    c.close();
    db.close();
    
    return locations;
  }
  
  public boolean hasTableLocation(String location) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_LOCATION + " FROM " + TABLE_LOCATIONS + ";", null);
  
    while (c.moveToNext()) {
      String name = c.getString(c.getColumnIndex(COLUMN_LOCATION));
      if (name.equals(location))
        return true;
    }
    
    return false;
  }
  
  public void remove(Table table){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CONTENTS +
                   " WHERE " + COLUMN_NAME + " = \"" + table.getName() + "\" " +
                   "AND " + COLUMN_LOCATION + " =\"" + table.getLocation() + "\";");
    db.close();
  }
}
