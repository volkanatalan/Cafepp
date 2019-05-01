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
import net.cafepp.cafepp.activities.ConnectActivity;
import net.cafepp.cafepp.activities.DevicesActivity;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.objects.Device;

public class PairFragment extends Fragment {
  private final String TAG = "PairFragment";
  private static final String PACKAGE = "package";
  private Package aPackage;
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
      aPackage = (Package) getArguments().getSerializable(PACKAGE);
      
      if (aPackage != null) {
        Command command = aPackage.getCommand();
        Device myDevice = aPackage.getSendingDevice();
        targetDevice = aPackage.getReceivingDevice();
  
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
    TextView declineButton = view.findViewById(R.id.decline);
    
    deviceNameTextView.setText(targetDevice.getDeviceName());
    pairKeyTextView.setText(pairKey +"");
    declineButton.setText(declineButtonText);
    
    onClickDeclineButton(view);
    onClickConfirmButton(view);
    return view;
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
    if (getActivity() != null)
      if (getActivity() instanceof ConnectActivity)
        ((ConnectActivity)getActivity()).interlayer.setVisibility(View.GONE);
      else if (getActivity() instanceof DevicesActivity)
        ((DevicesActivity)getActivity()).interlayer.setVisibility(View.GONE);
  }
  
  private void onClickDeclineButton(View view) {
    Log.d(TAG, "Clicked on " + declineButtonText + " button.");
    TextView declineButton = view.findViewById(R.id.decline);
    declineButton.setOnClickListener(v -> {
      if (onButtonClickListener != null) onButtonClickListener.onClickDecline(aPackage);
    });
  }
  
  private void onClickConfirmButton(View view) {
    Log.d(TAG, "Clicked on Confirm button.");
    TextView confirmButton = view.findViewById(R.id.confirm);
    confirmButton.setOnClickListener(v -> {
      if (onButtonClickListener != null) onButtonClickListener.onClickPair(aPackage);
    });
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
    return aPackage;
  }
}
