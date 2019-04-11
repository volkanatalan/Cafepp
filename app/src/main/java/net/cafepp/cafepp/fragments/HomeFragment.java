package net.cafepp.cafepp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.HomeActivity;
import net.cafepp.cafepp.activities.MenuActivity;

public class HomeFragment extends Fragment {
  
  public HomeFragment() {
    // Required empty public constructor
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    Button menuButton = view.findViewById(R.id.menuButton);
    menuButton.setOnClickListener(v -> {
      Intent intent = new Intent(getActivity(), MenuActivity.class);
      startActivity(intent);
    });
    return view;
  }
  
}
