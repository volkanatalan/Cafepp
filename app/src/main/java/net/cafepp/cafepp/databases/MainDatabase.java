package net.cafepp.cafepp.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDatabase extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "MainDatabase.db";
  private static final String TABLE_CATEGORIES = "Categories";
  private static final String COLUMN_ID = "_id";
  private static final String COLUMN_CATEGORY_NO = "CategoryNo";
  private static final String COLUMN_CATEGORY_NAME = "CategoryName";
  private static final String COLUMN_CATEGORY_IMAGE_PATH = "CategoryImagePath";
  private Context context;
  
  public MainDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
    String query = "CREATE TABLE " + TABLE_CATEGORIES + "(" +
                       COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       COLUMN_CATEGORY_NO + " LONG, " +
                       COLUMN_CATEGORY_NAME + " TEXT, " +
                       COLUMN_CATEGORY_IMAGE_PATH + " TEXT " +
                       ");";
    db.execSQL(query);
    
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
    onCreate(db);
    
  }
}
