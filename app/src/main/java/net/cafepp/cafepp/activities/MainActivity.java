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
  
  public void onClickConnectButton(View view) {
    Intent intent = new Intent(this, ConnectActivity.class);
    startActivity(intent);
  }
  
  public void onClickAdminButton(View view) {
    Intent intent = new Intent(this, HomeActivity.class);
    startActivity(intent);
  }
}
