package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.ConnectActivity;

public class ConnectFragment extends Fragment {
  private static final String DEVICE_NAME = "deviceName";
  private String deviceName;
  
  
  public ConnectFragment() {
    // Required empty public constructor
  }
  
  public static ConnectFragment newInstance(String param1) {
    ConnectFragment fragment = new ConnectFragment();
    Bundle args = new Bundle();
    args.putString(DEVICE_NAME, param1);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      deviceName = getArguments().getString(DEVICE_NAME);
    }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_connect, container, false);
    TextView deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
    deviceNameTextView.setText(deviceName);
    onClickCancelButton(view);
    onClickConfirmButton(view);
    return view;
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
    ((ConnectActivity)getActivity()).interlayer.setVisibility(View.GONE);
  }
  
  private void onClickCancelButton(View view) {
    TextView cancelButton = view.findViewById(R.id.cancel);
    cancelButton.setOnClickListener(v -> getActivity().onBackPressed());
  }
  
  
  private void onClickConfirmButton(View view) {
    TextView confirmButton = view.findViewById(R.id.confirm);
    confirmButton.setOnClickListener(v -> {
  
    });
  
  }
}
