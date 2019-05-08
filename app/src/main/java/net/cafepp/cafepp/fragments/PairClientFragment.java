package net.cafepp.cafepp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.connection.ClientType;
import net.cafepp.cafepp.connection.Command;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.objects.Device;

public class PairClientFragment extends Fragment {
  private final String TAG = "PairClientFragment";
  private Context mContext;
  private LinearLayout clientTypeProgressBar, separatorV;
  private TextView pairButton, clientTypeTextView;
  private Package mPackage;
  private Device targetDevice;
  private int pairKey;
  
  
  public PairClientFragment() {
    // Required empty public constructor
  }
  
  public static PairClientFragment newInstance(Context context, Package aPackage, OnButtonClickListener listener) {
    PairClientFragment fragment = new PairClientFragment();
    fragment.setContext(context);
    if (listener != null) fragment.setOnButtonClickListener(listener);
    fragment.setPackage(aPackage);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
      
    if (mPackage != null) {
      Device myDevice = mPackage.getSendingDevice();
      targetDevice = mPackage.getReceivingDevice();
      pairKey = myDevice.getPairKey();
      
    }
  }
  
  @SuppressLint("SetTextI18n")
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    
    View view = inflater.inflate(R.layout.fragment_pair_client, container, false);
    TextView deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
    TextView pairKeyTextView = view.findViewById(R.id.pairKeyTextView);
    TextView declineButton = view.findViewById(R.id.decline);
    pairButton = view.findViewById(R.id.pair);
    separatorV = view.findViewById(R.id.separatorV);
    clientTypeProgressBar = view.findViewById(R.id.clientTypeProgressBar);
    clientTypeTextView = view.findViewById(R.id.clientTypeTextView);
    
    deviceNameTextView.setText(targetDevice.getDeviceName());
    pairKeyTextView.setText(pairKey +"");
  
    pairButton.setOnClickListener(v -> {
      Log.d(TAG, "Clicked on Confirm button.");
      if (onButtonClickListener != null) onButtonClickListener.onClickPair(mPackage);
    });
  
    declineButton.setOnClickListener(v -> {
      Log.d(TAG, "Clicked on Cancel button.");
      if (onButtonClickListener != null) onButtonClickListener.onClickCancel(mPackage);
    });
    return view;
  }
  
  private OnButtonClickListener onButtonClickListener;
  
  public interface OnButtonClickListener {
    void onClickPair(Package aPackage);
    void onClickCancel(Package aPackage);
  }
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
  
  public void setContext(Context context) {
    mContext = context;
  }
  
  public Package getPackage() {
    return mPackage;
  }
  
  public void setPackage(Package aPackage) {
    mPackage = aPackage;
  }
  
  public void setClientType(ClientType clientType) {
    String type = "";
    switch (clientType) {
      case COOK:
        type = mContext.getResources().getString(R.string.cook);
        break;
      case CASHIER:
        type = mContext.getResources().getString(R.string.cashier);
        break;
      case CUSTOMER:
        type = mContext.getResources().getString(R.string.customer);
        break;
      case MANAGER:
        type = mContext.getResources().getString(R.string.manager);
        break;
      case WAITER:
        type = mContext.getResources().getString(R.string.waiter);
        break;
    }
    mPackage.getSendingDevice().setClientType(clientType);
    clientTypeTextView.setText(type);
    clientTypeTextView.setVisibility(View.VISIBLE);
    pairButton.setVisibility(View.VISIBLE);
    separatorV.setVisibility(View.VISIBLE);
    clientTypeProgressBar.setVisibility(View.GONE);
  }
}
