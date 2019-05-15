package net.cafepp.cafepp.objects;

public class TableLocation {
  private String name;
  private int number = 0;
  
  public TableLocation() {
    // Empty constructor
  }
  
  public TableLocation(String name) {
    this.name = name;
  }
  
  public TableLocation(String name, int number) {
    this.name = name;
    this.number = number;
  }
  
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getNumber() {
    return number;
  }
  
  public void setNumber(int number) {
    this.number = number;
  }
}
