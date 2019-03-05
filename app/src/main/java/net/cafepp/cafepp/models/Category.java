package net.cafepp.cafepp.models;

public class Category {
  private int _id;
  private String categoryName;
  private String categoryImage;
  
  public Category(String categoryName, String categoryImage){
    this.categoryName = categoryName;
    this.categoryImage = categoryImage;
  }
  
  public Category(String kategoriAdi){
    this.categoryName = kategoriAdi;
  }
  
  public String getCategoryName() { return categoryName; }
  
  public String getCategoryImage() {
    return categoryImage;
  }
  
  public int get_id() {
    return _id;
  }
  
  public void set_id(int _id) {
    this._id = _id;
  }
}
