package net.cafepp.cafepp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.enums.TableSituation;
import net.cafepp.cafepp.objects.Table;
import net.cafepp.cafepp.objects.TableLocation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TablesGridAdapter extends BaseAdapter {
  
  private final String TAG = "TablesGridAdapter";
  private final Context mContext;
  private List<Table> mNotFreeTables;
  private List<Table> mExtraTables = new ArrayList<>();
  private List<Table> mTables = new ArrayList<>();
  private int mTotalTable;
  private int mCount;
  private int mTableNumber = 0;
  
  public TablesGridAdapter(Context context, int totalTable, List<Table> notFreeTables) {
    mContext = context;
    mNotFreeTables = notFreeTables;
  
    
    // Count extra tables.
    mTotalTable = totalTable;
    for (Table table : mNotFreeTables) {
      if (table.getNumber() > mTotalTable) {
        mExtraTables.add(table);
      }
    }
    
    // Set count.
    mCount = mTotalTable + mExtraTables.size();
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
    return mCount;
  }
  
  @Override
  public Table getItem(int position) {
    return mNotFreeTables.get(position);
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
  
  
    TableSituation tableSituation = TableSituation.FREE;
    int backgroundDrawable = R.drawable.table_bg_free;
    float price = 0;
    Date date = null;
    String dateString = "";
    mTableNumber++;
  
    if (position < mTotalTable) {
      for (Table table : mNotFreeTables) {
        if (table.getNumber() == position + 1) {
          tableSituation = table.getSituation();
          price = table.getPrice();
          date = table.getOpeningDate();
        }
      }
    }
    
    // If there is an extra table.
    else {
      Table table = mExtraTables.get(position - mTotalTable);
      mTableNumber = table.getNumber();
      tableSituation = table.getSituation();
      price = table.getPrice();
      date = table.getOpeningDate();
    }
    
    
    String tableName = "Table " + mTableNumber;
    String priceText = price + " $";
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
  
  public void setTotalTable(int totalTable) {
    mTotalTable = totalTable;
  }
}
