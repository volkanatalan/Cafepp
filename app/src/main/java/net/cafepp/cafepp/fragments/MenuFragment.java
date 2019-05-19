package net.cafepp.cafepp.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.custom_views.CircleImageView;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.objects.Product;

import java.util.ArrayList;

/**
 * {@link MenuFragment} contains a ListView, which is consist of the products.
 */

public class MenuFragment extends Fragment {
  private static final String CATEGORY_NAME = "CATEGORY_NAME";
  private ArrayList<Product> mProductList;
  private ListView productListView;
  private TextView warningTextView;
  
  public MenuFragment() {
    // Required empty public constructor
  }
  public static MenuFragment newInstance(String categoryName) {
    MenuFragment fragment = new MenuFragment();
    Bundle args = new Bundle();
    args.putString(CATEGORY_NAME, categoryName);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      String categoryName = getArguments().getString(CATEGORY_NAME);
      ProductDatabase productDatabase = new ProductDatabase(getContext());
      mProductList = productDatabase.getProductsFromCategory(categoryName);
    }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_menu, container, false);
    productListView = view.findViewById(R.id.productListView);
    warningTextView = view.findViewById(R.id.warningTextView);
    return view;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    if (mProductList.size() > 0) {
      final ProductListAdapter productListAdapter = new ProductListAdapter(getActivity(), mProductList);
      productListView.setAdapter(productListAdapter);
      productListAdapter.getCount();
  
      productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          String productName = productListAdapter.getItem(position).getName();
      
          // Open MenuFragment
        ProductContentFragment productContentFragment = ProductContentFragment.newInstance(productName);
        getActivity().getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_right, R.anim.exit_to_left)
            .replace(R.id.fragmentContainer, productContentFragment, "productContentFragment")
            .addToBackStack(null)
            .commit();
        }
      });
    } else {
      productListView.setVisibility(View.GONE);
      warningTextView.setVisibility(View.VISIBLE);
    }
  }
  
  private class ProductListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Product> products;
    
    ProductListAdapter(Activity activity, ArrayList<Product> products) {
      inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      this.products = products;
    }
    
    @Override
    public int getCount() {
      return products.size();
    }
    
    @Override
    public Product getItem(int position) {
      return products.get(position);
    }
    
    @Override
    public long getItemId(int position) {
      return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = inflater.inflate(R.layout.list_row_menu_activity, null);
      TextView productNameTextView = view.findViewById(R.id.titleTextView);
      CircleImageView productImage = view.findViewById(R.id.circleImageView);
      
      String productName = products.get(position).getName();
      String productImagePath = products.get(position).getCoverImage();
      
      productNameTextView.setText(productName);
      productImage.setImage(productImagePath);
      productImage.setBorderColor(Color.WHITE);
      productImage.invalidate();
      return view;
    }
  }
  
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
  }
}
