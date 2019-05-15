package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.TableLocation;

public class AddTableLocationFragment extends Fragment {
  
  public AddTableLocationFragment() {
    // Required empty public constructor
  }
  
  public static AddTableLocationFragment newInstance(OnButtonClickListener listener) {
    AddTableLocationFragment fragment = new AddTableLocationFragment();
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
    View view = inflater.inflate(R.layout.fragment_add_table_location, container, false);
    EditText nameEditText = view.findViewById(R.id.locationNameEditText);
    EditText tableNumberEditText = view.findViewById(R.id.tableNumberEditText);
    TextView confirmTextView = view.findViewById(R.id.confirmTextView);
    TextView cancelTextView = view.findViewById(R.id.cancelTextView);
    
    confirmTextView.setOnClickListener((v) ->{
      String name = nameEditText.getText().toString();
      String tableNumberStr = tableNumberEditText.getText().toString();
      int tableNumber = tableNumberStr.equals("") ? 0 : Integer.parseInt(tableNumberStr);
      
      if (name.equals("")) {
        Toast.makeText(container.getContext(), "Location name cannot be empty.", Toast.LENGTH_LONG).show();
      }
      
      else if (onButtonClickListener != null) {
        onButtonClickListener.onClickConfirm(new TableLocation(name, tableNumber));
      }
    });
    
    
    cancelTextView.setOnClickListener((v) -> {
      if (onButtonClickListener != null) onButtonClickListener.onClickCancel();
    });
    
    return view;
  }
  
  private OnButtonClickListener onButtonClickListener;
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
  
  public interface OnButtonClickListener {
    void onClickConfirm(TableLocation location);
    void onClickCancel();
  }
}
