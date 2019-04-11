package net.cafepp.cafepp.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.AddProductActivity;
import net.cafepp.cafepp.custom_views.ThreeStateCheckBox;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.objects.ChosenIngredient;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalculateCalorieFragment extends Fragment {
  ArrayList<ChosenIngredient> chosenIngredientList;
  Button calculateButton;
  Button doneButton;
  Button make100grButton;
  ThreeStateCheckBox includeAllToCalculationCB;
  ProductDatabase productDatabase;
  RecyclerView recyclerView;
  RecyclerViewAdapter recyclerViewAdapter;
  TextView calorieResult;
  TextView kgResult;
  boolean includeAll = true;
  
  
  public CalculateCalorieFragment() {
    // Required empty public constructor
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    productDatabase = new ProductDatabase(getContext());
    chosenIngredientList = productDatabase.getChosenIngredients();
  }
  
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_calculate_calorie, container, false);
    recyclerView = view.findViewById(R.id.calorieRecyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    includeAllToCalculationCB = view.findViewById(R.id.includeAllToCalculationCB);
    calorieResult = view.findViewById(R.id.calorieResultTV);
    kgResult = view.findViewById(R.id.kgResult);
    calculateButton = view.findViewById(R.id.calculateButton);
    make100grButton = view.findViewById(R.id.make100gr);
    doneButton = view.findViewById(R.id.doneButton);
    recyclerViewAdapter = new RecyclerViewAdapter(chosenIngredientList);
    recyclerView.setAdapter(recyclerViewAdapter);
  
    includeAllToCalculationCB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getActivity().getCurrentFocus().clearFocus();
        int state = ((ThreeStateCheckBox) v).getState();
        switch (state) {
          case 1:
            includeAll = true;
            break;
        
          case 0:
            includeAll = false;
            break;
        }
        recyclerViewAdapter.notifyDataSetChanged();
      }
    });
  
    calculateButton.setOnClickListener(new View.OnClickListener() {
      @SuppressLint("SetTextI18n")
      @Override
      public void onClick(View v) {
        clearFocus();
      
        boolean calculable = true;
        for (int i = 0; i<chosenIngredientList.size(); i++) {
          Float amount = chosenIngredientList.get(i).getAmount();
          if (chosenIngredientList.get(i).isCalculateCalorie() && (
              amount == null || amount < 0))
            calculable = false;
        }
      
        // Calculate calorie if calculable
        if (calculable) {
          Float calorieSum = 0f;
          Float grSum = 0f;
          for (int i=0; i<chosenIngredientList.size(); i++) {
            ChosenIngredient chosenIngredient = chosenIngredientList.get(i);
            if (chosenIngredient.isCalculateCalorie()) {
              Float addedAmount = chosenIngredient.getAmount();
              Float calorieOfHundred = chosenIngredient.getCalorieAmount();
              Float gramOfUnit = chosenIngredient.getWeightOfUnit();
              String amountType = chosenIngredient.getAmountType();
              switch (amountType) {
                case "kilogram":
                  calorieSum += calorieOfHundred / 100 * addedAmount;
                  grSum += addedAmount;
                  break;
                case "piece":
                  calorieSum += ((gramOfUnit / 100) * calorieOfHundred) * addedAmount;
                  grSum += addedAmount * gramOfUnit;
                  break;
                case "liter":
                  calorieSum += calorieOfHundred * gramOfUnit * addedAmount / 10000;
                  grSum += gramOfUnit / 100 * addedAmount;
                  break;
              }
            }
          }
        
          calorieResult.setText(""+Math.round(calorieSum));
          kgResult.setText(""+Math.round(grSum));
        
          make100grButton.setVisibility(View.VISIBLE);
        } else {
          Toast.makeText(getContext(), "Please fill in the all necessary fields.",
              Toast.LENGTH_LONG).show();
        }
      }
    });
  
    make100grButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!calorieResult.getText().toString().equals("")) {
          float calorie = Float.valueOf(calorieResult.getText().toString());
          float gram = Float.valueOf(kgResult.getText().toString());
          calorie = calorie / gram * 100;
          gram = 100;
        
          calorieResult.setText(""+ Math.round(calorie));
          kgResult.setText(""+ Math.round(gram));
        }
      
      }
    });
  
    doneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clearFocus();
      
        // Adjustments
        if (!calorieResult.getText().toString().equals(""))
          ((AddProductActivity) getActivity()).calorie.setText(
              calorieResult.getText().toString().equals("?") ? "" : calorieResult.getText().toString());
        ((AddProductActivity) getActivity()).kgSumET.setText(
            kgResult.getText().toString().equals("?") ? "" : kgResult.getText().toString());
        ((AddProductActivity) getActivity()).chosenIngredientsAdapter.notifyDataSetChanged();
      
        // Press back button
        getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
      }
    });
    return view;
  }
  
  void clearFocus() {
    // Hide keyboard
    View view = getActivity().getWindow().getCurrentFocus();
    if (view != null) {
      InputMethodManager inputMethodManager =
          (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      if (inputMethodManager != null) {
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    }
    
    // Clear focus
    getActivity().getWindow().getCurrentFocus().clearFocus();
  }
  
  
  public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageViewHolder> {
    ArrayList<ChosenIngredient> chosenIngredients;
    
    class ImageViewHolder extends RecyclerView.ViewHolder {
      CardView cardView;
      CheckBox checkBox;
      TextView ingredientName;
      TextView calorieTV;
      EditText amountET;
      TextView typeTV;
      
      ImageViewHolder(View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.calorieCalculateCardView);
        checkBox = itemView.findViewById(R.id.checkedTextView);
        ingredientName = itemView.findViewById(R.id.ingredientName);
        calorieTV = itemView.findViewById(R.id.calorieOfHundredTV);
        amountET = itemView.findViewById(R.id.kgET);
        typeTV = itemView.findViewById(R.id.typeTV);
      }
    }
    
    RecyclerViewAdapter(ArrayList<ChosenIngredient> chosenIngredients) {
      this.chosenIngredients = chosenIngredients;
    }
    
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_calculate_calorie,
          parent, false);
      return new ImageViewHolder(v);
    }
    
    @Override
    public void onBindViewHolder(ImageViewHolder holder, @SuppressLint("RecyclerView") final int position) {
      ChosenIngredient addedIngredient = chosenIngredients.get(position);
      String amountUnit = "";
      Float calorie = 0f;
      String amount = "";
      String unitOfAmount = "";
      
      switch (addedIngredient.getAmountType()) {
        case "kilogram":
          amountUnit = " gr";
          calorie = addedIngredient.getCalorieAmount();
          amount = "100";
          unitOfAmount = "gr";
          break;
        case "liter":
          amountUnit = " ml";
          calorie = addedIngredient.getCalorieAmount() / 100 * addedIngredient.getWeightOfUnit();
          amount = "100";
          unitOfAmount = "ml";
          break;
        case "piece":
          amountUnit = " pcs";
          calorie = addedIngredient.getWeightOfUnit() / 100 * addedIngredient.getCalorieAmount();
          amount = "1";
          unitOfAmount = "pcs";
          break;
      }
      
      int calorieInt = Math.round(calorie);
      String fullCalorieText = "(" + calorieInt + " kcal / " + amount + " " + unitOfAmount + ")";
      String amountAdded = addedIngredient.getAmount()==-1f ?
                               "" : addedIngredient.getAmount().toString();
      if (!amountAdded.equals("")) {
        while (amountAdded.contains(".") && amountAdded.charAt(amountAdded.length()-1) == '0') {
          amountAdded = amountAdded.substring(0, amountAdded.length() - 2);
        }
        if (amountAdded.charAt(amountAdded.length()-1) == '.') {
          amountAdded = amountAdded.substring(0, amountAdded.length() - 2);
        }
      }
      
      holder.ingredientName.setText(addedIngredient.getName());
      holder.checkBox.setChecked(includeAll);
      addedIngredient.setCalculateCalorie(includeAll);
      holder.calorieTV.setText(fullCalorieText);
      holder.amountET.setText(amountAdded);
      holder.typeTV.setText(amountUnit);
      
      holder.checkBox.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          chosenIngredients.get(position).setCalculateCalorie(((CheckBox) v).isChecked());
          includeAllToCalculationCB.setState(-1);
        }
      });
      
      holder.amountET.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        
        }
        
        @Override
        public void afterTextChanged(Editable s) {
          //addedIngredient.setAmount(s.toString());
          chosenIngredients.get(position).setAmount(s.toString().equals("") ?
                                                       -1f : Float.valueOf(s.toString()));
        }
      });
    }
    
    @Override
    public int getItemCount() {
      return chosenIngredients.size();
    }
    
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
      super.onAttachedToRecyclerView(recyclerView);
    }
  }
  
}
