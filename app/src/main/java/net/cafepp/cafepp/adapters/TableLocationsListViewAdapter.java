package net.cafepp.cafepp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.List;

public class TableLocationsListViewAdapter extends BaseAdapter {
  
  private List<TableLocation> tableLocations;
  
  public TableLocationsListViewAdapter(List<TableLocation> locations, OnTableNumberClickedListener listener) {
    tableLocations = locations;
    onTableNumberClickedListener = listener;
  }
  
  
  private static class ViewHolder{
    LinearLayout tableNumberLinearLayout;
    TextView locationName;
    TextView tableNumberTextView;
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
    
      viewHolder.tableNumberLinearLayout = convertView.findViewById(R.id.tableNumber);
      viewHolder.locationName = convertView.findViewById(R.id.locationName);
      viewHolder.tableNumberTextView = convertView.findViewById(R.id.tableNumberTextView);
      viewHolder.minusImageView = convertView.findViewById(R.id.minusImageView);
      viewHolder.plusImageView = convertView.findViewById(R.id.plusImageView);
    
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
    }
    
    else {
      // If View Holder is available, reuse it.
      viewHolder = (ViewHolder) convertView.getTag();
    }
    
    TableLocation tableLocation = tableLocations.get(position);
    String locationName = tableLocation.getName();
    int tableNumber = tableLocation.getTotalTable();
    
    viewHolder.locationName.setText(locationName);
    viewHolder.tableNumberTextView.setText(tableNumber + "");
    
    viewHolder.tableNumberLinearLayout.setOnClickListener((v) -> {
      if (onTableNumberClickedListener != null) {
        onTableNumberClickedListener.onClick(tableLocation, viewHolder.tableNumberTextView);
      }
    });
    
    return convertView;
  }
  
  public void add(TableLocation location) {
    tableLocations.add(location);
    notifyDataSetChanged();
  }
  
  public void remove(String location) {
    int index = getIndex(location);
    if (index > -1) {
      tableLocations.remove(index);
      notifyDataSetChanged();
    }
  }
  
  public void set(int index, TableLocation location) {
    tableLocations.set(index, location);
    notifyDataSetChanged();
  }
  
  public int getIndex(String location) {
    for (int i = 0; i < tableLocations.size(); i++) {
      if (location.equals(tableLocations.get(i).getName())) {
        return i;
      }
    }
  
    return -1;
  }
  
  private OnTableNumberClickedListener onTableNumberClickedListener;
  
  public interface OnTableNumberClickedListener {
    void onClick(TableLocation location, TextView textView);
  }
  
  public void setOnTableNumberClickedListener(OnTableNumberClickedListener listener) {
    onTableNumberClickedListener = listener;
  }
}
