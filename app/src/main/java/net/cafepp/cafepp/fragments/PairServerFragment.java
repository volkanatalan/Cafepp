package net.cafepp.cafepp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.connection.Package;
import net.cafepp.cafepp.objects.Device;

public class PairServerFragment extends Fragment {
  
  private Context mContext;
  private final String TAG = "PairServerFragment";
  private Package mPackage;
  private Device targetDevice;
  private int pairKey;
  
  public PairServerFragment() {
    // Required empty public constructor
  }
  
  public static PairServerFragment newInstance(Context context, Package aPackage, OnButtonClickListener listener) {
    PairServerFragment fragment = new PairServerFragment();
    fragment.setContext(context);
    fragment.setPackage(aPackage);
    fragment.setOnButtonClickListener(listener);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (mPackage != null) {
      targetDevice = mPackage.getReceivingDevice();
      pairKey = targetDevice.getPairKey();
    }
  }
  
  private AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      String selectedClientType = parent.getItemAtPosition(position).toString();
      mPackage.getReceivingDevice().setClientType(selectedClientType);
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    
    }
  };
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pair_server, container, false);
    TextView deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
    TextView pairKeyTextView = view.findViewById(R.id.pairKeyTextView);
    TextView confirmButton = view.findViewById(R.id.pair);
    TextView declineButton = view.findViewById(R.id.decline);
    Spinner spinner = view.findViewById(R.id.clientTypeSpinner);
  
    deviceNameTextView.setText(targetDevice.getDeviceName());
    pairKeyTextView.setText(pairKey +"");
  
    spinner.setOnItemSelectedListener(selectedListener);
  
    ArrayAdapter dataAdapter =
        ArrayAdapter.createFromResource(mContext, R.array.clientTypes, R.layout.list_item_client_type_spinner);
    dataAdapter.setDropDownViewResource(R.layout.list_item_client_type_spinner);
    spinner.setAdapter(dataAdapter);
  
    confirmButton.setOnClickListener(v -> {
      Log.d(TAG, "Clicked on Confirm button.");
      if (onButtonClickListener != null) onButtonClickListener.onClickPair(mPackage);
    });
  
    declineButton.setOnClickListener(v -> {
      Log.d(TAG, "Clicked on Decline button.");
      if (onButtonClickListener != null) onButtonClickListener.onClickDecline(mPackage);
    });
    
    return view;
  }
  
  public void setContext(Context context) {
    mContext = context;
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
  
  public void setPackage(Package aPackage) {
    mPackage = aPackage;
  }
}
