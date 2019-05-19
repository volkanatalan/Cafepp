package net.cafepp.cafepp.objects;

import android.util.Log;

import net.cafepp.cafepp.enums.TableStatus;

import java.util.Date;

public class Table {
  private int id;
  private int number;
  private String location;
  private Date openingDate;
  private float price = 0;
  private TableStatus status = TableStatus.FREE;
  
  public Table() {
    // Empty constructor
  }
  
  public Table(int number, String location) {
    this.number = number;
    this.location = location;
  }
  
  public Table(int number, Date openingDate) {
    this.number = number;
    this.openingDate = openingDate;
  }
  
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public int getNumber() {
    return number;
  }
  
  public void setNumber(int number) {
    this.number = number;
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
  
  public TableStatus getStatus() {
    return status;
  }
  
  public void setStatus(TableStatus status) {
    this.status = status;
  }
  
  public void setSituation(String situation) {
    switch (situation) {
      case "FREE":
        this.status = TableStatus.FREE;
        break;
      case "OCCUPIED":
        this.status = TableStatus.OCCUPIED;
        break;
      case "RESERVED":
        this.status = TableStatus.RESERVED;
        break;
      default:
        Log.e("Table", "Unknown table status.");
    }
  }
}
