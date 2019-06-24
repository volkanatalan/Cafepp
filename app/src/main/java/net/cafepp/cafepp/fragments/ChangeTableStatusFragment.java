package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.enums.TableStatus;

public class ChangeTableStatusFragment extends Fragment {
  
  public ChangeTableStatusFragment() {
    // Required empty public constructor
  }
  
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_change_table_status, container, false);
    LinearLayout free = view.findViewById(R.id.free);
    free.setOnClickListener(onClickFree);
  
    LinearLayout reserved = view.findViewById(R.id.reserved);
    reserved.setOnClickListener(onClickReserved);
    
    LinearLayout occupied = view.findViewById(R.id.occupied);
    occupied.setOnClickListener(onClickOccupied);
    return view;
  }
  
  private View.OnClickListener onClickFree = (v) ->{
    changeTableStatus(TableStatus.FREE);
  };
  
  private View.OnClickListener onClickReserved = (v) ->{
    changeTableStatus(TableStatus.RESERVED);
  };
  
  private View.OnClickListener onClickOccupied = (v) ->{
    changeTableStatus(TableStatus.OCCUPIED);
  };
  
  private void changeTableStatus(TableStatus status) {
    Fragment fragment = getActivity().getSupportFragmentManager()
                            .findFragmentByTag("TableContentFragment");
    if (fragment instanceof TableContentFragment) {
      TableContentFragment tableContentFragment = (TableContentFragment) fragment;
      tableContentFragment.changeTableStatus(status);
    }
  
    getActivity().getSupportFragmentManager().popBackStack();
  }
}
