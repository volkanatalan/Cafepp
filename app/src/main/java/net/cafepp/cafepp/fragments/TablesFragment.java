package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.TablesAdapter;
import net.cafepp.cafepp.enums.TableSituation;
import net.cafepp.cafepp.objects.Table;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TablesFragment extends Fragment {
  
  private List<Table> tables = new ArrayList<>();
  
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
    
    for (int i = 0; i < 15; i++) {
      Table table = new Table("Table " + i, Calendar.getInstance().getTime());
      table.setPrice(i * 10);
      
      if (i == 1)
        table.setSituation(TableSituation.OCCUPIED);
      else if (i == 2)
        table.setSituation(TableSituation.RESERVED);
  
      tables.add(table);
    }
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tables, container, false);
    GridView gridView = view.findViewById(R.id.gridView);
    gridView.setOnItemClickListener((parent, view1, position, id) -> {
    
    });
    TablesAdapter tablesAdapter = new TablesAdapter(getContext(), tables);
    gridView.setAdapter(tablesAdapter);
    return view;
  }
  
}
