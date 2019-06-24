package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
  private ViewPager mViewPager;
  private ViewPagerAdapter mAdapter;
  private TableFragment mCurrentFragment;
  
  
  
  public TablesFragment() {
    // Required empty public constructor
  }
  
  
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  
    TableDatabase tableDatabase = new TableDatabase(getContext());
    mTableLocations = tableDatabase.getTableLocations();
    mTables = tableDatabase.getTables();
    tableDatabase.close();
  }
  
  
  
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_tables, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }
  
  
  
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_fragmenttables_add:
        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, new AddTableFragment(), "AddTableFragment")
            .addToBackStack("AddTableFragment")
            .commit();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
  
  
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tables, container, false);
    mViewPager = view.findViewById(R.id.viewPager);
    setupViewPager(mViewPager);
    
    TabLayout tabLayout = view.findViewById(R.id.tabLayout);
    tabLayout.setupWithViewPager(mViewPager);
    return view;
  }
  
  
  
  
  private void setupViewPager(ViewPager viewPager) {
    mAdapter = new ViewPagerAdapter(getChildFragmentManager());
    
    for (TableLocation location : mTableLocations) {
      List<Table> tables = new ArrayList<>();
      for (Table table : mTables) {
        if (table.getLocation().equals(location.getName()))
          tables.add(table);
      }
      mAdapter.addFragment(location.getName(), TableFragment.newInstance(location, tables));
    }
    
    viewPager.setAdapter(mAdapter);
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    
      }
  
      @Override
      public void onPageSelected(int position) {
        mCurrentFragment = (TableFragment) mAdapter.getRegisteredFragment(position);
      }
  
      @Override
      public void onPageScrollStateChanged(int state) {
    
      }
    });
  }
  
  
  
  
  public void updateViewPager() {
    TableDatabase tableDatabase = new TableDatabase(getContext());
    mTableLocations = tableDatabase.getTableLocations();
    mTables = tableDatabase.getTables();
    tableDatabase.close();
    
    mAdapter.clear();
    for (TableLocation location : mTableLocations) {
      List<Table> tables = new ArrayList<>();
      for (Table table : mTables) {
        if (table.getLocation().equals(location.getName()))
          tables.add(table);
      }
      mAdapter.addFragment(location.getName(), TableFragment.newInstance(location, tables));
    }
  }
  
  
  
  
  public void updateTableInCurrentLocation(Table table) {
    mCurrentFragment = (TableFragment) mAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
    mCurrentFragment.update(table);
  }
}
