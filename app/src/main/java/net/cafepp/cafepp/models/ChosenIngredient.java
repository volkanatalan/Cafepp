package net.cafepp.cafepp.models;

public class ChosenIngredient {
  private String name;
  private String ingredientType;
  private Float amount = -1f;
  private String amountType;
  private Float calorieAmount = -1f;
  private Float weightOfUnit = -1f;
  private boolean show = true;
  private boolean isCalculateCalorie = true;
  
  public ChosenIngredient(String name, String ingredientType, Float amount, String amountType,
                          Float calorieAmount, Float weightOfUnit, boolean show) {
    this.name = name;
    this.ingredientType = ingredientType;
    this.amount = amount;
    this.amountType = amountType;
    this.calorieAmount = calorieAmount;
    this.weightOfUnit = weightOfUnit;
    this.show = show;
  }
  
  ChosenIngredient(String name, String ingredientType, Float amount, String amountType,
                   Float calorieAmount, Float weightOfUnit, boolean show, boolean isCalculateCalorie) {
    this.name = name;
    this.ingredientType = ingredientType;
    this.amount = amount;
    this.amountType = amountType;
    this.calorieAmount = calorieAmount;
    this.weightOfUnit = weightOfUnit;
    this.show = show;
    this.isCalculateCalorie = isCalculateCalorie;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getAmountType() {
    return amountType;
  }
  
  public void setAmountType(String amountType) {
    this.amountType = amountType;
  }
  
  public boolean isShow() {
    return show;
  }
  
  public void setShow(boolean show) {
    this.show = show;
  }
  
  public boolean isCalculateCalorie() {
    return isCalculateCalorie;
  }
  
  public void setCalculateCalorie(boolean calculateCalorie) {
    isCalculateCalorie = calculateCalorie;
  }
  
  public String getIngredientType() {
    return ingredientType;
  }
  
  public void setIngredientType(String ingredientType) {
    this.ingredientType = ingredientType;
  }
  
  public Float getAmount() {
    return amount;
  }
  
  public void setAmount(Float amount) {
    this.amount = amount;
  }
  
  public Float getCalorieAmount() {
    return calorieAmount;
  }
  
  public void setCalorieAmount(Float calorieAmount) {
    this.calorieAmount = calorieAmount;
  }
  
  public Float getWeightOfUnit() {
    return weightOfUnit;
  }
  
  public void setWeightOfUnit(Float weightOfUnit) {
    this.weightOfUnit = weightOfUnit;
  }
}
