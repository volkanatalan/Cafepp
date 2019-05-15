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
import net.cafepp.cafepp.adapters.TablesAdapter;
import net.cafepp.cafepp.enums.TableSituation;
import net.cafepp.cafepp.objects.Table;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.Calendar;
import java.util.List;

public class TableFragment extends Fragment {
  
  private TableLocation mTableLocation;
  private List<Table> mTables;
  private TableContentFragment mTableContentFragment;
  
  public TableFragment() {
    // Required empty public constructor
  }
  
  public static TableFragment newInstance(TableLocation location, List<Table> tables) {
    TableFragment fragment = new TableFragment();
    fragment.setTableLocation(location);
    fragment.setTables(tables);
    return fragment;
  }
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    String locationName = mTableLocation.getName();
  
    for (int i = 0; i < mTableLocation.getNumber(); i++) {
      Table table = null;
      if (mTables != null && mTables.size() > 0) {
        for (Table t : mTables) {
          if (t != null && t.getName().equals(i + "")) {
            table = t;
          }
          else {
            table = new Table(i+"", locationName);
          }
        }
      }
      else {
        table = new Table(i+"", locationName);
      }
      
      if (i == 1){
        table.setSituation(TableSituation.OCCUPIED);
        table.setOpeningDate(Calendar.getInstance().getTime());
      }
      else if (i == 2)
        table.setSituation(TableSituation.RESERVED);
    
      mTables.add(table);
    }
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_table, container, false);
    TablesAdapter tablesAdapter = new TablesAdapter(getContext(), mTables);
  
    GridView gridView = view.findViewById(R.id.gridView);
    gridView.setOnItemClickListener((parent, view1, position, id) -> {
      Table table = tablesAdapter.getItem(position);
      mTableContentFragment = TableContentFragment.newInstance(table);
      FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
      ft.add(R.id.fragmentContainer, mTableContentFragment, "TableContentFragment")
          .addToBackStack("TableContentFragment")
          .commit();
    });
    gridView.setAdapter(tablesAdapter);
    return view;
  }
  
  public void setTableLocation(TableLocation location) {
    mTableLocation = location;
  }
  
  public void setTables(List<Table> tables) {
    mTables = tables;
  }
  
  public TableContentFragment getTableContentFragment() {
    return mTableContentFragment;
  }
  
}
