package net.cafepp.cafepp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.cafepp.cafepp.objects.Table;

import java.util.List;

public class TablesAdapter extends BaseAdapter {
  
  private final Context mContext;
  private List<Table> mTables;
  
  public TablesAdapter(Context context, List<Table> tables) {
    mContext = context;
    mTables = tables;
  }
  
  @Override
  public int getCount() {
    return mTables.size();
  }
  
  @Override
  public Object getItem(int position) {
    return mTables.get(position);
  }
  
  @Override
  public long getItemId(int position) {
    return position;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return null;
  }
}
