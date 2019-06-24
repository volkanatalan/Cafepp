package net.cafepp.cafepp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.TableLocationsListViewAdapter;
import net.cafepp.cafepp.custom_views.DynamicListView;
import net.cafepp.cafepp.databases.TableDatabase;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.List;

public class AddTableFragment extends Fragment {
  
  private List<TableLocation> mTableLocations;
  private TableLocationsListViewAdapter adapter;
  private FrameLayout interlayer;
  
  
  
  public AddTableFragment() {
    // Required empty public constructor
  }
  
  
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    
    TableDatabase database = new TableDatabase(getContext());
    mTableLocations = database.getTableLocations();
  }
  
  
  
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.clear();
    inflater.inflate(R.menu.fragment_add_table, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }
  
  
  
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_fragmentaddtable_add:
        onOptionsItemSelectedActionAdd();
        break;
      case R.id.action_fragmentaddtable_done:
        onOptionsItemSelectedActionDone();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
  
  
  
  @SuppressLint("SetTextI18n")
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_table, container, false);
    DynamicListView listView = view.findViewById(R.id.listView);
    interlayer = view.findViewById(R.id.interlayer);
  
    adapter = new TableLocationsListViewAdapter(mTableLocations, (tableLocation, textView) -> {
      // On click table number button
      // Show interlayer.
      showInterlayer(true);
      
      // Open NumberPickerFragment.
      getActivity().getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
              R.anim.enter_from_bottom, R.anim.exit_to_top)
          .add(R.id.fragmentContainer, NumberPickerFragment.newInstance(
              tableLocation.getTotalTable(), new NumberPickerFragment.OnButtonClickListener() {
                @Override
                public void onClickConfirm(int number) {
                  // Hide interlayer.
                  showInterlayer(false);
                  
                  // Save table to database.
                  tableLocation.setTotalTable(number);
                  TableDatabase database = new TableDatabase(getContext());
                  database.updateTableLocation(tableLocation);
                  
                  // On number picked
                  tableLocation.setTotalTable(number);
                  textView.setText(number +"");
                }
      
                @Override
                public void onClickCancel() {
                  // Hide interlayer.
                  showInterlayer(false);
                }
              }))
          .addToBackStack("NumberPickerFragment")
          .commit();
    });
    
    // Set setOnItemClickListener to list view.
    listView.setOnItemClickListener((parent, view1, position, id) -> {
      // Show interlayer.
      showInterlayer(true);
      
      AddTableLocationFragment fragment = new AddTableLocationFragment();
      fragment.setTableLocation(adapter.getItem(position));
      fragment.setOnButtonClickListener(new AddTableLocationFragment.OnButtonClickListener() {
        @Override
        public void onClickConfirm(TableLocation location) {
          // Hide interlayer.
          showInterlayer(false);
          
          // Save table to database.
          TableDatabase database = new TableDatabase(getContext());
          database.updateTableLocation(location);
          
          adapter.set(position, location);
          
          getFragmentManager().popBackStack();
        }
        
        
        @Override
        public void onClickCancel() {
          // Hide interlayer.
          showInterlayer(false);
          
          getFragmentManager().popBackStack();
        }
  
        
        @Override
        public void onClickRemove(TableLocation location) {
          // Hide interlayer.
          showInterlayer(false);
  
          // Remove table location.
          TableDatabase database = new TableDatabase(getContext());
          database.remove(location);
          database.close();
          adapter.remove(location.getName());
  
          getFragmentManager().popBackStack();
        }
      });
      
      getActivity().getSupportFragmentManager().beginTransaction()
          .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
              R.anim.enter_from_bottom, R.anim.exit_to_top)
          .add(R.id.fragmentContainer, fragment)
          .addToBackStack("AddTableLocationFragment")
          .commit();
  
    });
  
    //listView.setList(mTableLocations);
    listView.setAdapter(adapter);
    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    
    return view;
  }
  
  
  
  
  private void onOptionsItemSelectedActionAdd() {
    // Show interlayer.
    showInterlayer(true);
    
    getActivity().getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
            R.anim.enter_from_bottom, R.anim.exit_to_top)
        .add(R.id.fragmentContainer, AddTableLocationFragment.newInstance(
            new AddTableLocationFragment.OnButtonClickListener() {
            
              @Override
              public void onClickConfirm(TableLocation location) {
                // Hide interlayer.
                showInterlayer(false);
                
                TableDatabase database = new TableDatabase(getContext());
                if (database.hasTableLocation(location.getName())) {
                  Toast.makeText(getContext(),
                      getString(R.string.there_is_already_a_location_with_the_same_name),
                      Toast.LENGTH_LONG).show();
                }
                else {
                  location = database.add(location);
                  adapter.add(location);
                  getFragmentManager().popBackStack();
                }
                database.close();
              }
              
              @Override
              public void onClickCancel() {
                // Hide interlayer.
                showInterlayer(false);
                
                getFragmentManager().popBackStack();
              }
  
              @Override
              public void onClickRemove(TableLocation location) {
                // Hide interlayer.
                showInterlayer(false);
                
                // Remove table location.
                TableDatabase database = new TableDatabase(getContext());
                database.remove(location);
                database.close();
                adapter.remove(location.getName());
  
                getFragmentManager().popBackStack();
              }
            }))
        .addToBackStack("AddTableLocationFragment")
        .commit();
  }
  
  
  
  
  public void onOptionsItemSelectedActionDone() {
    Fragment fragment = getFragmentManager().findFragmentByTag("TablesFragment");
    if (fragment instanceof TablesFragment) {
      TablesFragment tablesFragment = (TablesFragment) fragment;
      tablesFragment.updateViewPager();
    }
    getActivity().getSupportFragmentManager().popBackStackImmediate();
  }
  
  
  
  
  public void showInterlayer(boolean show) {
    if (show) {
      interlayer.setVisibility(View.VISIBLE);
      interlayer.animate().alpha(1).start();
    }
    
    else {
      interlayer.animate()
          .alpha(0)
          .withEndAction(() -> interlayer.setVisibility(View.GONE))
          .start();
    }
  }
}
