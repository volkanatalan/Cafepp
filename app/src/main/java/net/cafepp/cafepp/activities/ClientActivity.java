package net.cafepp.cafepp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.Device;

public class ClientActivity extends AppCompatActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client);
    TextView tv = findViewById(R.id.textView);
  
    Device connectedDevice = (Device) getIntent().getSerializableExtra("connectedDevice");
    
    String st = connectedDevice.getDeviceName() + "\n" +
                    connectedDevice.getMacAddress() + "\n" +
                    connectedDevice.getClientType();
    
    tv.setText(st);
  }
}
