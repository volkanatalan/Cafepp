package net.cafepp.cafepp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;
import java.util.ArrayList;

import net.cafepp.cafepp.objects.ChosenIngredient;
import net.cafepp.cafepp.objects.Category;
import net.cafepp.cafepp.objects.Ingredient;
import net.cafepp.cafepp.objects.Product;

public class ProductDatabase extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "ProductDatabase.db";
  
  private static final String TABLE_CATEGORIES = "Categories";
  private static final String TABLE_INGREDIENTS = "Ingredients";
  private static final String TABLE_CHOSEN_ITEMS = "ChosenItems";
  private static final String TABLE_PRODUCTS = "Products";
  // IOP means "Ingredients Of Products"
  private static final String TABLE_IOP = "IngredientsOfProducts";
  private static final String TABLE_PRODUCT_IMAGES = "ProductImagesTable";
  
  private static final String COLUMN_ID = "_id";
  private static final String COLUMN_NO = "Num";
  private static final String COLUMN_NAME = "Name";
  private static final String COLUMN_IMAGE_PATH = "ImagePath";
  private static final String COLUMN_AMOUNT = "Amount";
  private static final String COLUMN_UNIT_TYPE = "UnitType";
  private static final String COLUMN_CALORIE = "Calorie";
  private static final String COLUMN_UNIT_WEIGHT = "WeightOfUnit";
  private static final String COLUMN_TYPE = "Type";
  private static final String COLUMN_SHOW = "Show";
  private static final String COLUMN_INGREDIENT_TYPE = "IngredientType";
  private static final String COLUMN_CATEGORY = "Category";
  private static final String COLUMN_PRICE = "Price";
  private static final String COLUMN_DESCRIPTION = "Description";
  private static final String COLUMN_GRAMMAGE = "Grammage";
  private static final String COLUMN_PRODUCT = "Product";
  private static final String COLUMN_INGREDIENT = "Ingredient";
  private static final String COLUMN_INGREDIENT_AMOUNT = "IngredientAmount";
  private static final String COLUMN_IMAGE_NO = "ImageNo";
  private static final String COLUMN_PRODUCT_NAME = "ProductName";
  
  public ProductDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
    String createTableCategories =
        "CREATE TABLE " + TABLE_CATEGORIES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NO + " LONG, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_IMAGE_PATH + " TEXT " + ");";
  
  
    String createTableIngredients =
        "CREATE TABLE " + TABLE_INGREDIENTS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_AMOUNT + " TEXT, " +
            COLUMN_UNIT_TYPE + " TEXT, " +
            COLUMN_CALORIE + " TEXT, " +
            COLUMN_UNIT_WEIGHT + " TEXT " + ");";
    
  
    String createTableChosenItems =
        "CREATE TABLE " + TABLE_CHOSEN_ITEMS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TYPE + " TEXT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_SHOW + " TEXT, " +
            COLUMN_INGREDIENT_TYPE + " TEXT, " +
            COLUMN_AMOUNT + " TEXT, " +
            COLUMN_UNIT_TYPE + " TEXT, " +
            COLUMN_CALORIE + " TEXT, " +
            COLUMN_UNIT_WEIGHT + " TEXT " + ");";
  
    
    String createTableProducts =
        "CREATE TABLE " + TABLE_PRODUCTS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NO + " LONG, " +
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_PRICE + " TEXT, " +
            COLUMN_IMAGE_PATH + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_CALORIE + " TEXT, " +
            COLUMN_GRAMMAGE + " TEXT " + ");";
    
  
    String createTableIOP =
        "CREATE TABLE " + TABLE_IOP + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SHOW + " INTEGER, " +
            COLUMN_PRODUCT + " TEXT, " +
            COLUMN_INGREDIENT + " TEXT, " +
            COLUMN_INGREDIENT_AMOUNT + " TEXT, " +
            COLUMN_UNIT_TYPE + " TEXT " + ");";
  
    
    String createTableImages =
        "CREATE TABLE " + TABLE_PRODUCT_IMAGES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_IMAGE_NO + " LONG, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_IMAGE_PATH + " TEXT " + ");";
    
  
    db.execSQL(createTableCategories);
    db.execSQL(createTableIngredients);
    db.execSQL(createTableChosenItems);
    db.execSQL(createTableProducts);
    db.execSQL(createTableIOP);
    db.execSQL(createTableImages);
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHOSEN_ITEMS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_IOP);
    onCreate(db);
  }
  
  
  
  
  ////////////////////////////////////////////////
  /////////////// CATEGORIES TABLE ///////////////
  ////////////////////////////////////////////////
  
  public void addChosenCategory(Category category){
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<String> categoryList= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES , null);
    while(c.moveToNext()){
      categoryList.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
    }
    c.close();
    long a = categoryList.size();
    
    ContentValues values = new ContentValues();
    values.put(COLUMN_NO, a);
    values.put(COLUMN_NAME, category.getCategoryName());
    values.put(COLUMN_IMAGE_PATH, category.getCategoryImage());
    db.insert(TABLE_CATEGORIES, null, values);
    db.close();
  }
  
  public void removeCategory(String category){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_NAME + "=\"" + category + "\";" );
    db.close();
  }
  
  public void clearCategories() {
    // db.delete(String tableName, String whereClause, String[] whereArgs);
    // If whereClause is null, it will delete all rows.
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CATEGORIES);
    db.close();
  }
  
  public ArrayList<Pair<Long, String>> getCategoryAndNo() {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Pair<Long, String>> categoryList= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
    while(c.moveToNext()){
      categoryList.add(new Pair<>(c.getLong(c.getColumnIndex(COLUMN_NO)),
          c.getString(c.getColumnIndex(COLUMN_NAME))));
    }
    c.close();
    db.close();
    return categoryList;
  }
  
  public ArrayList<Category> getCategories() {
    ArrayList<Category> categoryList= new ArrayList<>();
    int id;
    String no;
    String name;
    String imagePath;
    
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES , null);
    
    while(c.moveToNext()){
      id = Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID)));
      no = c.getString(c.getColumnIndex(COLUMN_NO));
      name = c.getString(c.getColumnIndex(COLUMN_NAME));
      imagePath = c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH));
      categoryList.add(new Category(id, no, name, imagePath));
    }
    
    c.close();
    db.close();
    return categoryList;
  }
  
  public ArrayList<String> getCategoryNames(){
    ArrayList<String> categoryNames = new ArrayList<>();
    
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_NAME +
                               " FROM " + TABLE_CATEGORIES , null);
  
    while (c.moveToNext()) {
      categoryNames.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
    }
    
    c.close();
    db.close();
    return categoryNames;
  }
  
  public ArrayList<String> getCategoryImagePaths() {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<String> images= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CATEGORIES , null);
    while(c.moveToNext()){
      images.add(c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH)));
    }
    c.close();
    db.close();
    return images;
  }
  
  public String getCategoryImagePathFromName(String name) {
    SQLiteDatabase db = this.getReadableDatabase();
    String imagePath;
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_NAME +
                               " = \"" + name + "\"" , null);
    c.moveToFirst();
    imagePath = c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH));
    c.close();
    db.close();
    return imagePath;
  }
  
  public String CategoriesToString(){
    String dbString = "";
    SQLiteDatabase db = getWritableDatabase();
    String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_CATEGORIES + " WHERE 1";
    Cursor c = db.rawQuery(query, null);
    c.moveToFirst();
    
    while (!c.isAfterLast()){
      if (c.getString(c.getColumnIndex(COLUMN_NAME)) != null){
        dbString += c.getString(c.getColumnIndex(COLUMN_NAME));
        dbString += "\n";
      }
      c.moveToNext();
    }
    
    c.close();
    db.close();
    return dbString;
  }
  
  
  
  
  
  /////////////////////////////////////////////////
  /////////////// INGREDIENTS TABLE ///////////////
  /////////////////////////////////////////////////
  
  public void addChosenIngredient(Ingredient ingredient){
    SQLiteDatabase db = this.getReadableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_NAME, ingredient.getName());
    values.put(COLUMN_AMOUNT, ingredient.getAmount());
    values.put(COLUMN_UNIT_TYPE, ingredient.getAmountType());
    values.put(COLUMN_CALORIE, ingredient.getCalorie());
    values.put(COLUMN_UNIT_WEIGHT, ingredient.getUnitWeight());
    db.insert(TABLE_INGREDIENTS, null, values);
    db.close();
  }
  
  public void removeIngredient(String ingredient){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_NAME + "=\"" + ingredient + "\";" );
    db.close();
  }
  
  public void clearIngredients() {
    // db.delete(String tableName, String whereClause, String[] whereArgs);
    // If whereClause is null, it will delete all rows.
    SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
    db.execSQL("DELETE FROM " + TABLE_INGREDIENTS);
    db.close();
  }
  
  public String ingredientsToString(){
    String dbString = "";
    SQLiteDatabase db = getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_INGREDIENTS + ";", null);
    c.moveToFirst();
    
    while (!c.isAfterLast()){
      if (c.getString(c.getColumnIndex(COLUMN_NAME)) != null){
        dbString += c.getString(c.getColumnIndex(COLUMN_NAME));
        dbString += " ";
        dbString += c.getString(c.getColumnIndex(COLUMN_AMOUNT));
        dbString += " ";
        dbString += c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE));
        dbString += " ";
        dbString += c.getString(c.getColumnIndex(COLUMN_UNIT_WEIGHT));
        dbString += "\n";
      }
      c.moveToNext();
    }
    
    c.close();
    db.close();
    return dbString;
  }
  
  public ArrayList<String> getIngredientNames(){
    SQLiteDatabase db = getReadableDatabase();
    ArrayList<String> ingredients = new ArrayList<>();
    String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_INGREDIENTS;
    
    Cursor c = db.rawQuery(query, null);
    //c.moveToFirst();
    
    while(c.moveToNext()){
      if(!ingredients.contains(c.getString(c.getColumnIndex(COLUMN_NAME)))){
        ingredients.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
      }
    }
    c.close();
    db.close();
    return ingredients;
  }
  
  public String getIngredientUnitType(String ingredient){
    SQLiteDatabase db = getReadableDatabase();
    String unitType ="";
    String query = "SELECT " + COLUMN_UNIT_TYPE + " FROM " + TABLE_INGREDIENTS + " WHERE "
                       + COLUMN_NAME + " =\"" + ingredient + "\";";
    Cursor c = db.rawQuery(query, null);
    while (c.moveToNext()){
      unitType = c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE));
    }
    c.close();
    db.close();
    return unitType;
  }
  
  public String getIngredientCalorie(String ingredient) {
    String calorie = null;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_CALORIE +
                               " FROM " + TABLE_INGREDIENTS +
                               " WHERE " + COLUMN_NAME + "=\"" + ingredient + "\";",
        null);
    while (c.moveToNext()) calorie = c.getString(c.getColumnIndex(COLUMN_CALORIE));
    c.close();
    db.close();
    return calorie;
  }
  
  public String getIngredientUnitWeight(String ingredient) {
    String weightOfUnit = null;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_UNIT_WEIGHT +
                               " FROM " + TABLE_INGREDIENTS +
                               " WHERE " + COLUMN_NAME + "=\"" + ingredient + "\";",
        null);
    while (c.moveToNext()) weightOfUnit = c.getString(c.getColumnIndex(COLUMN_UNIT_WEIGHT));
    c.close();
    db.close();
    return weightOfUnit;
  }
  
  public Boolean hasIngredient(String ingredient) {
    Boolean has;
    String s = "";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_NAME +
                               "=\"" + ingredient + "\";", null);
    while (c.moveToNext()) {
      s = c.getString(c.getColumnIndex(COLUMN_NAME));
    }
    has = !s.equals("");
    c.close();
    db.close();
    return has;
  }
  
  
  
  
  //////////////////////////////////////////////////
  //////////////  CHOSEN ITEMS TABLE  //////////////
  //////////////////////////////////////////////////
  
  
  public void addChosenIngredient(String ingredient, String ingredientType) {
    ArrayList<String> items = getChosenItems("ingredient");
    if (items.contains(ingredient)) {
      removeChosenItem("ingredient", ingredient);
    }
    String unitType = null;
    String calorie = null;
    unitType = getIngredientUnitType(ingredient);
    calorie = getIngredientCalorie(ingredient);
    String weightOfUnit = getIngredientUnitWeight(ingredient);
    
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_TYPE, "ingredient");
    values.put(COLUMN_NAME, ingredient);
    values.put(COLUMN_SHOW, "1");
    values.put(COLUMN_INGREDIENT_TYPE, ingredientType);
    values.put(COLUMN_UNIT_TYPE, unitType);
    values.put(COLUMN_CALORIE, calorie);
    values.put(COLUMN_UNIT_WEIGHT, weightOfUnit);
    db.insert(TABLE_CHOSEN_ITEMS, null, values);
    db.close();
  }
  
  public void addChosenIngredient(String ingredient, String ingredientType, boolean show, String amount, String unitType) {
    ArrayList<String> items = getChosenItems("ingredient");
    if (items.contains(ingredient)) {
      removeChosenItem("ingredient", ingredient);
    }
    
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_NAME, ingredient);
    values.put(COLUMN_SHOW, show ? "1" : "0");
    values.put(COLUMN_TYPE, "ingredient");
    values.put(COLUMN_INGREDIENT_TYPE, ingredientType);
    values.put(COLUMN_AMOUNT, amount);
    values.put(COLUMN_UNIT_TYPE, unitType);
    db.insert(TABLE_CHOSEN_ITEMS, null, values);
    db.close();
  }
  
  public ArrayList<ChosenIngredient> getChosenIngredients() {
    ArrayList<ChosenIngredient> chosenIngredients = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHOSEN_ITEMS +
                               " WHERE " + COLUMN_TYPE + " = \"ingredient\";",
        null);
    while(c.moveToNext()){
      chosenIngredients.add(new ChosenIngredient(
          c.getString(c.getColumnIndex(COLUMN_NAME)),
          c.getString(c.getColumnIndex(COLUMN_INGREDIENT_TYPE)),
          c.getString(c.getColumnIndex(COLUMN_AMOUNT))==null ? -1f :
              Float.valueOf(c.getString(c.getColumnIndex(COLUMN_AMOUNT))),
          c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE)),
          Float.valueOf(c.getString(c.getColumnIndex(COLUMN_CALORIE))),
          Float.valueOf(c.getString(c.getColumnIndex(COLUMN_UNIT_WEIGHT))),
          c.getString(c.getColumnIndex(COLUMN_SHOW)).equals("1")
      ));
    }
    c.close();
    db.close();
    return chosenIngredients;
  }
  
  public ArrayList<ChosenIngredient> getChosenIngredients(ArrayList<String> chosenIngredientNames) {
    ArrayList<ChosenIngredient> chosenIngredients = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    for (String s : chosenIngredientNames) {
      Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHOSEN_ITEMS +
                                 " WHERE " + COLUMN_NAME + " = \"" +
                                 s + "\";", null);
      while (c.moveToNext()) {
        chosenIngredients.add(new ChosenIngredient(
            c.getString(c.getColumnIndex(COLUMN_NAME)),
            c.getString(c.getColumnIndex(COLUMN_INGREDIENT_TYPE)),
            c.getString(c.getColumnIndex(COLUMN_AMOUNT)) == null ? 0f :
                c.getString(c.getColumnIndex(COLUMN_AMOUNT)).equals("") ? 0f :
                    Float.valueOf(c.getString(c.getColumnIndex(COLUMN_AMOUNT))),
            c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE)),
            Float.valueOf(c.getString(c.getColumnIndex(COLUMN_CALORIE))),
            Float.valueOf(c.getString(c.getColumnIndex(COLUMN_UNIT_WEIGHT))),
            c.getString(c.getColumnIndex(COLUMN_SHOW)).equals("1")
        ));
      }
      c.close();
    }
    db.close();
    return chosenIngredients;
  }
  
  public void addChosenCategory(String category) {
    ArrayList<String> items = getChosenItems("category");
    if (!items.contains(category)) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(COLUMN_TYPE, "category");
      values.put(COLUMN_NAME, category);
      db.insert(TABLE_CHOSEN_ITEMS, null, values);
      db.close();
    }
  }
  
  public ArrayList<String> getChosenItems(String type) {
    ArrayList<String> chosenItems = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_NAME +
                               " FROM " + TABLE_CHOSEN_ITEMS +
                               " WHERE " + COLUMN_TYPE + " = \"" + type + "\";",
        null);
    while(c.moveToNext()){
      chosenItems.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
    }
    c.close();
    db.close();
    return chosenItems;
  }
  
  public String getChosenItemAmount(String ingredient) {
    String amount = "";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_AMOUNT + " FROM " + TABLE_CHOSEN_ITEMS + " WHERE " +
                               COLUMN_NAME + " = \"" + ingredient + "\";",
        null);
    while (c.moveToNext()) {
      amount = c.getString(c.getColumnIndex(COLUMN_AMOUNT));
    }
    c.close();
    db.close();
    return amount;
  }
  
  private void addChosenItem(String type, String ingredientType, boolean show, String item, String amount, String unitType) {
    ArrayList<String> items = getChosenItems(type);
    if (!items.contains(item)) {
      String calorie = "";
      String weightOfUnit = "";
      if (ingredientType.equals("ingredient")) {
        calorie = getIngredientCalorie(item);
        weightOfUnit = getIngredientUnitWeight(item);
      } else if (ingredientType.equals("product")) {
        calorie = getProductCalorie(item);
        weightOfUnit = getProductGrammage(item);
      }
      
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(COLUMN_TYPE, type);
      values.put(COLUMN_NAME, item);
      values.put(COLUMN_SHOW, show ? "1" : "0");
      values.put(COLUMN_INGREDIENT_TYPE, ingredientType);
      values.put(COLUMN_AMOUNT, amount);
      values.put(COLUMN_UNIT_TYPE, unitType);
      values.put(COLUMN_CALORIE, calorie);
      values.put(COLUMN_UNIT_WEIGHT, weightOfUnit);
      db.insert(TABLE_CHOSEN_ITEMS, null, values);
      db.close();
    }
  }
  
  public void setChosenIngredientShow(String ingredientName, boolean show) {
    String itemType = null;
    String ingredientType = null;
    String unitType = null;
    String amount = null;
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHOSEN_ITEMS +
                               " WHERE " + COLUMN_NAME + " =\"" + ingredientName + "\";",
        null);
    while (c.moveToNext()) {
      itemType = c.getString(c.getColumnIndex(COLUMN_TYPE));
      ingredientType = c.getString(c.getColumnIndex(COLUMN_INGREDIENT_TYPE));
      unitType = c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE));
      amount = c.getString(c.getColumnIndex(COLUMN_AMOUNT));
    }
    c.close();
    db.close();
    removeChosenItem("ingredient", ingredientName);
    addChosenItem(itemType, ingredientType, show, ingredientName, amount, unitType);
  }
  
  public void setAllIngredientsShow(boolean show) {
    String itemType = "ingredient";
    String ingredient;
    String ingredientType;
    String unitType;
    String amount;
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHOSEN_ITEMS +
                               " WHERE " + COLUMN_TYPE + " =\"" + itemType + "\";",
        null);
    while (c.moveToNext()) {
      ingredient = c.getString(c.getColumnIndex(COLUMN_NAME));
      ingredientType = c.getString(c.getColumnIndex(COLUMN_INGREDIENT_TYPE));
      unitType = c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE));
      amount = c.getString(c.getColumnIndex(COLUMN_AMOUNT));
      removeChosenItem("ingredient", ingredient);
      addChosenItem(itemType, ingredientType, show, ingredient, amount, unitType);
    }
    c.close();
    db.close();
  }
  
  public void setChosenIngredientAmount(String ingredientName, String amount) {
    String itemType = null;
    String ingredientType = null;
    String unitType = null;
    boolean show = true;
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHOSEN_ITEMS +
                               " WHERE " + COLUMN_NAME + " =\"" + ingredientName + "\";",
        null);
    while (c.moveToNext()) {
      itemType = c.getString(c.getColumnIndex(COLUMN_TYPE));
      ingredientType = c.getString(c.getColumnIndex(COLUMN_INGREDIENT_TYPE));
      unitType = c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE));
      show = c.getString(c.getColumnIndex(COLUMN_SHOW)).equals("1");
    }
    c.close();
    db.close();
    removeChosenItem("ingredient", ingredientName);
    addChosenItem(itemType, ingredientType, show, ingredientName, amount, unitType);
  }
  
  public void removeChosenItem(String itemType, String itemName) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CHOSEN_ITEMS + " WHERE " + COLUMN_TYPE + "=\"" + itemType + "\" AND " +
                   COLUMN_NAME + " = \"" + itemName + "\";");
    db.close();
  }
  
  public void clearChosenItems() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_CHOSEN_ITEMS + ";");
    db.close();
  }
  
  
  
  
  
  //////////////////////////////////////////////
  /////////////// PRODUCTS TABLE ///////////////
  //////////////////////////////////////////////
  
  public void addProduct (String category, String name, String image, String price, String description, String calorie, String gram){
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<String> products= new ArrayList<>();
    Cursor c = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_PRODUCTS , null);
    while(c.moveToNext()){
      products.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
    }
    c.close();
    long a = products.size();
    
    ContentValues values = new ContentValues();
    values.put(COLUMN_NO, a);
    values.put(COLUMN_CATEGORY, category);
    values.put(COLUMN_NAME, name);
    values.put(COLUMN_IMAGE_PATH, image);
    values.put(COLUMN_PRICE, price);
    values.put(COLUMN_DESCRIPTION, description);
    values.put(COLUMN_CALORIE, calorie);
    values.put(COLUMN_GRAMMAGE, gram);
    db.insert(TABLE_PRODUCTS, null, values);
    db.close();
  }
  
  public ArrayList<Product> getProductsFromCategory(String category) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Product> categories = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_CATEGORY +
                               " = \"" + category + "\";", null);
    while(c.moveToNext()){
      categories.add( new Product(
          c.getString(c.getColumnIndex(COLUMN_CATEGORY)),
          c.getString(c.getColumnIndex(COLUMN_NAME)),
          c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH)),
          c.getString(c.getColumnIndex(COLUMN_PRICE)),
          c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)),
          c.getString(c.getColumnIndex(COLUMN_CALORIE)),
          c.getString(c.getColumnIndex(COLUMN_GRAMMAGE))));
    }
    c.close();
    db.close();
    return categories;
  }
  
  public ArrayList<Product> getAllProducts() {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Product> categories = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + ";", null);
    while(c.moveToNext()){
      categories.add( new Product(
          c.getString(c.getColumnIndex(COLUMN_CATEGORY)),
          c.getString(c.getColumnIndex(COLUMN_NAME)),
          c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH)),
          c.getString(c.getColumnIndex(COLUMN_PRICE)),
          c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)),
          c.getString(c.getColumnIndex(COLUMN_CALORIE)),
          c.getString(c.getColumnIndex(COLUMN_GRAMMAGE))));
    }
    c.close();
    db.close();
    return categories;
  }
  
  public Product getProductFromName(String productName){
    SQLiteDatabase db = getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_NAME +
                               "=\"" + productName + "\";", null);
    Product product = new Product();
    while(c.moveToNext()){
      product.setCategory(productName);
      product.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
      product.setImages(getProductImages(productName));
      product.setPrice(c.getString(c.getColumnIndex(COLUMN_PRICE)));
      product.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
      product.setCalorie(c.getString(c.getColumnIndex(COLUMN_CALORIE)));
      product.setGrammage(c.getString(c.getColumnIndex(COLUMN_GRAMMAGE)));
      product.setIngredients(getIngredientsOfProduct(productName));
    }
    c.close();
    db.close();
    return product;
  }
  
  public ArrayList<String> getProductNames(){
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<String> products = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_PRODUCTS , null);
    while(c.moveToNext()){
      if(!products.contains(c.getString(c.getColumnIndex(COLUMN_NAME)))){
        products.add(c.getString(c.getColumnIndex(COLUMN_NAME)));
      }
    }
    c.close();
    db.close();
    return products;
  }
  
  public String getProductCalorie(String product) {
    String calorie = "";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_CALORIE +
                               " FROM " + TABLE_PRODUCTS +
                               " WHERE " + COLUMN_NAME + " = \"" + product + "\";",
        null);
    while (c.moveToNext()) calorie = c.getString(c.getColumnIndex(COLUMN_CALORIE));
    c.close();
    db.close();
    return calorie;
  }
  
  public String getProductGrammage(String productName){
    String grammage;
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_GRAMMAGE +
                               " FROM " + TABLE_PRODUCTS +
                               " WHERE " + COLUMN_NAME + " = \"" + productName + "\";",
        null);
    c.moveToFirst();
    grammage = c.getString(c.getColumnIndex(COLUMN_NAME));
    c.close();
    db.close();
    return grammage;
  }
  
  
  
  
  
  /////////////////////////////////////////////////////////////
  /////////////// INGREDIENTS OF PRODUCTS TABLE ///////////////
  /////////////////////////////////////////////////////////////
  
  public ArrayList<String> getIngredientsOfProduct(String product){
    SQLiteDatabase db = getReadableDatabase();
    ArrayList<String> ingredientList = new ArrayList<String>();
    Cursor c = db.rawQuery("SELECT " + COLUMN_INGREDIENT + " FROM " + TABLE_IOP + " WHERE " +
                               COLUMN_PRODUCT + " = \"" + product + "\";", null);
    while(c.moveToNext()){
      String CIndex = c.getString(c.getColumnIndex(COLUMN_INGREDIENT));
      if(!ingredientList.contains(CIndex)){
        ingredientList.add(CIndex);
      }
    }
    c.close();
    db.close();
    return ingredientList;
  }
  
  public ArrayList<Ingredient> getIngredientsToShow(String product){
    SQLiteDatabase db = getReadableDatabase();
    ArrayList<Ingredient> ingredientList = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT * FROM " + TABLE_IOP +
                               " WHERE " + COLUMN_PRODUCT + " = \"" + product + "\" AND " +
                               COLUMN_SHOW + " = \"1\";", null);
    while(c.moveToNext()){
      Ingredient ingredient = new Ingredient(
          c.getString(c.getColumnIndex(COLUMN_PRODUCT)),
          c.getString(c.getColumnIndex(COLUMN_INGREDIENT)),
          c.getString(c.getColumnIndex(COLUMN_INGREDIENT_AMOUNT)),
          c.getString(c.getColumnIndex(COLUMN_UNIT_TYPE)),
          c.getString(c.getColumnIndex(COLUMN_SHOW)).equals("1"));
      if(!ingredientList.contains(ingredient)){
        ingredientList.add(ingredient);
      }
    }
    c.close();
    db.close();
    return ingredientList;
  }
  
  public void addIngredientToProduct(int show, String product, String ingredient, String amount, String type){
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_SHOW, show);
    values.put(COLUMN_PRODUCT, product);
    values.put(COLUMN_INGREDIENT, ingredient);
    values.put(COLUMN_INGREDIENT_AMOUNT, amount);
    values.put(COLUMN_UNIT_TYPE, type);
    db.insert(TABLE_IOP, null, values);
    db.close();
  }
  
  public void removeIngredientFromProduct(String ingredient, String product){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_IOP +
                   " WHERE " + COLUMN_PRODUCT + "=\"" + product + "\" AND " +
                   COLUMN_INGREDIENT + "=\"" + ingredient + "\";");
    db.close();
  }
  
  public void clearIngredientsOfProductsTable() {
    // db.delete(String tableName, String whereClause, String[] whereArgs);
    // If whereClause is null, it will delete all rows.
    SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
    db.execSQL("DELETE FROM " + TABLE_IOP);
    db.close();
  }
  
  
  
  
  ////////////////////////////////////////////////////
  /////////////// PRODUCT IMAGES TABLE ///////////////
  ////////////////////////////////////////////////////
  
  public ArrayList<String> getProductImages (String product){
    ArrayList<String> imageList = new ArrayList<>();
    
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_IMAGE_PATH + " FROM " + TABLE_PRODUCT_IMAGES +
                               " WHERE " + COLUMN_PRODUCT_NAME + " = \"" + product + "\";", null);
    while (c.moveToNext()) {
      imageList.add(c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH)));
    }
    c.close();
    return imageList;
  }
  
  
  public String getProductImage (String product, int pos) {
    String image = "";
    ArrayList<String> images = new ArrayList<>();
    
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor c = db.rawQuery("SELECT " + COLUMN_IMAGE_PATH + " FROM " + TABLE_PRODUCT_IMAGES +
                               " WHERE " + COLUMN_PRODUCT_NAME + " = \"" + product + "\";",
        null);
    
    while (c.moveToNext()) {
      images.add(c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH)));
    }
    
    c.close();
    image = images.get(pos);
    return image;
  }
  
  
  public void addProductImage (String product, String image){
    SQLiteDatabase db = this.getWritableDatabase();
    ArrayList<String> productList = new ArrayList<>();
    Cursor c = db.rawQuery("SELECT " + COLUMN_IMAGE_PATH + " FROM " + TABLE_PRODUCT_IMAGES +
                               " WHERE " + COLUMN_PRODUCT_NAME + " = \"" + product + "\";",
        null);
    while(c.moveToNext()){
      productList.add(c.getString(c.getColumnIndex(COLUMN_IMAGE_PATH)));
    }
    c.close();
    long productListSize = productList.size();
    
    ContentValues values = new ContentValues();
    values.put(COLUMN_IMAGE_NO, productListSize);
    values.put(COLUMN_PRODUCT_NAME, product);
    values.put(COLUMN_IMAGE_PATH, image);
    db.insert(TABLE_PRODUCT_IMAGES, null, values);
    db.close();
  }
  
  public void deleteProductImage(String product, String id){
    SQLiteDatabase db = getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_PRODUCT_IMAGES +
                   " WHERE " + COLUMN_PRODUCT_NAME + " = \"" + product + "\""
                   + " AND " + COLUMN_ID + " = \"" + id + "\";" );
    db.close();
  }
  
  public void clearProductImages() {
    // If whereClause is null, it will delete all rows.
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_PRODUCT_IMAGES);
    db.close();
  }
}