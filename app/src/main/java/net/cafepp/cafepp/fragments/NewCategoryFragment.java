package net.cafepp.cafepp.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.custom_views.CircleImage;
import net.cafepp.cafepp.databases.ProductDatabase;
import net.cafepp.cafepp.models.Category;
import net.cafepp.cafepp.models.Save;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class NewCategoryFragment extends Fragment {
  private OnFragmentInteractionListener mListener;
  EditText categoryName;
  ImageView categoryImage;
  Button doneButton;
  ProductDatabase productDatabase;
  String catImName;
  String catImgPath;
  Uri image;
  Bitmap bitmap;
  Save save;
  ArrayList<String> items;
  ArrayList<String> items2;
  String searchBoxContent;
  LinearLayout categoryImageLinearLayout;
  Bundle bundle4;
  SpinnerBoxFragment spinnerBoxFragment;
  int READ_EXTERNAL_STORAGE_REQUEST = 916;
  int CHOOSE_IMAGE = 654;
  
  
  public NewCategoryFragment() {
    // Required empty public constructor
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    items = new ArrayList<>();
    items2 = new ArrayList<>();
    spinnerBoxFragment = new SpinnerBoxFragment();
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    if (searchBoxContent != null) {
      categoryName.setText(searchBoxContent);
      categoryName.setSelection(categoryName.getText().length());
    }
  }
  
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    bundle4 = getArguments();
    if (bundle4 != null) {
      items = bundle4.getStringArrayList("listItems");
      searchBoxContent = bundle4.getString("search_box_content");
    }
  
    productDatabase = new ProductDatabase(getActivity());
    View view = inflater.inflate(R.layout.fragment_add_category_name, container, false);
    categoryName = view.findViewById(R.id.titleTextView);
    categoryImage = view.findViewById(R.id.circleImageView);
    doneButton = view.findViewById(R.id.doneButton);
    categoryImageLinearLayout = view.findViewById(R.id.categoryImageLinearL);
  
    save = new Save(getActivity());
  
    view.setOnTouchListener(new View.OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
  
    categoryImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if(getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
          
          }else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,CHOOSE_IMAGE);
          }
        }else{
          Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(intent,CHOOSE_IMAGE);
        }
      }
    });
  
    doneButton.setOnClickListener(new View.OnClickListener(){
    
      @Override
      public void onClick(View v) {
        String categoryNametoString = categoryName.getText().toString();
        if (!"".equals(categoryNametoString) && bitmap != null){
          // Galeriden alınan resmi program dosyasına kaydet
          save.toInternalStorage(bitmap,"CategoryImages");
          catImgPath = save.getImagePath();
          catImName = save.getImageName();
          String imgPathofCatImage = save.fullPath;
        
          // Save the category name and the image to database
          Category category = new Category(0, null, categoryNametoString, imgPathofCatImage);
          productDatabase.addChosenCategory(category);
        
          SharedPreferences sharedPreferences = getActivity().getSharedPreferences("net.cafepp.cafepp", Context.MODE_PRIVATE);
          sharedPreferences.edit().putString("newItem", categoryNametoString).apply();
        
          // Add new category name to items arraylist
          items2.add(categoryNametoString);
          items2.addAll(items);
          items.clear();
          items.addAll(items2);
        
          categoryName.setText("");
          bitmap = null;
        
          getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
          getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        
        }
        else if("".equals(categoryNametoString)) {
          Toast.makeText(getActivity(), "Category name can't be blank.", Toast.LENGTH_SHORT).show();
        
        }else if(bitmap == null){
          Toast.makeText(getActivity(), "Please, choose a category image.", Toast.LENGTH_SHORT).show();
        
        }else{
          Toast.makeText(getActivity(), "Please, type a name and choose an image.", Toast.LENGTH_SHORT).show();
        }
      }
    });
  
    return view;
  }
  
  
  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  /*@Override
  public void onAttach(Context context) {
      super.onAttach(context);
      if (context instanceof OnFragmentInteractionListener) {
          mListener = (OnFragmentInteractionListener) context;
      } else {
          throw new RuntimeException(context.toString()
                  + " must implement OnFragmentInteractionListener");
      }
  }*/
  
  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
  
  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode == READ_EXTERNAL_STORAGE_REQUEST){
      if(grantResults.length > 0 && grantResults[0] == getActivity().getPackageManager().PERMISSION_GRANTED){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,CHOOSE_IMAGE);
      }
    }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null){
      searchBoxContent = categoryName.getText().toString();
      
      categoryImageLinearLayout.setBackground(null);
      
      image = data.getData();
      Bitmap bmp = null;
      try {
        bmp = getBitmapFromUri(image);
      } catch (IOException e) {
        e.printStackTrace();
      }
      categoryImage.setImageBitmap(bmp);
      CircleImage circleImage2 = new CircleImage(getActivity(), bmp, R.color.bordo);
      Drawable drawable = circleImage2.getDrawable();
      categoryImage.setImageDrawable(drawable);
      categoryImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
      
      try {
        
        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
        
      } catch (IOException e) { e.printStackTrace(); }
      
      super.onActivityResult(requestCode, resultCode, data);
    }
    
    
  }
  
  private Bitmap getBitmapFromUri(Uri uri) throws IOException {
    ParcelFileDescriptor parcelFileDescriptor =
        getActivity().getContentResolver().openFileDescriptor(uri, "r");
    FileDescriptor fileDescriptor = null;
    if (parcelFileDescriptor != null) fileDescriptor = parcelFileDescriptor.getFileDescriptor();
    Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
    if (parcelFileDescriptor != null) parcelFileDescriptor.close();
    return image;
  }
  
}
