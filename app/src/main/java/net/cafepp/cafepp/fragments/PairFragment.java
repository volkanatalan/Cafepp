package net.cafepp.cafepp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.objects.Device;

public class PairFragment extends Fragment {
  private final String TAG = "PairFragment";
  private static final String PACKAGE = "package";
  private Package mPackage;
  private Device targetDevice;
  private int pairKey;
  private String declineButtonText = "";
  
  
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
      mPackage = (Package) getArguments().getSerializable(PACKAGE);
      
      if (mPackage != null) {
        Command command = mPackage.getCommand();
        Device myDevice = mPackage.getSendingDevice();
        targetDevice = mPackage.getReceivingDevice();
  
        switch (command) {
          case PAIR_REQ:
            pairKey = myDevice.getPairKey();
            if (getActivity() != null)
              declineButtonText = getActivity().getResources().getString(R.string.cancel);
            break;
            
          case PAIR_ANSWER:
            pairKey = targetDevice.getPairKey();
            if (getActivity() != null)
              declineButtonText = getActivity().getResources().getString(R.string.decline);
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
    TextView confirmButton = view.findViewById(R.id.confirm);
    TextView declineButton = view.findViewById(R.id.decline);
    
    deviceNameTextView.setText(targetDevice.getDeviceName());
    pairKeyTextView.setText(pairKey +"");
    declineButton.setText(declineButtonText);
    
    confirmButton.setOnClickListener(v -> {
      Log.d(TAG, "Clicked on Confirm button.");
      if (onButtonClickListener != null) onButtonClickListener.onClickPair(mPackage);
    });
  
    declineButton.setOnClickListener(v -> {
      Log.d(TAG, "Clicked on " + declineButtonText + " button.");
      if (onButtonClickListener != null) onButtonClickListener.onClickDecline(mPackage);
    });
    return view;
  }
  
  private OnButtonClickListener onButtonClickListener;
  
  public interface OnButtonClickListener {
    void onClickPair(Package aPackage);
    void onClickDecline(Package aPackage);
  }
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
  
  public Package getPackage() {
    return mPackage;
  }
}
