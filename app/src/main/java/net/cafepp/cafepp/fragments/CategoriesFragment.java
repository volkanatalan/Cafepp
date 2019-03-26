package net.cafepp.cafepp.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.custom_views.CircleImageView;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.models.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * {@link CategoriesFragment} contains a ListView, which is
 * consist of the categories of the products.
 */

public class CategoriesFragment extends Fragment {
  ListView categoryListView;
  FrameLayout fragmentBase;
  ArrayList<Category> categoryList;
  TextView warningTextView;
  
  public CategoriesFragment() {
    // Required empty public constructor
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_categories, container, false);
    fragmentBase = view.findViewById(R.id.fragmentBase);
    categoryListView = view.findViewById(R.id.categoryListView);
    warningTextView = view.findViewById(R.id.warningTextView);
    return view;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ProductDatabase productDatabase = new ProductDatabase(getContext());
    categoryList = productDatabase.getCategories();
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    if (categoryList.size() > 0) {
      final CategoryListAdapter categoryListAdapter = new CategoryListAdapter(getActivity(), categoryList);
      categoryListView.setAdapter(categoryListAdapter);
      categoryListAdapter.getCount();
  
      categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          String categoryName = categoryListAdapter.getItem(position).getCategoryName();
      
          // Open ProductsFragment
          ProductsFragment productsFragment = ProductsFragment.newInstance(categoryName);
          getActivity().getSupportFragmentManager().beginTransaction()
              .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                  R.anim.enter_from_right, R.anim.exit_to_left)
              .replace(R.id.fragmentContainer, productsFragment, "productsFragment")
              .addToBackStack("categories")
              .commit();
        }
      });
    } else {
      categoryListView.setVisibility(View.GONE);
      warningTextView.setVisibility(View.VISIBLE);
    }
  }
  
  private class CategoryListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Category> categories;
    
    CategoryListAdapter(Activity activity, ArrayList<Category> categories) {
      inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      this.categories = categories;
    }
    
    @Override
    public int getCount() {
      return categories.size();
    }
    
    @Override
    public Category getItem(int position) {
      return categories.get(position);
    }
    
    @Override
    public long getItemId(int position) {
      return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = inflater.inflate(R.layout.list_item_menu_activity, null);
      TextView categoryNameTextView = view.findViewById(R.id.titleTextView);
      CircleImageView categoryImage = view.findViewById(R.id.circleImageView);
      
      String categoryName = categories.get(position).getCategoryName();
      String categoryImagePath = categories.get(position).getCategoryImage();
      
      categoryNameTextView.setText(categoryName);
      categoryImage.setImage(categoryImagePath);
      categoryImage.setBorderColor(Color.WHITE);
      categoryImage.invalidate();
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