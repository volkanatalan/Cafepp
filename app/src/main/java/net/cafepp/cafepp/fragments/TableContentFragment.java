package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.Table;

public class TableContentFragment extends Fragment {
  
  private FrameLayout fabInterLayer;
  private LinearLayout orderCancelContainer, paymentFabContainer, orderFabContainer;
  private TextView orderCancelLabel, paymentFabLabel, orderFabLabel;
  private FloatingActionButton fab, orderFab, paymentFab, orderCancellationFab;
  private Table mTable;
  private boolean isFABOpen;
  
  public TableContentFragment() {
    // Required empty public constructor
  }
  
  public static TableContentFragment newInstance(Table table) {
    TableContentFragment fragment = new TableContentFragment();
    fragment.setTable(table);
    return fragment;
  }
  
  
  private AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      String selectedSituation = parent.getItemAtPosition(position).toString();
      mTable.setSituation(selectedSituation);
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    
    }
  };
  
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_table_content, container, false);
  
    Spinner spinner = view.findViewById(R.id.tableSituationSpinner);
    spinner.setOnItemSelectedListener(selectedListener);
    ArrayAdapter dataAdapter = ArrayAdapter.createFromResource(
        getContext(), R.array.table_situation, R.layout.spinner_item);
    dataAdapter.setDropDownViewResource(R.layout.spinner_item);
    spinner.setAdapter(dataAdapter);
  
    ListView ordersListView = view.findViewById(R.id.ordersListView);
    ordersListView.setOnItemClickListener((parent, view2, position, id) -> {
  
    });
    
    fabInterLayer = view.findViewById(R.id.fabInterLayer);
    fabInterLayer.setOnClickListener(v -> closeFABMenu());
    
    orderCancelContainer = view.findViewById(R.id.orderCancelContainer);
    orderCancelLabel = view.findViewById(R.id.orderCancelLabel);
    orderFab = view.findViewById(R.id.orderFab);
  
    paymentFabContainer = view.findViewById(R.id.paymentFabContainer);
    paymentFabLabel = view.findViewById(R.id.paymentFabLabel);
    paymentFab = view.findViewById(R.id.paymentFab);
  
    orderFabContainer = view.findViewById(R.id.orderFabContainer);
    orderFabLabel = view.findViewById(R.id.orderFabLabel);
    orderCancellationFab = view.findViewById(R.id.orderCancellationFab);
  
    fab = view.findViewById(R.id.fab);
    fab.setOnClickListener(view1 -> {
      if(!isFABOpen){
        showFABMenu();
      } else {
        closeFABMenu();
      }
    });
    return view;
  }
  
  private void showFABMenu(){
    isFABOpen=true;
  
    fabInterLayer.setAlpha(0);
    fabInterLayer.setVisibility(View.VISIBLE);
    fabInterLayer.animate().alpha(1);
  
    fab.animate().rotation(360).withEndAction(
        () -> fab.setImageResource(R.drawable.close));
    
    orderCancelContainer.setVisibility(View.VISIBLE);
    paymentFabContainer.setVisibility(View.VISIBLE);
    orderFabContainer.setVisibility(View.VISIBLE);
  
    orderCancelContainer.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
    paymentFabContainer.animate().translationY(-getResources().getDimension(R.dimen.standard_130));
    orderFabContainer.animate().translationY(-getResources().getDimension(R.dimen.standard_195))
    
    .withEndAction(() -> {
      orderCancelLabel.setVisibility(View.VISIBLE);
      paymentFabLabel.setVisibility(View.VISIBLE);
      orderFabLabel.setVisibility(View.VISIBLE);
    });
  }
  
  public void closeFABMenu(){
    isFABOpen=false;
  
    fabInterLayer.animate().alpha(0).withEndAction(
        () -> fabInterLayer.setVisibility(View.GONE)
    );
  
    fab.animate().rotation(0).withEndAction(
        () -> fab.setImageResource(R.drawable.more_vert));
  
    orderCancelLabel.setVisibility(View.GONE);
    paymentFabLabel.setVisibility(View.GONE);
    orderFabLabel.setVisibility(View.GONE);
    
    orderCancelContainer.animate().translationY(0);
    paymentFabContainer.animate().translationY(0);
    orderFabContainer.animate().translationY(0)
    
    .withEndAction(() -> {
      orderCancelContainer.setVisibility(View.GONE);
      paymentFabContainer.setVisibility(View.GONE);
      orderFabContainer.setVisibility(View.GONE);
    });
  }
  
  public boolean isFABOpen() {
    return isFABOpen;
  }
  
  public void setTable(Table table) {
    mTable = table;
  }
}
