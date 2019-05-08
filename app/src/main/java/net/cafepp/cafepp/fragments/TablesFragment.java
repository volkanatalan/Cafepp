package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cafepp.cafepp.R;

public class TablesFragment extends Fragment {
  
  public TablesFragment() {
    // Required empty public constructor
  }
  
  public static TablesFragment newInstance() {
    TablesFragment fragment = new TablesFragment();
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tables, container, false);
    return view;
  }
  
}
