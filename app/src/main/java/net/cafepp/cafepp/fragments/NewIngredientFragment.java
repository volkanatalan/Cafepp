package net.cafepp.cafepp.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.objects.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class NewIngredientFragment extends Fragment implements AdapterView.OnItemSelectedListener {
  View view;
  EditText ingredientNameET;
  EditText ingredientAmountInStockET;
  String ingredientName;
  String ingredientAmountInStock;
  RelativeLayout unitsOfMeasurmentsRL;
  Button addButton;
  String spinnerChosenItem;
  EditText calorieET;
  String calorieAmount;
  ProductDatabase productDatabase;
  ArrayList<String > items;
  ArrayList<String > items2;
  String searchBoxContent;
  Bundle bundle5;
  LinearLayout weightLL;
  TextView gramTextView;
  EditText gramEditText;
  Ingredient ingredient;
  
  
  public NewIngredientFragment() {
    // Required empty public constructor
  }
  
  public static NewIngredientFragment newInstance() {
    return new NewIngredientFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    productDatabase = new ProductDatabase(getContext());
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_new_ingredient, container, false);
    view.setOnTouchListener(new View.OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
    return view;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    bundle5 = getArguments();
    if (bundle5 != null) {
      items = bundle5.getStringArrayList("listItems");
      searchBoxContent = bundle5.getString("search_box_content");
    }
    items2 = new ArrayList<>();
    ingredientNameET = view.findViewById(R.id.ingredientName);
    ingredientAmountInStockET = view.findViewById(R.id.ingredientAmountInStock);
    unitsOfMeasurmentsRL = view.findViewById(R.id.unitsOfMeasurmentsRL);
    calorieET = view.findViewById(R.id.calorieEditText);
    weightLL = view.findViewById(R.id.weightLL);
    gramTextView = view.findViewById(R.id.gramTextView);
    gramEditText = view.findViewById(R.id.gramEditText);
    addButton = view.findViewById(R.id.addButton);
    
    if (bundle5 != null) {
      ingredientNameET.setText(searchBoxContent);
    }
    
    ingredientNameET.requestFocus();
    ingredientNameET.setSelection(ingredientNameET.getText().toString().length());
    
    Spinner spinner = view.findViewById(R.id.spinner);
    spinner.setOnItemSelectedListener(this);
    
    List<String> spinnerList = new ArrayList<>();
    spinnerList.add("kilogram");
    spinnerList.add("liter");
    spinnerList.add("piece");
    
    ArrayAdapter<String> dataAdapter =
        new ArrayAdapter<>(getActivity(), R.layout.unit_type_simple_spinner_item, spinnerList);
    dataAdapter.setDropDownViewResource(R.layout.unit_type_spinner_item);
    spinner.setAdapter(dataAdapter);
    
    
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ingredientName = ingredientNameET.getText().toString();
        
        if (ingredientName.equals("") || ingredientName==null)
          Toast.makeText(getContext(), "Please write an ingredient name.",
              Toast.LENGTH_LONG).show();
        
        else if (productDatabase.hasIngredient(ingredientName))
          Toast.makeText(getContext(), "There is already an ingredient with this name.",
              Toast.LENGTH_LONG).show();
        
        else {
          ingredientAmountInStock = ingredientAmountInStockET.getText().toString().equals("")
                                        ? "0" : ingredientAmountInStockET.getText().toString();
          calorieAmount = calorieET.getText().toString().equals("")
                              ? "0" : calorieET.getText().toString();
          String weightOfUnit = spinnerChosenItem.equals("kilogram") ? "1000" : gramEditText.getText().toString();
  
          ingredient = new Ingredient(
              ingredientName, ingredientAmountInStock, spinnerChosenItem, calorieAmount, weightOfUnit);
          productDatabase.addChosenIngredient(ingredient);
          
          SharedPreferences sharedPreferences =
              getActivity().getSharedPreferences("net.cafepp.cafepp", Context.MODE_PRIVATE);
          sharedPreferences.edit().putString("newItem", ingredientName).apply();
          
          ingredientNameET.setText("");
          ingredientAmountInStockET.setText("");
          calorieET.setText("");
          gramEditText.setText("");
          
          // Hide keyboard
          View view = getActivity().getCurrentFocus();
          if (view != null) {
            InputMethodManager inputMethodManager =
                (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager!=null)
              inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
          }
          
          // Press back button to exit
          getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
          getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        }
      }
    });
  }
  
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    // On selecting a spinner item
    spinnerChosenItem = parent.getItemAtPosition(position).toString();
    switch (spinnerChosenItem) {
      case "kilogram":
        weightLL.setVisibility(View.GONE);
        break;
      case "liter":
        weightLL.setVisibility(View.VISIBLE);
        gramTextView.setText("gr / 100 ml");
        break;
      case "piece":
        weightLL.setVisibility(View.VISIBLE);
        gramTextView.setText("gr / 1 piece");
        break;
    }
  }
  
  @Override
  public void onNothingSelected(AdapterView<?> parent) {
  
  }

  /*
  @Override
  public void onAttach(Context context) {
      super.onAttach(context);
      if (context instanceof OnFragmentInteractionListener) {
          mListener = (OnFragmentInteractionListener) context;
      } else {
          throw new RuntimeException(context.toString()
                  + " must implement OnFragmentInteractionListener");
      }
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }*/
  
}
