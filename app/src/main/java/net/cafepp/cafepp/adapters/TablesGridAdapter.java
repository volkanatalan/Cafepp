package net.cafepp.cafepp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.enums.TableStatus;
import net.cafepp.cafepp.objects.Table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TablesGridAdapter extends BaseAdapter {
  
  private final String TAG = "TablesGridAdapter";
  private List<Table> mTables;
  
  public TablesGridAdapter(List<Table> tables) {
    mTables = tables;
  }
  
  private static class ViewHolder{
    LinearLayout backgroundLinearLayout;
    TextView tableNameTextView;
    TextView openingTimeTextView;
    TextView situationTextView;
    TextView priceTextView;
  }
  
  @Override
  public int getCount() {
    return mTables.size();
  }
  
  @Override
  public Table getItem(int position) {
    return mTables.get(position);
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
      convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.grid_item_tables, parent, false);
  
      viewHolder.backgroundLinearLayout = convertView.findViewById(R.id.background);
      viewHolder.tableNameTextView = convertView.findViewById(R.id.tableName);
      viewHolder.openingTimeTextView = convertView.findViewById(R.id.openingTime);
      viewHolder.situationTextView = convertView.findViewById(R.id.situation);
      viewHolder.priceTextView = convertView.findViewById(R.id.price);
    
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
    
    } else {
      // If View Holder is available, reuse it.
      viewHolder = (ViewHolder) convertView.getTag();
    }
  
  
    Table table = mTables.get(position);
    int tableNumber = table.getNumber();
    String tableName = "Table " + tableNumber;
    TableStatus tableStatus = table.getStatus();
    float price = 0;
    String priceText = price + " $";
    Date date = table.getOpeningDate();
    String dateString = "";
    if (date != null) {
      DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
      dateString = dateFormat.format(date);
    }
    int backgroundDrawable = R.drawable.table_bg_free;
    
    switch (tableStatus) {
      case FREE:
        dateString = "";
        priceText = "";
        break;
        
      case OCCUPIED:
        backgroundDrawable = R.drawable.table_bg_occupied;
        break;
        
      case RESERVED:
        backgroundDrawable = R.drawable.table_bg_reserved;
        break;
    }
    
    
    viewHolder.backgroundLinearLayout.setBackgroundResource(backgroundDrawable);
    viewHolder.tableNameTextView.setText(tableName);
    viewHolder.situationTextView.setText(tableStatus.toString());
    viewHolder.priceTextView.setText(priceText);
    viewHolder.openingTimeTextView.setText(dateString);
    return convertView;
  }
}
