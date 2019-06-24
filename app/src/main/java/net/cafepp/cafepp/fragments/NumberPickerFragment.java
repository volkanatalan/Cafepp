package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import net.cafepp.cafepp.R;

public class NumberPickerFragment extends Fragment {
  
  private int mCurrentNumber;
  
  public NumberPickerFragment() {
    // Required empty public constructor
  }
  
  public static NumberPickerFragment newInstance(int currentNumber, OnButtonClickListener listener) {
    NumberPickerFragment fragment = new NumberPickerFragment();
    fragment.setCurrentNumber(currentNumber);
    fragment.setOnButtonClickListener(listener);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_number_picker, container, false);
    NumberPicker numberPicker = view.findViewById(R.id.numberPicker);
    TextView cancelTextView = view.findViewById(R.id.cancelTextView);
    TextView confirmTextView = view.findViewById(R.id.confirmTextView);
    
    numberPicker.setMinValue(0);
    numberPicker.setMaxValue(1000);
    numberPicker.setValue(mCurrentNumber);
    
    cancelTextView.setOnClickListener((v) ->{
      getFragmentManager().popBackStack();
      if (onButtonClickListener != null) onButtonClickListener.onClickCancel();
    });
    
    confirmTextView.setOnClickListener((v) -> {
      int number = numberPicker.getValue();
      getFragmentManager().popBackStack();
      if (onButtonClickListener != null) onButtonClickListener.onClickConfirm(number);
    });
    return view;
  }
  
  private OnButtonClickListener onButtonClickListener;
  
  public interface OnButtonClickListener {
    void onClickConfirm(int number);
    void onClickCancel();
  }
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
  
  public void setCurrentNumber(int currentNumber) {
    mCurrentNumber = currentNumber;
  }
}
