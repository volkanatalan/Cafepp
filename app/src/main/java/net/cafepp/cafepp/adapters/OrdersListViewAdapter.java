package net.cafepp.cafepp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.objects.Order;

import java.util.List;

public class OrdersListViewAdapter extends BaseAdapter {
  
  List<Order> mOrders;
  
  public OrdersListViewAdapter(List<Order> orders) {
    mOrders = orders;
  }
  
  
  private static class ViewHolder{
    private TextView productName, price, quantity, currency;
  }
  
  @Override
  public int getCount() {
    return mOrders.size();
  }
  
  @Override
  public Order getItem(int position) {
    return mOrders.get(position);
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
                        .inflate(R.layout.list_item_orders, parent, false);
    
      viewHolder.productName = convertView.findViewById(R.id.productName);
      viewHolder.price = convertView.findViewById(R.id.price);
      viewHolder.quantity = convertView.findViewById(R.id.quantity);
      viewHolder.currency = convertView.findViewById(R.id.currency);
    
      // Save View Holder to reuse it later.
      convertView.setTag(viewHolder);
    
    } else {
      // If View Holder is available, reuse it.
      viewHolder = (ViewHolder) convertView.getTag();
    }
  
    viewHolder.productName.setText(mOrders.get(position).getName());
    viewHolder.price.setText(mOrders.get(position).getPrice());
    viewHolder.quantity.setText(mOrders.get(position).getQuantity() +"");
    viewHolder.currency.setText(mOrders.get(position).getPrice());
    
    return convertView;
  }
}
