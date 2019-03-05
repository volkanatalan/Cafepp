package net.cafepp.cafepp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.cafepp.cafepp.R;

public class MainActivity extends AppCompatActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
  
  public void intentMenuActivity(View view){
    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
    startActivity(intent);
  }
  
  public void intentAddProductActivity(View view) {
    Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
    startActivity(intent);
  }
}
