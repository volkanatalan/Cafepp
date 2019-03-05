package net.cafepp.cafepp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import java.util.ArrayList;

import net.cafepp.cafepp.models.Category;

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
  
  public void addCategory (Category category){
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<String> categoryList= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES , null);
    while(c.moveToNext()){
      categoryList.add(c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME)));
    }
    c.close();
    long a = categoryList.size();
    
    ContentValues values = new ContentValues();
    values.put(COLUMN_CATEGORY_NO, a);
    values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
    values.put(COLUMN_CATEGORY_IMAGE_PATH, category.getCategoryImage());
    db.insert(TABLE_CATEGORIES, null, values);
    db.close();
  }
  
  public void removeCategory(String category){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME + "=\"" + category + "\";" );
    db.close();
  }
  
  public void removeAll() {
    // db.delete(String tableName, String whereClause, String[] whereArgs);
    // If whereClause is null, it will delete all rows.
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CATEGORIES);
    db.close();
  }
  
  public ArrayList<Pair<Long, String>> getCategorieAndNo() {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Pair<Long, String>> categoryList= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
    while(c.moveToNext()){
      categoryList.add(new Pair<>(c.getLong(c.getColumnIndex(COLUMN_CATEGORY_NO)),
          c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME))));
    }
    c.close();
    db.close();
    return categoryList;
  }
  
  public ArrayList<Category> getCategory() {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Category> categoryList= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CATEGORIES , null);
    while(c.moveToNext()){
      categoryList.add( new Category(c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME))));
    }
    c.close();
    db.close();
    return categoryList;
  }
  
  public ArrayList<String> getCategoryNames(){
    ArrayList<Category> categories = getCategory();
    ArrayList<String> categoryNames = new ArrayList<>();
    for (Category category : categories){
      categoryNames.add(category.getCategoryName());
    }
    return categoryNames;
  }
  
  public ArrayList<String> getImagePathes() {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<String> images= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CATEGORIES , null);
    while(c.moveToNext()){
      images.add(c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_PATH)));
    }
    c.close();
    db.close();
    return images;
  }
  
  public String getImagePathFromName(String name) {
    SQLiteDatabase db = this.getReadableDatabase();
    String imagePath;
    Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME +
                               " = \"" + name + "\"" , null);
    c.moveToFirst();
    imagePath = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_PATH));
    c.close();
    db.close();
    return imagePath;
  }
  
  public String toString(){
    String dbString = "";
    SQLiteDatabase db = getWritableDatabase();
    String query = "SELECT " + COLUMN_CATEGORY_NAME + " FROM " + TABLE_CATEGORIES + " WHERE 1";
    Cursor c = db.rawQuery(query, null);
    c.moveToFirst();
    
    while (!c.isAfterLast()){
      if (c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME)) != null){
        dbString += c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
        dbString += "\n";
      }
      c.moveToNext();
    }
    
    c.close();
    db.close();
    return dbString;
  }
}
