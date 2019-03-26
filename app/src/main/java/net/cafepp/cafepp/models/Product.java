package net.cafepp.cafepp.models;

import java.util.ArrayList;

public class Product {
  
  private String name;
  private ArrayList<String> images;
  private String coverImage;
  private String price;
  private String description;
  private String category;
  private String calorie;
  private String grammage;
  private ArrayList<String> ingredients;
  private int _id;
  
  public Product(String category, String name, ArrayList<String> images, String price,
                 String description, String calorie, String grammage){
    this.name = name;
    this.category = category;
    this.images = images;
    this.price = price;
    this.description = description;
    this.calorie = calorie;
    this.grammage = grammage;
  }
  
  public Product(String category, String name, String image, String price,
                 String description, String calorie, String grammage){
    this.name = name;
    this.category = category;
    this.coverImage = image;
    this.price = price;
    this.description = description;
    this.calorie = calorie;
    this.grammage = grammage;
  }
  
  public Product(){
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getCoverImage() {
    return coverImage;
  }
  
  public void setCoverImage(String coverImage) {
    this.coverImage = coverImage;
  }
  
  public ArrayList<String> getImages() {
    return images;
  }
  
  public void setImages(ArrayList<String> images) {
    this.images = images;
  }
  
  public String getPrice() {
    return price;
  }
  
  public void setPrice(String price) {
    this.price = price;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getCategory() {
    return category;
  }
  
  public void setCategory(String category) {
    this.category = category;
  }
  
  public String getCalorie() {
    return calorie;
  }
  
  public void setCalorie(String calorie) {
    this.calorie = calorie;
  }
  
  public String getGrammage() {
    return grammage;
  }
  
  public void setGrammage(String grammage) {
    this.grammage = grammage;
  }
  
  public ArrayList<String> getIngredients() {
    return ingredients;
  }
  
  public void setIngredients(ArrayList<String> ingredients) {
    this.ingredients = ingredients;
  }
  
  public int get_id() {
    return _id;
  }
  
  public void set_id(int _id) {
    this._id = _id;
  }
}
