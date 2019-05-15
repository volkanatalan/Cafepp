package net.cafepp.cafepp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.TableLocationsListViewAdapter;
import net.cafepp.cafepp.databases.TableDatabase;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.List;

public class AddTableFragment extends Fragment {
  
  private List<TableLocation> mTableLocations;
  
  public AddTableFragment() {
    // Required empty public constructor
  }
  
  public static AddTableFragment newInstance(List<TableLocation> locations) {
    AddTableFragment fragment = new AddTableFragment();
    fragment.setTableLocations(locations);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_table, container, false);
    ImageView addLocationImageView = view.findViewById(R.id.addLocationImageView);
    ListView listView = view.findViewById(R.id.listView);
  
    TableLocationsListViewAdapter adapter = new TableLocationsListViewAdapter(mTableLocations);
    
    addLocationImageView.setOnClickListener((v) -> {
    getActivity().getSupportFragmentManager().beginTransaction()
        .add(R.id.fragmentContainer, AddTableLocationFragment.newInstance(
            new AddTableLocationFragment.OnButtonClickListener() {
          @Override
          public void onClickConfirm(TableLocation tableLocation) {
            TableDatabase database = new TableDatabase(getContext());
            if (database.hasTableLocation(tableLocation.getName())) {
              Toast.makeText(getContext(), "There is already a location with the same name.",
                  Toast.LENGTH_LONG).show();
            }
            
            else {
              database.addTableLocation(tableLocation);
              adapter.add(tableLocation);
              getFragmentManager().popBackStack();
            }
          }
  
          @Override
          public void onClickCancel() {
            getFragmentManager().popBackStack();
          }
        }), "AddTableLocationFragment")
        .addToBackStack("AddTableLocationFragment")
        .commit();
    });
  
    listView.setAdapter(adapter);
    
    return view;
  }
  
  
  public void setTableLocations(List<TableLocation> locations) {
    mTableLocations = locations;
  }
}
