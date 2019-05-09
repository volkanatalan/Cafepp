package net.cafepp.cafepp.objects;

import net.cafepp.cafepp.enums.TableSituation;

import java.util.Date;

public class Table {
  private String name;
  private Date openingDate;
  private long price;
  private TableSituation situation = TableSituation.FREE;
  
  public Table(String name, Date openingDate) {
    this.name = name;
    this.openingDate = openingDate;
  }
  
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Date getOpeningDate() {
    return openingDate;
  }
  
  public void setOpeningDate(Date openingDate) {
    this.openingDate = openingDate;
  }
  
  public long getPrice() {
    return price;
  }
  
  public void setPrice(long price) {
    this.price = price;
  }
  
  public TableSituation getSituation() {
    return situation;
  }
  
  public void setSituation(TableSituation situation) {
    this.situation = situation;
  }
}
