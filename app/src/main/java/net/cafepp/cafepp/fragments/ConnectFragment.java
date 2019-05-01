package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.Device;

public class ConnectFragment extends Fragment {
  private Device mDevice;
  private OnButtonClickListener onButtonClickListener;
  private boolean isFound;
  private boolean isConnected;
  private String contentText;
  
  public ConnectFragment() {
    // Required empty public constructor
  }
  
  public static ConnectFragment newInstance(Device device, OnButtonClickListener listener) {
    ConnectFragment fragment = new ConnectFragment();
    fragment.setDevice(device);
    fragment.setOnButtonClickListener(listener);
    return fragment;
  }
  
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
    if (mDevice != null) {
      String deviceName = mDevice.getDeviceName() == null ? "" : mDevice.getDeviceName();
      String macAddress = mDevice.getMacAddress() == null ? "" : mDevice.getMacAddress();
      String ipAddress = mDevice.getIpAddress() == null ? "" : mDevice.getIpAddress();
    
      contentText = deviceName + "\n" + macAddress + "\n" + ipAddress;
      
      isFound = mDevice.isFound();
      isConnected = mDevice.isConnected();
    }
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState){
    View view = inflater.inflate(R.layout.fragment_connect, container, false);
    TextView contentTextView = view.findViewById(R.id.contentTextView);
    TextView unpairButton = view.findViewById(R.id.unpair);
    TextView cancelButton = view.findViewById(R.id.cancel);
    TextView connectButton = view.findViewById(R.id.connect);
    LinearLayout separator2 = view.findViewById(R.id.separator2);
  
    if (getActivity() != null) {
      if (!isFound) {
        unpairButton.setVisibility(View.GONE);
        separator2.setVisibility(View.GONE);
        connectButton.setText(getActivity().getResources().getText(R.string.unpair));
      }
      
      else {
        String connectButtonText = isConnected ?
                                       getActivity().getResources().getString(R.string.disconnect) :
                                       getActivity().getResources().getString(R.string.connect);
        connectButton.setText(connectButtonText);
      }
    }
  
    contentTextView.setText(contentText);
    
    unpairButton.setOnClickListener(v -> {
      if (onButtonClickListener != null) onButtonClickListener.onClickUnpair(mDevice);
    });
    
    cancelButton.setOnClickListener(v -> {
      if (onButtonClickListener != null) onButtonClickListener.onClickCancel();
    });
    
    connectButton.setOnClickListener(v -> {
      if (onButtonClickListener != null) {
        if (!isFound)onButtonClickListener.onClickUnpair(mDevice);
        else if (isConnected)onButtonClickListener.onClickDisconnect(mDevice);
        else onButtonClickListener.onClickConnect(mDevice);
      }
    });
    
    return view;
  }
  
  public void setDevice(Device device) {
    mDevice = device;
  }
  
  public interface OnButtonClickListener {
    void onClickConnect(Device device);
    void onClickDisconnect(Device device);
    void onClickCancel();
    void onClickUnpair(Device device);
  }
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
}
