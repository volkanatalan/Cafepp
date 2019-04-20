package net.cafepp.cafepp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.ConnectActivity;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.objects.Device;

public class PairFragment extends Fragment {
  private static final String PACKAGE = "package";
  private Package aPackage;
  private Device targetDevice;
  private int pairKey;
  private Command command;
  
  
  public PairFragment() {
    // Required empty public constructor
  }
  
  public static PairFragment newInstance(Package aPackage, OnButtonClickListener listener) {
    PairFragment fragment = new PairFragment();
    if (listener != null) fragment.setOnButtonClickListener(listener);
    Bundle args = new Bundle();
    args.putSerializable(PACKAGE, aPackage);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      aPackage = (Package) getArguments().getSerializable(PACKAGE);
      
      if (aPackage != null) {
        command = aPackage.getCommand();
        Device myDevice = aPackage.getSendingDevice();
        targetDevice = aPackage.getTargetDevice();
  
        switch (command) {
          case PAIR_REQ:
            pairKey = myDevice.getPairKey();
            break;
            
          case PAIR_ANSWER:
            pairKey = targetDevice.getPairKey();
            break;
        }
      }
    }
  }
  
  @SuppressLint("SetTextI18n")
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    
    View view = inflater.inflate(R.layout.fragment_pair, container, false);
    TextView deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
    TextView pairKeyTextView = view.findViewById(R.id.pairKeyTextView);
    
    deviceNameTextView.setText(targetDevice.getDeviceName());
    pairKeyTextView.setText(pairKey +"");
    
    onClickCancelButton(view);
    onClickConfirmButton(view);
    return view;
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
    if (getActivity() != null)
      ((ConnectActivity)getActivity()).interlayer.setVisibility(View.GONE);
  }
  
  private void onClickCancelButton(View view) {
    TextView cancelButton = view.findViewById(R.id.cancel);
    cancelButton.setOnClickListener(v -> {
      
      if (onButtonClickListener != null) onButtonClickListener.onClickCancel(aPackage);
      if (getActivity() != null) getActivity().onBackPressed();
    });
  }
  
  private void onClickConfirmButton(View view) {
    TextView confirmButton = view.findViewById(R.id.confirm);
    confirmButton.setOnClickListener(v -> {
  
      if (onButtonClickListener != null) onButtonClickListener.onClickPair(aPackage);
      if (getActivity() != null) getActivity().onBackPressed();
      
    });
  }
  
  private OnButtonClickListener onButtonClickListener;
  
  public interface OnButtonClickListener {
    void onClickPair(Package aPackage);
    void onClickCancel(Package aPackage);
  }
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
  
}
