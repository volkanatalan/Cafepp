package net.cafepp.cafepp.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.activities.MenuActivity;
import net.cafepp.cafepp.custom_views.CircleImageView;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.objects.Product;

import java.util.ArrayList;

public class SearchProductFragment extends Fragment {
  
  private static final String SEARCH_TEXT = "searchText";
  private String searchText;
  private ListView searchListView;
  private SearchListAdapter searchListAdapter;
  private ProductDatabase productDatabase;
  private ArrayList<Product> allProducts;
  private ArrayList<Product> products;
  
  public SearchProductFragment() {
    // Required empty public constructor
  }
  
  public static SearchProductFragment newInstance(String searchText) {
    SearchProductFragment fragment = new SearchProductFragment();
    Bundle args = new Bundle();
    args.putString(SEARCH_TEXT, searchText);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    productDatabase = new ProductDatabase(getContext());
    products = new ArrayList<>();
    
    if (getArguments() != null) {
      searchText = getArguments().getString(SEARCH_TEXT);
      allProducts = productDatabase.getAllProducts();
    }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_search_product, container, false);
    searchListView = view.findViewById(R.id.searchListView);
    searchListAdapter = new SearchListAdapter(getActivity(), products);
    searchListView.setAdapter(searchListAdapter);
    searchListAdapter.getCount();
  
    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String productName = searchListAdapter.getItem(position).getName();
        
        // Clean search box and move down
        ((MenuActivity) getActivity()).clearSearchEditText();
        ((MenuActivity) getActivity()).onClickSearchButton(null);
        
        // Pop back to CategoriesFragment
        getFragmentManager().popBackStackImmediate(
            "categories", FragmentManager.POP_BACK_STACK_INCLUSIVE);
      
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
    
    return view;
  }
  
  public void onTextChanged(String text) {
    products = getProductsFromSearch(text);
    searchListAdapter.products = products;
    searchListAdapter.notifyDataSetChanged();
  }
  
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
  }
  
  public ArrayList<Product> getProductsFromSearch(String search) {
    ArrayList<Product> products = new ArrayList<>();
    
    if (!search.equals("")) {
      for (Product productName : allProducts) {
        if (productName.getName().toLowerCase().contains(search.toLowerCase())) {
          products.add(productName);
        }
      }
    } else {
      products.clear();
    }
    
    return products;
  }
  
  private class SearchListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Product> products;
  
    SearchListAdapter(Activity activity, ArrayList<Product> products) {
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
}
