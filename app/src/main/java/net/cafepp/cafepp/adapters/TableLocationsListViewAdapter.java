package net.cafepp.cafepp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.List;

public class TableLocationsListViewAdapter extends BaseAdapter {
  
  private List<TableLocation> tableLocations;
  
  public TableLocationsListViewAdapter(List<TableLocation> locations) {
    tableLocations = locations;
  }
  
  
  private static class ViewHolder{
    TextView locationName;
    EditText tableNumberEditText;
    ImageView minusImageView, plusImageView;
  }
  
  @Override
  public int getCount() {
    return tableLocations.size();
  }
  
  @Override
  public TableLocation getItem(int position) {
    return tableLocations.get(position);
  }
  
  @Override
  public long getItemId(int position) {
    return position;
  }
  
  @SuppressLint("SetTextI18n")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
  
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_table_locations, null);
    
      viewHolder.locationName = convertView.findViewById(R.id.locationName);
      viewHolder.tableNumberEditText = convertView.findViewById(R.id.tableNumberEditText);
      viewHolder.minusImageView = convertView.findViewById(R.id.minusImageView);
      viewHolder.plusImageView = convertView.findViewById(R.id.plusImageView);
    
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
    }
    
    else {
      // If View Holder is available, reuse it.
      viewHolder = (ViewHolder) convertView.getTag();
    }
    
    String locationName = tableLocations.get(position).getName();
    int tableNumber = tableLocations.get(position).getNumber();
    
    viewHolder.locationName.setText(locationName);
    viewHolder.tableNumberEditText.setText(tableNumber + "");
  
    viewHolder.plusImageView.setOnClickListener((v) -> {
      int number = tableLocations.get(position).getNumber();
      tableLocations.get(position).setNumber(++number);
  
      viewHolder.tableNumberEditText.setText(number +"");
    });
  
    viewHolder.minusImageView.setOnClickListener((v) -> {
      int number = tableLocations.get(position).getNumber();
      number--;
      
      if (number >= 0) {
        tableLocations.get(position).setNumber(number);
        viewHolder.tableNumberEditText.setText(number+"");
      }
    });
    
    return convertView;
  }
  
  public void add(TableLocation location) {
    tableLocations.add(location);
    notifyDataSetChanged();
  }
}
