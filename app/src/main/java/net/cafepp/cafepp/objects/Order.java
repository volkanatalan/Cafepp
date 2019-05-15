package net.cafepp.cafepp.objects;

public class Order extends Product {
  private float quantity;
  
  public Order(Product product) {
    setProduct(product);
  }
  
  public Product getProduct() {
    Product product = new Product();
    product.setName(getName());
    product.setImages(getImages());
    product.setPrice(getPrice());
    product.setDescription(getDescription());
    product.setCategory(getCategory());
    product.setCalorie(getCalorie());
    product.setGrammage(getGrammage());
    product.setIngredients(getIngredients());
    product.set_id(get_id());
    return product;
  }
  
  public void setProduct(Product product) {
    setName(product.getName());
    setImages(product.getImages());
    setPrice(product.getPrice());
    setDescription(product.getDescription());
    setCategory(product.getCategory());
    setCalorie(product.getCalorie());
    setGrammage(product.getGrammage());
    setIngredients(product.getIngredients());
    set_id(product.get_id());
  }
  
  public float getQuantity() {
    return quantity;
  }
  
  public void setQuantity(float quantity) {
    this.quantity = quantity;
  }
}
