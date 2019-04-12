package net.cafepp.cafepp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.ConnectActivity;

public class ConnectSettingsDeviceNameFragment extends Fragment {
  
  private EditText deviceNameEditText;
  private TextView cancelTextView, confirmTextView;
  private String deviceName;
  
  public ConnectSettingsDeviceNameFragment() {
    // Required empty public constructor
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
        "ConnectSettings", Context.MODE_PRIVATE);
    deviceName = sharedPreferences.getString("deviceName", "Cafepp Device");
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_connect_settings_device_name, container, false);
    deviceNameEditText = view.findViewById(R.id.deviceNameTextView);
    cancelTextView = view.findViewById(R.id.cancelTextView);
    confirmTextView = view.findViewById(R.id.confirmTextView);
    return view;
  }
  
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    
    deviceNameEditText.setText(deviceName);
    
    confirmTextView.setOnClickListener(v -> {
      deviceName = deviceNameEditText.getText().toString();
      SharedPreferences.Editor editor = getActivity().getSharedPreferences(
          "ConnectSettings", Context.MODE_PRIVATE).edit();
      editor.putString("deviceName", deviceName);
      editor.apply();
      
      ((ConnectActivity) getActivity()).deviceNameTextView.setText(deviceName);
      ((ConnectActivity) getActivity()).interlayer.setVisibility(View.GONE);
      getActivity().onBackPressed();
    });
    
    cancelTextView.setOnClickListener(v -> {
      ((ConnectActivity) getActivity()).interlayer.setVisibility(View.GONE);
      getActivity().onBackPressed();
    });
  }
}
