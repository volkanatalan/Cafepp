package net.cafepp.cafepp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.enums.TableSituation;
import net.cafepp.cafepp.objects.Table;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TablesAdapter extends BaseAdapter {
  
  private final String TAG = "TablesAdapter";
  private final Context mContext;
  private List<Table> mTables;
  
  public TablesAdapter(Context context, List<Table> tables) {
    mContext = context;
    mTables = tables;
  }
  
  private static class ViewHolder{
    LinearLayout backgroundLinearLeyout;
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
  
      viewHolder.backgroundLinearLeyout = convertView.findViewById(R.id.background);
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
    
    TableSituation tableSituation = mTables.get(position).getSituation();
    int backgroundDrawable = R.drawable.table_bg_free;
    String tableName = "Table " + mTables.get(position).getName();
    float price = mTables.get(position).getPrice();
    String priceText = price + " ₺";
    String dateString = "";
    Date date = mTables.get(position).getOpeningDate();
    if (date != null) {
      DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
      dateString = dateFormat.format(date);
    }
    
    
    switch (tableSituation) {
      case FREE:
        dateString = "";
        priceText = "";
        break;
        
      case OCCUPIED:
        backgroundDrawable = R.drawable.table_bg_occupied;
        break;
        
      case RESERVED:
        backgroundDrawable = R.drawable.table_bg_reserved;
        dateString = "";
        break;
    }
    
    
    viewHolder.backgroundLinearLeyout.setBackgroundResource(backgroundDrawable);
    viewHolder.tableNameTextView.setText(tableName);
    viewHolder.situationTextView.setText(tableSituation.toString());
    viewHolder.priceTextView.setText(priceText);
    viewHolder.openingTimeTextView.setText(dateString);
    return convertView;
  }
}
