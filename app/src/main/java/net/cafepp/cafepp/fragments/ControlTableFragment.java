package net.cafepp.cafepp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.MenuActivity;

public class ControlTableFragment extends Fragment {
  
  
  public ControlTableFragment() {
    // Required empty public constructor
  }
  
  
  
  
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }
  
  
  
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_control_table, container, false);
    ImageView takeOrderImageView = view.findViewById(R.id.takeOrderImageView);
    takeOrderImageView.setOnClickListener(onClickTakeOrderImageView);
    
    ImageView paymentImageView = view.findViewById(R.id.paymentImageView);
    paymentImageView.setOnClickListener(onClickPaymentImageView);
  
    ImageView orderCancellationImageView = view.findViewById(R.id.orderCancellationImageView);
    orderCancellationImageView.setOnClickListener(onClickOrderCancellationImageView);
  
    ImageView tableStatusImageView = view.findViewById(R.id.tableStatusImageView);
    tableStatusImageView.setOnClickListener(onClickTableStatusImageView);
    
    return view;
  }
  
  
  
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    super.onCreateOptionsMenu(menu, inflater);
  }
  
  
  
  
  View.OnClickListener onClickTakeOrderImageView = v -> {
    getActivity().getSupportFragmentManager().popBackStack();
    ((TableContentFragment) getActivity().getSupportFragmentManager().findFragmentByTag("TableContentFragment"))
        .hideInterLayer();
    
    Intent intent = new Intent(getContext(), MenuActivity.class);
    startActivity(intent);
  };
  
  
  
  
  View.OnClickListener onClickPaymentImageView = v -> {
  
  };
  
  
  
  
  View.OnClickListener onClickOrderCancellationImageView = v -> {
  
  };
  
  
  
  
  View.OnClickListener onClickTableStatusImageView = v -> {
    getActivity().getSupportFragmentManager().popBackStackImmediate();
    getActivity().getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
            R.anim.enter_from_bottom, R.anim.exit_to_top)
        .add(R.id.fragmentContainer, new ChangeTableStatusFragment(), "ChangeTableStatusFragment")
        .addToBackStack("ChangeTableStatusFragment")
        .commit();
  };
}
