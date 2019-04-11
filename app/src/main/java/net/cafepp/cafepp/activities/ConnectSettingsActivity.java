package net.cafepp.cafepp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.fragments.ConnectSettingsDeviceNameFragment;

public class ConnectSettingsActivity extends AppCompatActivity {
  
  public FrameLayout interlayer;
  public TextView deviceName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect_settings);
    interlayer = findViewById(R.id.interlayer);
    deviceName = findViewById(R.id.deviceNameTextView);
  }
  
  @Override
  protected void onResume() {
    super.onResume();
  
    SharedPreferences sharedPreferences = getSharedPreferences("ConnectSettings", Context.MODE_PRIVATE);
    deviceName.setText(sharedPreferences.getString("deviceName", "Cafepp Device"));
  }
  
  public void onClickDeviceName(View view) {
    interlayer.setVisibility(View.VISIBLE);
    
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
        R.anim.enter_from_right, R.anim.exit_to_left);
    ft.replace(R.id.fragmentContainer, new ConnectSettingsDeviceNameFragment());
    ft.addToBackStack("connectSettings");
    ft.commit();
  }
}
