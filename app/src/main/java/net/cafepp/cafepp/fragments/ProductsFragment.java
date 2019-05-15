package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cafepp.cafepp.R;

public class ProductsFragment extends Fragment {
  
  public static ProductsFragment newInstance() {
    ProductsFragment fragment = new ProductsFragment();
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_products, container, false);
  }
  
}
