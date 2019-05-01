package net.cafepp.cafepp.fragments;

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

public class ChangeDeviceNameFragment extends Fragment {
  
  private EditText deviceNameEditText;
  private TextView cancelTextView, confirmTextView;
  private String deviceName;
  
  public ChangeDeviceNameFragment() {
    // Required empty public constructor
  }
  
  public static ChangeDeviceNameFragment newInstance(String deviceName, OnButtonClickListener listener) {
    ChangeDeviceNameFragment fragment = new ChangeDeviceNameFragment();
    fragment.setDeviceName(deviceName);
    fragment.setOnButtonClickListener(listener);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
      if (onButtonClickListener != null) onButtonClickListener.onClickConfirm(deviceName);
    });
    
    cancelTextView.setOnClickListener(v -> {
      if (onButtonClickListener != null) onButtonClickListener.onClickCancel();
    });
  }
  
  public void setDeviceName(String name) {
    deviceName = name;
  }
  
  private OnButtonClickListener onButtonClickListener;
  
  public interface OnButtonClickListener {
    void onClickCancel();
    void onClickConfirm(String deviceName);
  }
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
}
