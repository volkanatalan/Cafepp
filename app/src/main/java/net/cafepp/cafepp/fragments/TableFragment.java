package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.TablesGridAdapter;
import net.cafepp.cafepp.objects.Table;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.ArrayList;
import java.util.List;

public class TableFragment extends Fragment {
  
  private TableLocation mTableLocation;
  private List<Table> mNotFreeTables;
  private List<Table> mAllTables = new ArrayList<>();
  private List<Table> mExtraTables = new ArrayList<>();
  private TableContentFragment mTableContentFragment;
  private TablesGridAdapter mTablesGridAdapter;
  
  public TableFragment() {
    // Required empty public constructor
  }
  
  public static TableFragment newInstance(TableLocation location, List<Table> notFreeTables) {
    TableFragment fragment = new TableFragment();
    fragment.setTableLocation(location);
    fragment.setNotFreeTables(notFreeTables);
    return fragment;
  }
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupGridAdapterView(mTableLocation);
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_table, container, false);
    
    mTablesGridAdapter = new TablesGridAdapter(mAllTables);
    
    GridView gridView = view.findViewById(R.id.gridView);
    gridView.setOnItemClickListener((parent, view1, position, id) -> {
      // Open TableContentFragment.
      Table table = mTablesGridAdapter.getItem(position);
      mTableContentFragment = TableContentFragment.newInstance(table);
      FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
      ft.add(R.id.fragmentContainer, mTableContentFragment, "TableContentFragment")
          .addToBackStack("TableContentFragment")
          .commit();
    });
    gridView.setAdapter(mTablesGridAdapter);
    
    return view;
  }
  
  private void setupGridAdapterView(TableLocation location) {
    
    String locationName = location.getName();
    int numberOfTables = location.getTotalTable();
    
    mAllTables.clear();
    
    // Move tables in mExtraTables list to mNotFreeTables.
    mNotFreeTables.addAll(mExtraTables);
    mExtraTables.clear();
    
    // Find extra tables.
    for (int i = 0; i < mNotFreeTables.size(); i++) {
      Table table = mNotFreeTables.get(i);
      if (table.getNumber() > numberOfTables) {
        mExtraTables.add(table);
        mNotFreeTables.remove(i);
      }
    }
    
    
    int totalNumber = numberOfTables + mExtraTables.size();
    int tableNumber = 0;
    
    
    // Populate mAllTables list with default tables.
    for (int i = 0; i < totalNumber; i++) {
      tableNumber++;
      mAllTables.add(new Table(tableNumber, locationName));
    }
    
    
    // Put not free tables to mAllTables list.
    for (Table table : mNotFreeTables) {
      int number = table.getNumber();
      int index = number - 1;
      mAllTables.set(index, table);
    }
    
    
    // Put extra tables to mAllTables list.
    for (int i = 0; i < mExtraTables.size(); i++) {
      int index = numberOfTables + i;
      Table table = mExtraTables.get(i);
      mAllTables.set(index, table);
    }
  }
  
  public void update(TableLocation location) {
    setupGridAdapterView(location);
  }
  
  public void update(Table table) {
    // Find out if there is the same table in mNotFreeTables list.
    for (int i = 0; i < mNotFreeTables.size(); i++) {
      if (table.getNumber() == mNotFreeTables.get(i).getNumber()) {
        // If there is the same table update it.
        mNotFreeTables.set(i, table);
      }
    }
    
    // Update mAllTables list.
    for (int i = 0; i < mAllTables.size(); i++) {
      if (table.getNumber() == mAllTables.get(i).getNumber()) {
        mAllTables.set(i, table);
      }
    }
  
    mTablesGridAdapter.notifyDataSetChanged();
  }
  
  public TableLocation getTableLocation() {
    return mTableLocation;
  }
  
  public void setTableLocation(TableLocation location) {
    mTableLocation = location;
  }
  
  public void setNotFreeTables(List<Table> tables) {
    mNotFreeTables = tables;
  }
}
