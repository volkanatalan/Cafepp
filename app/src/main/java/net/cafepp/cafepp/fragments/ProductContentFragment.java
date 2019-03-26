package net.cafepp.cafepp.fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.custom_views.ImageSliderView;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.models.Product;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ProductContentFragment extends Fragment {
  private static final String PRODUCT_NAME = "PRODUCT_NAME";
  private String mProductName;
  private TextView productNameTextView;
  private TextView productDescriptionTextView;
  private TextView ingredientsTextView;
  private TextView productCalorieTextView;
  private TextView priceTextView;
  private ImageSliderView imageSliderView;
  
  public ProductContentFragment() {
    // Required empty public constructor
  }
  
  public static ProductContentFragment newInstance(String productName) {
    ProductContentFragment fragment = new ProductContentFragment();
    Bundle args = new Bundle();
    args.putString(PRODUCT_NAME, productName);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mProductName = getArguments().getString(PRODUCT_NAME);
    }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_product_content, container, false);
    imageSliderView = view.findViewById(R.id.imageSliderView);
    productNameTextView = view.findViewById(R.id.productName);
    productDescriptionTextView = view.findViewById(R.id.productDescription);
    ingredientsTextView = view.findViewById(R.id.ingredients);
    productCalorieTextView = view.findViewById(R.id.productCalorie);
    priceTextView = view.findViewById(R.id.priceTextView);
    
  
    ProductDatabase productDatabase = new ProductDatabase(getContext());
    Product product = productDatabase.getProductFromName(mProductName);
  
    imageSliderView.setImagePaths(product.getImages());
    productNameTextView.setText(product.getName());
    productDescriptionTextView.setText(product.getDescription());
    ingredientsTextView.setText(makeIngredientsText(product.getIngredients()));
    productCalorieTextView.setText(product.getCalorie());
    priceTextView.setText(product.getPrice());
    return view;
  }
  
  private String makeIngredientsText(ArrayList<String> ingredients) {
    String ingredientsText = "";
    for (int i = 0; i < ingredients.size(); i++) {
      if (i < ingredients.size() - 1) {
        ingredientsText += ingredients.get(i);
        ingredientsText += ", ";
      } else {
        ingredientsText += ingredients.get(i);
      }
    }
    return ingredientsText;
  }
  
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
  }
}
