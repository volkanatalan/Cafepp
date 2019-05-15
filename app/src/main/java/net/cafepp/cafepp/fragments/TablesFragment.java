package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.ViewPagerAdapter;
import net.cafepp.cafepp.databases.TableDatabase;
import net.cafepp.cafepp.objects.Table;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.ArrayList;
import java.util.List;

public class TablesFragment extends Fragment {
  private List<TableLocation> mTableLocations;
  private List<Table> mTables;
  
  public TablesFragment() {
    // Required empty public constructor
  }
  
  public static TablesFragment newInstance(FragmentManager manager) {
    TablesFragment fragment = new TablesFragment();
    fragment.setSupportFragmentManager(manager);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
    TableDatabase tableDatabase = new TableDatabase(getContext());
    mTableLocations = tableDatabase.getTableLocations();
    mTables = tableDatabase.getTables();
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tables, container, false);
    ViewPager viewPager = view.findViewById(R.id.viewPager);
    setupViewPager(viewPager);
    
    TabLayout tabLayout = view.findViewById(R.id.tabLayout);
    tabLayout.setupWithViewPager(viewPager);
    return view;
  }
  
  private void setupViewPager(ViewPager viewPager) {
    ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
    
    for (TableLocation location : mTableLocations) {
      List<Table> tables = new ArrayList<>();
      for (Table table : mTables) {
        if (table.getLocation().equals(location))
          tables.add(table);
      }
      adapter.addFragment(location.getName(), TableFragment.newInstance(location, tables));
    }
    
    viewPager.setAdapter(adapter);
  }
  
  public void setSupportFragmentManager(FragmentManager manager) {
    FragmentManager mFragmentManager = manager;
  }
}
