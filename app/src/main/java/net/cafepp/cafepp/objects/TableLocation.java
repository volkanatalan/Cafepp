package net.cafepp.cafepp.objects;

public class TableLocation {
  int id;
  private String name;
  private int totalTable = 0;
  
  public TableLocation() {
    // Empty constructor
  }
  
  public TableLocation(String name) {
    this.name = name;
  }
  
  public TableLocation(String name, int totalTable) {
    this.name = name;
    this.totalTable = totalTable;
  }
  
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getTotalTable() {
    return totalTable;
  }
  
  public void setTotalTable(int totalTable) {
    this.totalTable = totalTable;
  }
}
