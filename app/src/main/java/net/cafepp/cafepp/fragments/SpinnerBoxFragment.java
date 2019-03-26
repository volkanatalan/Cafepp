package net.cafepp.cafepp.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.interfaces.SpinnerBoxItemClick;

import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;

public class SpinnerBoxFragment extends Fragment {
  
  private View v;
  private ArrayList<Pair<String, String>> items;
  private ArrayList<Pair<String, String>> selectedItems;
  private ArrayList<Pair<String, String>> ingredientPairList = new ArrayList<>();
  private String dTitle;
  private String searchBoxContent;
  private SpinnerBoxItemClick spinnerBoxItemClick;
  private Fragment newFragment;
  private CustomAdapter customAdapter;
  private ListView listView;
  private EditText searchBox;
  private LayoutInflater layoutInflater;
  private ArrayList<String> allItems;
  private String dataCode;
  private ProductDatabase productDatabase;
  
  public SpinnerBoxFragment() {
    // Required empty public constructor
  }
  
  
  public static SpinnerBoxFragment newInstance() {
    return new SpinnerBoxFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Activity activity = getActivity();
    productDatabase = new ProductDatabase(activity);
    layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    items = new ArrayList<>();
    selectedItems = new ArrayList<>();
    
    Bundle bundle2 = getArguments();
    if (bundle2 != null) {
      dTitle = bundle2.getString("spinnerBoxTitle");
      dataCode = bundle2.getString("DataCode");
    }
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    v = inflater.inflate(R.layout.fragment_spinner_box, container, false);
    v.setOnTouchListener(new View.OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
    return v;
  }
  
  @Override
  public void onResume() {
    super.onResume();
  
    ArrayList<String> chosenItems = productDatabase.getChosenItems(dataCode);
    ArrayList<String> products = productDatabase.getProductNames();
    
    RuleBasedCollator collator = null;
    String turkishCyrillic = "<0<1<2<3<4<5<6<7<8<9< A,a< B,b< C,c< Ç,ç< D,d< E,e< F,f< G,g< Ğ,ğ< H,h" +
                                 "< I,ı< İ,i< J,j< K,k< L,l< M,m< N,n< O,o< Ö,ö< P,p< Q,q< R,r< S,s<" +
                                 "Ş,ş< T,t< U,u< Ü,ü< V,v< W,w< X,x< Y,y< Z,z< А,а< Б,б< В,в< Г,г< Д" +
                                 ",д< Е,е< Ё,ё< Ж,ж< И,и< Й,й< К,к< Л,л< М,м< Н,н< О,о< П,п< Р,р< С," +
                                 "с< Т,т< У,у< Ф,ф< Х,х< Ц,ц< Ч,ч< Ш,ш< Щ,щ< Ъ,ъ< Ы,ы< Ь,ь< Э,э< Ю,ю" +
                                 "< Я,я";
    try {
      collator = new RuleBasedCollator(turkishCyrillic);
    } catch (ParseException e) { e.printStackTrace(); }
    
    if (dataCode.equals("category")) {
      allItems = productDatabase.getCategoryNames();
      Collections.sort(allItems, collator);
      ingredientPairList.clear();
      for (String s : allItems) ingredientPairList.add(new Pair<>(s, "category"));
    } else if (dataCode.equals("ingredient")) {
      allItems = productDatabase.getIngredientNames();
      Collections.sort(allItems, collator);
      Collections.sort(products, collator);
      ingredientPairList.clear();
      for (String s : allItems) ingredientPairList.add(new Pair<>(s, "ingredient"));
      for (String s : products) ingredientPairList.add(new Pair<>(s, "product"));
    }
    
    for (int i = 0; i < ingredientPairList.size(); i++) {
      if (chosenItems.contains(ingredientPairList.get(i).first)){
        ingredientPairList.remove(i);
        i--;
      }
    }
    
    final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
        "net.cafepp.cafepp", Context.MODE_PRIVATE);
    String newItem = sharedPreferences.getString("newItem","");
    
    if(!newItem.equals("")){
      Pair<String,String> newItemPair = new Pair<>(newItem, dataCode);
      selectedItems.add(newItemPair);
      sharedPreferences.edit().putString("newItem","").apply();
      
      ingredientPairList.remove(newItemPair);
      ArrayList<Pair<String, String>> tempPairList = new ArrayList<>();
      tempPairList.add(newItemPair);
      tempPairList.addAll(ingredientPairList);
      ingredientPairList.clear();
      ingredientPairList.addAll(tempPairList);
    }
    
    items.clear();
    items.addAll(ingredientPairList);
    
    TextView doneButton = v.findViewById(R.id.doneButton);
    TextView title = v.findViewById(R.id.spinerBaslikTextView);
    title.setText(dTitle);
    listView = v.findViewById(R.id.spinnerList);
    searchBox = v.findViewById(R.id.aramaKutusu);
    Button addnewButton = v.findViewById(R.id.addNew);
    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    listView.setItemsCanFocus(false);
    customAdapter = new CustomAdapter();
    listView.setAdapter(customAdapter);
    searchBox.setText("");
    searchBox.requestFocus();
    
    addnewButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        searchBoxContent = searchBox.getText().toString();
        
        Bundle bundle3 = new Bundle();
        bundle3.putStringArrayList("listItems", allItems);
        bundle3.putString("search_box_content", searchBoxContent);
        
        if (newFragment == null) {
          if (dataCode.equals("category")) {
            newFragment = new NewCategoryFragment();
          } else if (dataCode.equals("ingredient")) {
            newFragment = new NewIngredientFragment();
          }
        }
        newFragment.setArguments(bundle3);
        
        getFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, newFragment,"addNew")
            .addToBackStack("addNew")
            .commit();
      }
    });
    
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
        Pair<String, String> clickedItem = customAdapter.getItem(position);
        if (selectedItems.contains(clickedItem)) {
          selectedItems.remove(clickedItem);
        } else {
          selectedItems.add(clickedItem);
        }
      }
    });
    
    searchBox.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      
      }
      
      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      
      }
      
      @Override
      public void afterTextChanged(Editable editable) {
        customAdapter.getFilter().filter(searchBox.getText().toString());
      }
    });
    
    
    doneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        for(int i=0; i<selectedItems.size(); i++) {
          //databaseAddedItems.addItem(dataCode, selectedItems.get(i).second, selectedItems.get(i).first);
          spinnerBoxItemClick.onClick(selectedItems.get(i).first, selectedItems.get(i).second, i);
        }
        
        sharedPreferences.edit().putString("newItem","").apply();
        
        
        // Hide keyboard
        View view = getActivity().getCurrentFocus();
        if (view != null) {
          InputMethodManager inputMethodManager =
              (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
          if (inputMethodManager!=null)
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        
        // Press back button to exit
        getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
      }
    });
  }
  
  public void bindOnSpinerListener(SpinnerBoxItemClick spinnerBoxItemClick) {
    this.spinnerBoxItemClick = spinnerBoxItemClick;
  }

  /*public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }*/

