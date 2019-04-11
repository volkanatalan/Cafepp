package net.cafepp.cafepp.activities;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.custom_views.LockableScrollView;
import net.cafepp.cafepp.custom_views.NonScrollableListView;
import net.cafepp.cafepp.custom_views.OnTouchSwipeListener;
import net.cafepp.cafepp.custom_views.ThreeStateCheckBox;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.fragments.CalculateCalorieFragment;
import net.cafepp.cafepp.fragments.NewCategoryFragment;
import net.cafepp.cafepp.fragments.NewIngredientFragment;
import net.cafepp.cafepp.fragments.SpinnerBoxFragment;
import net.cafepp.cafepp.interfaces.SpinnerBoxItemClick;
import net.cafepp.cafepp.objects.ChosenIngredient;
import net.cafepp.cafepp.objects.DecimalPlaceValue;
import net.cafepp.cafepp.objects.Save;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Collections;

public class AddProductActivity extends AppCompatActivity {
  
  Activity mActivity;
  Context mContext;
  Bitmap mBitmap;
  ProductDatabase mProductDatabase;
  Save mSave = new Save(this);
  List<String> mCategoryNames;
  ArrayList<String> mChosenCategories = new ArrayList<>();
  ArrayList<String> mChosenIngredientNames = new ArrayList<>();
  EditText mProductNameET;
  EditText mPriceET;
  EditText mDescriptionET;
  LayoutInflater mInflaterChosenCategories;
  LayoutInflater mInflaterChosenIngredients;
  AdapterRecycleView adapterRecycleView;
  ChosenCategoriesAdapter chosenCategoriesAdapter;
  public ChosenIngredientsAdapter chosenIngredientsAdapter;
  NonScrollableListView listview;
  NonScrollableListView listView2;
  View kategorilerView;
  String turkish;
  RuleBasedCollator turkishCollator;
  ThreeStateCheckBox showAllCBTS;
  LinearLayout lLUnderShow;
  LockableScrollView scrollView;
  public EditText calorie;
  public EditText kgSumET;
  List<String> imagesEncodedList;
  ArrayList<Bitmap> bitmapList = new ArrayList<>();
  ArrayList<Uri> mArrayUri = new ArrayList<>();
  RecyclerView recyclerView;
  Bundle bundle;
  NewCategoryFragment newCategoryFragment;
  SpinnerBoxFragment spinnerBoxFragment;
  CustomLinearLayoutManager customLinearLayoutManager;
  int CHOOSE_MULTIPLE_IMAGES = 165;
  int READ_EXTERNAL_STORAGE_REQ = 843;
  boolean showAllIngredients = true;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_product);
    mActivity = this;
    mContext = this;
    turkish = "<0<1<2<3<4<5<6<7<8<9< A,a< B,b< C,c< Ç,ç< D,d< E,e< F,f< G,g< Ğ,ğ< H,h< I,ı< İ,i< J,j" +
                  "< K,k< L,l< M,m< N,n< O,o< Ö,ö< P,p< Q,q< R,r< S,s< Ş,ş< T,t< U,u< Ü,ü" +
                  "V,v< W,w< X,x< Y,y< Z,z";
    try { turkishCollator = new RuleBasedCollator(turkish); }
    catch (ParseException e) { e.printStackTrace();
    }
    mProductDatabase = new ProductDatabase(this);
    mProductNameET = findViewById(R.id.productName);
    mPriceET = findViewById(R.id.price);
    mDescriptionET =  findViewById(R.id.description);
    calorie = findViewById(R.id.productCalorieET);
    kgSumET = findViewById(R.id.kgSumET);
    showAllCBTS = findViewById(R.id.showAllCBTS);
    lLUnderShow = findViewById(R.id.LLUnderShow);
    scrollView = findViewById(R.id.scrollView);
    TextView idealResolution = findViewById(R.id.idealImageResolution);
    
    listview = findViewById(R.id.addedCategoriesListView);
    listview.setItemsCanFocus(true);
    listview.setFocusable(false);
    chosenCategoriesAdapter = new ChosenCategoriesAdapter(this, mChosenCategories);
    listview.setAdapter(chosenCategoriesAdapter);
    
    listView2 = findViewById(R.id.addedIngredientsListView);
    listView2.setItemsCanFocus(true);
    listView2.setFocusable(false);
    chosenIngredientsAdapter = new ChosenIngredientsAdapter(this, mChosenIngredientNames);
    listView2.setAdapter(chosenIngredientsAdapter);
    
    recyclerView = findViewById(R.id.recyclerView);
    customLinearLayoutManager = new CustomLinearLayoutManager(this);
    customLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(customLinearLayoutManager);
    adapterRecycleView = new AdapterRecycleView(bitmapList, mArrayUri);
    recyclerView.setAdapter(adapterRecycleView);
    
    int srcWidth = getScreenDimensionX();
    int imageHeight = (srcWidth / 7) * 4;
    
    idealResolution.setText("Ideal image resolution: " + srcWidth + "(W) x " + imageHeight + "(H)");
    DecimalPlaceValue decimalPlaceValue = new DecimalPlaceValue(mPriceET, " ", 2);
    
    mProductDatabase.clearChosenItems();
    
    showAllCBTS.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clearFocus();
        int state = ((ThreeStateCheckBox) v).getState();
        switch (state) {
          case 1:
            mProductDatabase.setAllIngredientsShow(true);
            break;
          
          case 0:
            mProductDatabase.setAllIngredientsShow(false);
            break;
        }
        chosenIngredientsAdapter.notifyDataSetChanged();
      }
    });
  }
  
  
  @Override
  protected void onResume() {
    super.onResume();
    
    adapterRecycleView.notifyDataSetChanged();
    
    if (bitmapList.size() == 0){
      recyclerView.setVisibility(View.GONE);
    }else {
      recyclerView.setVisibility(View.VISIBLE);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.done_only, menu);
    return super.onCreateOptionsMenu(menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_done:
        doneButtonOnClick();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  void clearFocus() {
    // Hide keyboard
    View view = getWindow().getCurrentFocus();
    if (view != null) {
      InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      if (inputMethodManager != null) {
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    }
    
    // Clear focus
    getWindow().getCurrentFocus().clearFocus();
  }
  
  public void AddImageButtonOnClick(View view){
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQ);
      }
      else {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), CHOOSE_MULTIPLE_IMAGES);
      }
    }
    else {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
      intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
      startActivityForResult(Intent.createChooser(intent, "Select Pictures"), CHOOSE_MULTIPLE_IMAGES);
    }
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode == READ_EXTERNAL_STORAGE_REQ){
      if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
      {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), CHOOSE_MULTIPLE_IMAGES);
      }
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == CHOOSE_MULTIPLE_IMAGES && resultCode == RESULT_OK && data != null){
      
      imagesEncodedList = new ArrayList<>();
      if(data.getData()!= null){
        
        Uri mImageUri = data.getData();
        mArrayUri.add(mImageUri);
        Bitmap mbitmap = null;
        try {
          mbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
        } catch (IOException e) {
          e.printStackTrace();
        }
        
        if (mbitmap != null) {
          bitmapList.add(mbitmap);
        }
        
      } else {
        if (data.getClipData() != null) {
          ClipData mClipData = data.getClipData();
          
          for (int i = 0; i < mClipData.getItemCount(); i++) {
            ClipData.Item item = mClipData.getItemAt(i);
            Uri uri = item.getUri();
            mArrayUri.add(uri);
            try { mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) { e.printStackTrace(); }
            
            bitmapList.add(mBitmap);
            adapterRecycleView.notifyDataSetChanged();
          }
        }
      }
    }
  }
  
  
  int screenwidth;
  public int getScreenDimensionX() {
    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    screenwidth = size.x;
    return screenwidth;
  }
  
  
  public class AdapterRecycleView extends RecyclerView.Adapter<AdapterRecycleView.ViewHolder>{
    ArrayList<Bitmap> bitmapList;
    ArrayList<Uri> mArrayUri;
    int SELECTED_COLOR = Color.YELLOW;
    int UNSELECTED_COLOR = Color.WHITE;
    int selectedImagePosition = 0;
    int revealPos = -1;
    
    class ViewHolder extends RecyclerView.ViewHolder {
      CardView cv;
      TextView photoName;
      TextView deleteTextview;
      ImageView productPhoto;
      ImageView deleteImage;
      ImageView dragIV;
      RelativeLayout surfaceLayout;
      
      ViewHolder(View itemView) {
        super(itemView);
        cv = itemView.findViewById(R.id.imagesCardView);
        photoName = itemView.findViewById(R.id.photo_name);
        deleteTextview = itemView.findViewById(R.id.delete_textview);
        productPhoto = itemView.findViewById(R.id.product_photo);
        deleteImage = itemView.findViewById(R.id.delete);
        dragIV = itemView.findViewById(R.id.drag);
        surfaceLayout = itemView.findViewById(R.id.surfaceLayout);
      }
    }
    
    AdapterRecycleView(ArrayList<Bitmap> bitmapList, ArrayList<Uri> mArrayUri){
      this.bitmapList = bitmapList;
      this.mArrayUri = mArrayUri;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_add_product_image,
          parent, false);
      return new ViewHolder(v);
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
      /*holder.surfaceLayout.animate()
          .x(0)
          .setDuration(0)
          .start();*/
      holder.productPhoto.setImageBitmap(bitmapList.get(position));
      //holder.photoName.setText(mArrayUri.get(position).getLastPathSegment());
      File file = new File(getPath(mArrayUri.get(position)));
      holder.photoName.setText(file.toString());
      
      if (selectedImagePosition ==position)
        holder.surfaceLayout.setBackgroundColor(SELECTED_COLOR);
      else
        holder.surfaceLayout.setBackgroundColor(UNSELECTED_COLOR);
      
      OnTouchSwipeListener onTouchSwipeListener = new OnTouchSwipeListener(holder.dragIV, holder.deleteTextview){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          return super.onTouch(v, event);
        }
        
        @Override
        public void onActionUp() {
          customLinearLayoutManager.setScrollEnabled(true);
          scrollView.setScrollingEnabled(true);
        }
        
        @Override
        public void onMove() {
          customLinearLayoutManager.setScrollEnabled(false);
          scrollView.setScrollingEnabled(false);
          if (revealPos != -1 && revealPos != position && revealPos < getItemCount()) {
            RelativeLayout surface = ((RelativeLayout) ((ViewGroup) ((ViewGroup) customLinearLayoutManager.findViewByPosition(revealPos)).getChildAt(0)).getChildAt(1));
            onCover(surface);
          }
        }
        
        @Override
        public void onRevealRightSide(View v) {
          super.onRevealRightSide(v);
          revealPos = position;
        }
        
        @Override
        public void onRevealLeftSide(View v) {
          super.onRevealLeftSide(v);
          revealPos = position;
        }
        
        @Override
        public void onSurfaceClick(View v) {
          RelativeLayout clickedSurface = ((RelativeLayout) ((ViewGroup) ((ViewGroup) customLinearLayoutManager.findViewByPosition(position)).getChildAt(0)).getChildAt(1));
          RelativeLayout previousSelectedSurface = ((RelativeLayout) ((ViewGroup) ((ViewGroup) customLinearLayoutManager.findViewByPosition(selectedImagePosition)).getChildAt(0)).getChildAt(1));
          
          if (selectedImagePosition != position) {
            clickedSurface.setBackgroundColor(SELECTED_COLOR);
            previousSelectedSurface.setBackgroundColor(UNSELECTED_COLOR);
            selectedImagePosition = position;
          }
          
          if (revealPos != -1) {
            RelativeLayout closeSurface = ((RelativeLayout) ((ViewGroup) ((ViewGroup) customLinearLayoutManager.findViewByPosition(revealPos)).getChildAt(0)).getChildAt(1));
            onCover(closeSurface);
            revealPos = -1;
          }
        }
      };
      holder.surfaceLayout.setOnTouchListener(onTouchSwipeListener);
      onTouchSwipeListener.onCover(holder.surfaceLayout);
      
      holder.deleteImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
          TranslateAnimation removeAnimation = new TranslateAnimation(0,holder.cv.getWidth()*-1,0,0);
          removeAnimation.setDuration(300);
          holder.cv.startAnimation(removeAnimation);
          removeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
              bitmapList.remove(position);
              mArrayUri.remove(position);
              if (position < selectedImagePosition)
                selectedImagePosition--;
              else if (selectedImagePosition >= getItemCount())
                selectedImagePosition = getItemCount()==0 ? 0 : getItemCount()-1;
              notifyDataSetChanged();
            }
          });
        }
      });
    }
    
    @Override
    public int getItemCount() {
      return bitmapList.size();
    }
    
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
      super.onAttachedToRecyclerView(recyclerView);
    }
    
    int getSelectedImagePosition() {
      return selectedImagePosition;
    }
    
    
    String getPath(Uri uri) {
      String fileName = "";
      if (uri.getScheme().equals("file")) {
        fileName = uri.getLastPathSegment();
      } else {
        Cursor cursor = null;
        try {
          cursor = getContentResolver().query(uri, new String[]{
              MediaStore.Images.ImageColumns.DISPLAY_NAME
          }, null, null, null);
          
          if (cursor != null && cursor.moveToFirst()) {
            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
          }
        } finally {
          
          if (cursor != null) {
            cursor.close();
          }
        }
      }
      return fileName;
    }
  }
  
  
  public class ChosenCategoriesAdapter extends BaseAdapter{
    ChosenCategoriesAdapter(Activity activity, List<String> kategoriIsmi) {
      mInflaterChosenCategories = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      mCategoryNames = kategoriIsmi;
    }
    
    @Override
    public int getCount() {
      return mCategoryNames.size();
    }
    
    @Override
    public String getItem(int position) {
      return mCategoryNames.get(position);
    }
    
    @Override
    public long getItemId(int position) {
      return position;
    }
    
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      kategorilerView = mInflaterChosenCategories.inflate(R.layout.list_row_add_product, null);
      final LinearLayout baseLayout = kategorilerView.findViewById(R.id.baseLayout);
      TextView textView = kategorilerView.findViewById(R.id.listItemAddProductTV);
      ImageView imageView = kategorilerView.findViewById(R.id.listItemAddProductDelete);
      
      textView.setText(mChosenCategories.get(position));
      
      imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          TranslateAnimation removeAnimation = new TranslateAnimation(0,baseLayout.getWidth() * -1,0,0);
          removeAnimation.setDuration(200);
          baseLayout.startAnimation(removeAnimation);
          removeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
              String deletedIngredient = mCategoryNames.get(position);
              mCategoryNames.remove(position);
              mProductDatabase.removeChosenItem("category", deletedIngredient);
              notifyDataSetChanged();
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            
            }
          });
        }
      });
      
      return kategorilerView;
    }
  }
  
  class ViewHolder {
    RelativeLayout baseLayoutRL;
    EditText amountET;
    TextView titleTV;
    TextView typeTV;
    ImageView deleteIV;
    CheckBox showCB;
  }
  
  public class ChosenIngredientsAdapter extends BaseAdapter{
    ViewHolder holder;
    ArrayList<String> ingredientNames;
    ArrayList<ChosenIngredient> chosenIngredients;
    
    ChosenIngredientsAdapter(Activity activity, ArrayList<String> ingredientNames) {
      mInflaterChosenIngredients = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      this.ingredientNames = ingredientNames;
    }
    
    @Override
    public int getCount() {
      return ingredientNames.size();
    }
    
    @Override
    public String getItem(int position) {
      return ingredientNames.get(position);
    }
    
    ArrayList<ChosenIngredient> getItems() {
      return chosenIngredients;
    }
    
    @Override
    public long getItemId(int position) {
      return position;
    }
    
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      chosenIngredients = mProductDatabase.getChosenIngredients(ingredientNames);
      String EMAamount = mProductDatabase.getChosenItemAmount(chosenIngredients.get(position).getName())==null ?
                             "" : mProductDatabase.getChosenItemAmount(chosenIngredients.get(position).getName());
      boolean EMAshow = chosenIngredients.get(position).isShow();
      String EMAunitType = chosenIngredients.get(position).getAmountType();
      String EMAunitShort = "";
      
      switch (EMAunitType) {
        case "kilogram":
          EMAunitShort = "gr";
          break;
        case "liter":
          EMAunitShort = "ml";
          break;
        case "piece":
          EMAunitShort = "pcs";
          break;
      }
      
      if (chosenIngredients.get(position).getIngredientType().equals("product"))
        EMAunitShort = "piece";
      
      holder = new ViewHolder();
      convertView = mInflaterChosenIngredients.inflate(R.layout.list_row_add_ingredient, null);
      holder.baseLayoutRL = convertView.findViewById(R.id.baseLayout);
      holder.showCB = convertView.findViewById(R.id.checkBox);
      holder.amountET = convertView.findViewById(R.id.amountET);
      holder.titleTV = convertView.findViewById(R.id.listItemAddProductTV);
      holder.deleteIV = convertView.findViewById(R.id.ingredientListItemDelete);
      holder.typeTV = convertView.findViewById(R.id.typeTV);
      convertView.setTag(holder);
      
      holder.titleTV.setText(chosenIngredients.get(position).getName());
      holder.amountET.setText(EMAamount);
      holder.showCB.setChecked(EMAshow);
      holder.typeTV.setText(EMAunitShort);
      holder.amountET.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
        }
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        
        }
        
        @Override
        public void afterTextChanged(Editable s) {
          chosenIngredients.get(position).setAmount(s.toString().equals("") ? -1f : Float.valueOf(s.toString()));
          mProductDatabase.setChosenIngredientAmount(chosenIngredients.get(position).getName(), s.toString());
        }
      });
      
      holder.showCB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mProductDatabase.setChosenIngredientShow(chosenIngredients.get(position).getName(), ((CheckBox) v).isChecked());
          showAllCBTS.setState(-1);
        }
      });
      
      holder.deleteIV.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          TranslateAnimation removeAnimation = new TranslateAnimation(0, holder.baseLayoutRL.getWidth() * -1, 0, 0);
          removeAnimation.setDuration(200);
          ((RelativeLayout)v.getParent()).startAnimation(removeAnimation);
          removeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
              clearFocus();
              String deletedIngredient = ingredientNames.get(position);
              mProductDatabase.removeChosenItem("ingredient", deletedIngredient);
              ingredientNames.remove(position);
              notifyDataSetChanged();
              
              if(ingredientNames.size()==0){
                showAllCBTS.setVisibility(View.GONE);
                lLUnderShow.setVisibility(View.GONE);
                showAllCBTS.setState(1);
                showAllIngredients = true;
              }
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            
            }
          });
        }
      });
      return convertView;
    }
  }
  
  public void chooseCategoriesSpinnerBoxOnClick(View view){
    spinnerBoxFragment = new SpinnerBoxFragment();
    
    if (bundle != null) bundle.clear();
    bundle = new Bundle();
    bundle.putString("spinnerBoxTitle", "Select a categories");
    bundle.putString("DataCode", "category");
    spinnerBoxFragment.setArguments(bundle);
    
    newCategoryFragment = new NewCategoryFragment();
    spinnerBoxFragment.setNewFragment(newCategoryFragment);
    
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragmentContainer, spinnerBoxFragment,"spinnerBoxFragment")
        .addToBackStack("spinnerBoxFragment")
        .commit();
    
    spinnerBoxFragment.bindOnSpinerListener(new SpinnerBoxItemClick() {
      @Override
      public void onClick(String item, String type, int position) {
        mProductDatabase.addChosenCategory(item);
        mChosenCategories.add(item);
        Collections.sort(mChosenCategories, turkishCollator);
        chosenCategoriesAdapter.notifyDataSetChanged();
      }
    });
  }
  
  public void ChooseIngredientsSpinnerBoxOnClick (final View view){
    SpinnerBoxFragment spinnerBoxFragment = new SpinnerBoxFragment();
    
    Bundle bundle = new Bundle();
    bundle.putString("spinnerBoxTitle", "Select an ingredient");
    bundle.putString("DataCode", "ingredient");
    spinnerBoxFragment.setArguments(bundle);
    
    NewIngredientFragment newIgredientAddFragment = new NewIngredientFragment();
    spinnerBoxFragment.setNewFragment(newIgredientAddFragment);
    
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragmentContainer, spinnerBoxFragment,"spinnerBoxFragment")
        .addToBackStack("spinnerBoxFragment")
        .commit();
    
    spinnerBoxFragment.bindOnSpinerListener(new SpinnerBoxItemClick() {
      @Override
      public void onClick(String item, String type, int position) {
        mProductDatabase.addChosenIngredient(item, type);
        mChosenIngredientNames.add(item);
        Collections.sort(mChosenIngredientNames, turkishCollator);
        
        if(mChosenIngredientNames.size()==0){
          showAllCBTS.setVisibility(View.GONE);
          lLUnderShow.setVisibility(View.GONE);
        }else{
          showAllCBTS.setVisibility(View.VISIBLE);
          lLUnderShow.setVisibility(View.VISIBLE);
        }
        chosenIngredientsAdapter.notifyDataSetChanged();
      }
    });
  }
  
  public void calculateButtonOnClick(View view) {
    clearFocus();
    if (chosenIngredientsAdapter.getCount() > 0) {
      CalculateCalorieFragment calculateCalorieFragment = new CalculateCalorieFragment();
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragmentContainer, calculateCalorieFragment, "calculateCalorieFragment")
          .addToBackStack("spinnerBoxFragment")
          .commit();
    } else {
      Toast.makeText(this, "Please add ingredients.", Toast.LENGTH_SHORT).show();
    }
    
  }
  
  public void doneButtonOnClick() {
    
    if(mProductNameET.getText().toString().equals("") && mChosenCategories.size() == 0 && bitmapList.size() == 0) {
      Toast.makeText(this, "Please, choose a product name, a category and an image.", Toast.LENGTH_LONG).show();
    } else if (mProductNameET.getText().toString().equals("") && mChosenCategories.size() == 0){
      Toast.makeText(this, "Please, choose a product name and a category.", Toast.LENGTH_LONG).show();
    }else if (mChosenCategories.size() == 0 && bitmapList.size() == 0){
      Toast.makeText(this, "Please, choose a category and an image.", Toast.LENGTH_LONG).show();
    }else if (mProductNameET.getText().toString().equals("") && bitmapList.size() == 0){
      Toast.makeText(this, "Please, choose a product name and an image.", Toast.LENGTH_LONG).show();
    }else if (mProductNameET.getText().toString().equals("")){
      Toast.makeText(this, "Please, write a product name.", Toast.LENGTH_SHORT).show();
    }else if (mChosenCategories.size() == 0){
      Toast.makeText(this, "Please, choose a category.", Toast.LENGTH_SHORT).show();
    }else if (bitmapList.size() == 0){
      Toast.makeText(this, "Please, choose an image.", Toast.LENGTH_SHORT).show();
    } else if (mProductDatabase.getProductNames().contains(mProductNameET.getText().toString())) {
      Toast.makeText(this, "There is already a product with this name.", Toast.LENGTH_LONG).show();
    } else {
      clearFocus();
      String productName = this.mProductNameET.getText().toString();
      String fiyat;
      String aciklama = mDescriptionET.getText().toString();
      String weight = kgSumET.getText().toString();
      String kalori = calorie.getText().toString() + " kcal / " + weight + " gram";
      int chosenCoverPhoto = adapterRecycleView.getSelectedImagePosition();
      ArrayList<ChosenIngredient> chosenIngredientList = chosenIngredientsAdapter.getItems();
      
      if (!mPriceET.getText().toString().equals("")) {
        fiyat = mPriceET.getText().toString();
      } else {
        fiyat = "0";
      }
      
      for (int i = 0; i < bitmapList.size(); i++) {
        String resim = mSave.toInternalStorage(bitmapList.get(i), "ProductImages");
        mProductDatabase.addProductImage(productName, resim);
      }
      
      String coverPhoto = mProductDatabase.getProductImage(productName, chosenCoverPhoto);
      
      for (int i = 0; i < mChosenCategories.size(); i++) {
        String kategori = mChosenCategories.get(i);
        mProductDatabase.addProduct(kategori, productName, coverPhoto, fiyat, aciklama, kalori, weight);
      }
      
      if (chosenIngredientList != null) {
        for (int i = 0; i < chosenIngredientList.size(); i++) {
          int checkBoxValue = chosenIngredientList.get(i).isShow() ? 1 : 0;
          String ingredientName = chosenIngredientList.get(i).getName();
          String amount = chosenIngredientList.get(i).getAmount().toString();
          String amountType = chosenIngredientList.get(i).getAmountType();
          mProductDatabase.addIngredientToProduct(checkBoxValue, productName, ingredientName, amount, amountType);
        }
      }
      
      // Set visibility of some views
      showAllCBTS.setVisibility(View.GONE);
      lLUnderShow.setVisibility(View.GONE);
      
      // Clear the fields
      this.mProductNameET.setText("");
      bitmapList.clear();
      mArrayUri.clear();
      mPriceET.setText("");
      mDescriptionET.setText("");
      calorie.setText("");
      kgSumET.setText("");
      mChosenIngredientNames.clear();
      mChosenCategories.clear();
      mProductDatabase.clearChosenItems();
      chosenIngredientList.clear();
      adapterRecycleView.notifyDataSetChanged();
      chosenCategoriesAdapter.notifyDataSetChanged();
      chosenIngredientsAdapter.notifyDataSetChanged();
      
      // Give the massage that product successfully added
      Toast.makeText(this, productName + " has been added.", Toast.LENGTH_SHORT).show();
      
      // Scroll up
      // Wait until scrollView is ready
      scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          // Request focus to mProductNameET EditText
          AddProductActivity.this.mProductNameET.requestFocus();
          // Move up
          scrollView.fullScroll(View.FOCUS_UP);
          // Remove OnGlobalLayoutListener
          scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      });
    }
  }
  
  
  
  public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;
    
    public CustomLinearLayoutManager(Context context) {
      super(context);
    }
    
    public void setScrollEnabled(boolean flag) {
      this.isScrollEnabled = flag;
    }
    
    @Override
    public boolean canScrollVertically() {
      return isScrollEnabled && super.canScrollVertically();
    }
  }
}
