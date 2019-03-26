package net.cafepp.cafepp.models;

public class Ingredient {
  private int _id;
  private String ingredient;
  private String name;
  private String amount;
  private String amountType;
  private String calorie;
  private String unitWeight;
  private boolean show = true;
  
  public Ingredient(String name, String amount, String amountType,
                    String calorie, String unitWeight){
    this.name = name;
    this.amount = amount;
    this.amountType = amountType;
    this.calorie = calorie;
    this.unitWeight = unitWeight;
  }
  
  public Ingredient (String ingredient, String name, String amount, String amountType, boolean show){
    this.ingredient = ingredient;
    this.name = name;
    this.amount = amount;
    this.amountType = amountType;
    this.show = show;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getAmount() {
    return amount;
  }
  
  public void setAmount(String amount) {
    this.amount = amount;
  }
  
  public String getAmountType() {
    return amountType;
  }
  
  public void setAmountType(String amountType) {
    this.amountType = amountType;
  }
  
  public int get_id() {
    return _id;
  }
  
  public void set_id(int _id) {
    this._id = _id;
  }
  
  public String getCalorie() {
    return calorie;
  }
  
  public void setCalorie(String calorie) {
    this.calorie = calorie;
  }
  
  public String getUnitWeight() {
    return unitWeight;
  }
  
  public void setUnitWeight(String unitWeight) {
    this.unitWeight = unitWeight;
  }
  
  public String getIngredient() {
    return ingredient;
  }
  
  public void setIngredient(String ingredient) {
    this.ingredient = ingredient;
  }
  
  public boolean isShow() {
    return show;
  }
  
  public void setShow(boolean show) {
    this.show = show;
  }
}
