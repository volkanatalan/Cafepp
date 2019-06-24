package net.cafepp.cafepp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.TableLocation;

public class AddTableLocationFragment extends Fragment {
  
  private TableLocation mTableLocation;
  
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
    setHasOptionsMenu(true);
  }
  
  @SuppressLint("SetTextI18n")
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_table_location, container, false);
    EditText nameEditText = view.findViewById(R.id.locationNameEditText);
    EditText tableNumberEditText = view.findViewById(R.id.tableNumberEditText);
    TextView headerTextView = view.findViewById(R.id.headerTextView);
    TextView confirmTextView = view.findViewById(R.id.confirmTextView);
    TextView cancelTextView = view.findViewById(R.id.cancelTextView);
    TextView removeTextView = view.findViewById(R.id.removeTextView);
  
    if (mTableLocation != null) {
      nameEditText.setText(mTableLocation.getName());
      tableNumberEditText.setText(mTableLocation.getTotalTable() + "");
    }
    
    else {
      headerTextView.setText(getString(R.string.add_a_location));
      removeTextView.setVisibility(View.GONE);
      cancelTextView.setBackgroundResource(R.drawable.rounded_bottom_left_white);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)cancelTextView.getLayoutParams();
      params.setMarginStart(0);
      cancelTextView.setLayoutParams(params);
    }
    
    confirmTextView.setOnClickListener((v) ->{
      hideKeyboard();
      
      String name = nameEditText.getText().toString();
      String tableNumberStr = tableNumberEditText.getText().toString();
      int tableNumber = tableNumberStr.equals("") ? 0 : Integer.parseInt(tableNumberStr);
      
      if (name.equals("")) {
        Toast.makeText(container.getContext(), "Location name cannot be empty.", Toast.LENGTH_LONG).show();
      }
      
      else if (onButtonClickListener != null) {
        
        TableLocation tableLocation = new TableLocation(name, tableNumber);
        if (mTableLocation != null)
          tableLocation.setId(mTableLocation.getId());
        
        onButtonClickListener.onClickConfirm(tableLocation);
      }
    });
    
    
    cancelTextView.setOnClickListener((v) -> {
      hideKeyboard();
      if (onButtonClickListener != null) onButtonClickListener.onClickCancel();
    });
  
  
    removeTextView.setOnClickListener((v) -> {
      hideKeyboard();
      if (onButtonClickListener != null) onButtonClickListener.onClickRemove(mTableLocation);
    });
    
    return view;
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    super.onCreateOptionsMenu(menu, inflater);
  }
  
  public void hideKeyboard() {
    Activity activity = getActivity();
    if (activity != null) {
      InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
      //Find the currently focused view, so we can grab the correct window token from it.
      View view = activity.getCurrentFocus();
      //If no view currently has focus, create a new one, just so we can grab a window token from it
      if (view == null) {
        view = new View(activity);
      }
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }
  
  private OnButtonClickListener onButtonClickListener;
  
  public void setOnButtonClickListener(OnButtonClickListener listener) {
    onButtonClickListener = listener;
  }
  
  public interface OnButtonClickListener {
    void onClickConfirm(TableLocation location);
    void onClickCancel();
    void onClickRemove(TableLocation location);
  }
  
  public void setTableLocation(TableLocation location) {
    mTableLocation = location;
  }
}