//  @Override
//  public void onAttach(Context context) {
//    super.onAttach(context);
//    if (context instanceof OnFragmentInteractionListener) {
//      mListener = (OnFragmentInteractionListener) context;
//    } else {
//      throw new RuntimeException(context.toString()
//              + " must implement OnFragmentInteractionListener");
//    }
//  }
//
//  @Override
//  public void onDetach() {
//    super.onDetach();
//    OnFragmentInteractionListener mListener = null;
//  }
//
//  public interface OnFragmentInteractionListener {
//    // Update argument type and name
//    void onFragmentInteraction(Uri uri);
//  }
  
  public void setNewFragment (Fragment fragment){ this.newFragment = fragment; }
  
  
  
  // SpinnerBox ListView adapter
  public class CustomAdapter extends BaseAdapter implements Filterable {
    
    @Override
    public int getCount() { return items.size(); }
    
    @Override
    public Pair<String, String> getItem(int position) {
      return items.get(position);
    }
    
    @Override
    public long getItemId(int position) {
      return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = layoutInflater.inflate(R.layout.list_item_spinner_box, null);
      CheckedTextView checkedTextView = view.findViewById(R.id.text1);
      checkedTextView.setText(items.get(position).first);
      if (items.get(position).second.equals("product")) checkedTextView.setTypeface(checkedTextView.getTypeface(), Typeface.BOLD);
      else checkedTextView.setTypeface(checkedTextView.getTypeface(), Typeface.NORMAL);
      listView.setItemChecked(position, false);
      
      if(selectedItems.contains(items.get(position))){
        listView.setItemChecked(position, true);
      }
      return view;
    }
    
    
    ArrayList<Pair<String,String>> arrayListNames;
    
    @Override
    public Filter getFilter() {
      
      Filter filter = new Filter() {
        
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          items = (ArrayList<Pair<String,String>>) results.values;
          notifyDataSetChanged();
        }
        
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
          FilterResults results = new FilterResults();
          ArrayList<Pair<String,String>> FilteredArrayNames = new ArrayList<>();
          
          if (arrayListNames == null)    {
            arrayListNames = new ArrayList<>(ingredientPairList);
          }
          if (constraint == null || constraint.length() == 0) {
            results.count = arrayListNames.size();
            results.values = arrayListNames;
          } else {
            constraint = constraint.toString().toLowerCase();
            for (int i = 0; i < arrayListNames.size(); i++) {
              Pair<String,String> dataNames = arrayListNames.get(i);
              if (dataNames.first.toLowerCase().startsWith(constraint.toString()))  {
                FilteredArrayNames.add(dataNames);
              }
            }
            results.count = FilteredArrayNames.size();
            results.values = FilteredArrayNames;
          }
          return results;
        }
      };
      return filter;
    }
  }
  
}
