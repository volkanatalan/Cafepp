package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cafepp.cafepp.R;

public class StockFragment extends Fragment {
  
  public StockFragment() {
    // Required empty public constructor
  }
  
  public static StockFragment newInstance() {
    StockFragment fragment = new StockFragment();
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
    View view = inflater.inflate(R.layout.fragment_stock, container, false);
    return view;
  }
}
