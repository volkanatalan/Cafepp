package net.cafepp.cafepp.objects;

/**
 * Every product has to be in a category. For example; water can be in the "Drinks" category.
 */

public class Category {
  private int _id;
  private String categoryNo;
  private String categoryName;
  private String categoryImage;
  
  public Category(int _id, String categoryNo, String categoryName, String categoryImage){
    this._id = _id;
    this.categoryNo = categoryNo;
    this.categoryName = categoryName;
    this.categoryImage = categoryImage;
  }
  
  public String getCategoryNo() {
    return categoryNo;
  }
  
  public void setCategoryNo(String categoryNo) {
    this.categoryNo = categoryNo;
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