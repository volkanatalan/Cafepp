package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.databases.TableDatabase;
import net.cafepp.cafepp.enums.TableStatus;
import net.cafepp.cafepp.objects.Table;

import java.util.Calendar;
import java.util.Date;

public class TableContentFragment extends Fragment {
  
  private Menu mMenu;
  private MenuInflater mMenuInflater;
  private FrameLayout fabInterLayer;
  private LinearLayout ordersContainer;
  private Table mTable;
  private TextView tableInfoLabels, tableInfos;
  
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
      mTable.setStatus(selectedSituation);
    }
    
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    
    }
  };
  
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_table_content, container, false);
  
    ordersContainer = view.findViewById(R.id.ordersContainer);
    if (mTable.getStatus() == TableStatus.FREE) {
      ordersContainer.setVisibility(View.GONE);
    }
  
    tableInfoLabels = view.findViewById(R.id.tableInfoLabels);
    tableInfos = view.findViewById(R.id.tableInfos);
    setupTableInfo();
  
    ListView ordersListView = view.findViewById(R.id.ordersListView);
    ordersListView.setOnItemClickListener((parent, view2, position, id) -> {
  
    });
    
    fabInterLayer = view.findViewById(R.id.fabInterLayer);
    fabInterLayer.setOnClickListener(v -> {
      hideInterLayer();
      getFragmentManager().popBackStack();
    });
    return view;
  }
  
  
  @Override
  public void onResume() {
    super.onResume();
  
    if (mTable.getStatus() == TableStatus.FREE) {
      // Open ControlTableFragment.
      showInterLayer();
      getActivity().getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
              R.anim.enter_from_bottom, R.anim.exit_to_top)
          .add(R.id.fragmentContainer, new ChangeTableStatusFragment(), "ChangeTableStatusFragment")
          .addToBackStack("ChangeTableStatusFragment")
          .commit();
    }
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    mMenu = menu;
    mMenuInflater = inflater;
    menu.clear();
    
    if (mTable.getStatus() != TableStatus.FREE) {
      inflater.inflate(R.menu.fragment_table_content, menu);
    }
    
    super.onCreateOptionsMenu(menu, inflater);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
  
    switch (id) {
      case R.id.action_fragmenttablecontent_settings:
        showInterLayer();
        getActivity().getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
                R.anim.enter_from_bottom, R.anim.exit_to_top)
            .add(R.id.fragmentContainer, new ControlTableFragment(), "ControlTableFragment")
            .addToBackStack("ControlTableFragment")
            .commit();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
  private void showInterLayer(){
    fabInterLayer.setVisibility(View.VISIBLE);
    fabInterLayer.animate().alpha(1);
  }
  
  public void hideInterLayer(){
    fabInterLayer.animate().alpha(0).withEndAction(
        () -> fabInterLayer.setVisibility(View.GONE)
    );
  }
  
  public void setTable(Table table) {
    mTable = table;
  }
  
  public void changeTableStatus(TableStatus status) {
    hideInterLayer();
    
    Date openingDate = Calendar.getInstance().getTime();
    
    mTable.setOpeningDate(openingDate);
    mTable.setStatus(status);
    
    setupTableInfo();
    
    if (status != TableStatus.FREE) {
      // Inflate menu.
      mMenu.clear();
      mMenuInflater.inflate(R.menu.fragment_table_content, mMenu);
      
      // Make orders section visible.
      ordersContainer.setVisibility(View.VISIBLE);
    }
    
    TableDatabase database = new TableDatabase(getContext());
    database.add(mTable);
    mTable.setId(database.getTableId(mTable));
    database.close();
    
    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("TablesFragment");
    if (fragment instanceof TablesFragment) {
      TablesFragment tablesFragment = (TablesFragment) fragment;
      tablesFragment.updateTableInCurrentLocation(mTable);
    }
  }
  
  private void setupTableInfo() {
    
    // Setup info labels.
    String locationLabel = getActivity().getResources().getString(R.string.location_semicolon);
    String numberLabel = getActivity().getResources().getString(R.string.number_semicolon);
    String statusLabel = getActivity().getResources().getString(R.string.status_semicolon);
    String openingDateLabel = getActivity().getResources().getString(R.string.opening_date_semicolon);
    String priceLabel = getActivity().getResources().getString(R.string.price_semicolon);
    
    String labelText =
        locationLabel + "\n" +
            numberLabel + "\n" +
            statusLabel + "\n" +
            openingDateLabel + "\n" +
            priceLabel;
  
    tableInfoLabels.setText(labelText);
    
    
    // Setup infos.
    String location = mTable.getLocation();
    String number = mTable.getNumber() + "";
    String status = mTable.getStatus().toString();
    String openingDate = mTable.getOpeningDate() == null ? "" : mTable.getOpeningDate().toString();
    String price = mTable.getPrice() + "";
  
    String infoText =
        location + "\n" +
            number + "\n" +
            status + "\n" +
            openingDate + "\n" +
            price;
  
    tableInfos.setText(infoText);
    
  }
}
