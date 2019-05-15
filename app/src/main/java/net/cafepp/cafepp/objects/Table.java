package net.cafepp.cafepp.objects;

import android.util.Log;

import net.cafepp.cafepp.enums.TableSituation;

import java.util.Date;

public class Table {
  private int id;
  private String name;
  private String location;
  private Date openingDate;
  private float price = 0;
  private TableSituation situation = TableSituation.FREE;
  
  public Table() {
    // Empty constructor
  }
  
  public Table(String name, String location) {
    this.name = name;
    this.location = location;
  }
  
  public Table(String name, Date openingDate) {
    this.name = name;
    this.openingDate = openingDate;
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
  
  public String getLocation() {
    return location;
  }
  
  public void setLocation(String location) {
    this.location = location;
  }
  
  public Date getOpeningDate() {
    return openingDate;
  }
  
  public void setOpeningDate(Date openingDate) {
    this.openingDate = openingDate;
  }
  
  public float getPrice() {
    return price;
  }
  
  public void setPrice(float price) {
    this.price = price;
  }
  
  public TableSituation getSituation() {
    return situation;
  }
  
  public void setSituation(TableSituation situation) {
    this.situation = situation;
  }
  
  public void setSituation(String situation) {
    switch (situation) {
      case "FREE":
        this.situation = TableSituation.FREE;
        break;
      case "OCCUPIED":
        this.situation = TableSituation.OCCUPIED;
        break;
      case "RESERVED":
        this.situation = TableSituation.RESERVED;
        break;
      default:
        Log.e("Table", "Unknown table situation.");
    }
  }
}
