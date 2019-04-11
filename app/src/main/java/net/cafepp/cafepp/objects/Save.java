package net.cafepp.cafepp.objects;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Save {
  Activity activity;
  File directory;
  File checkFile;
  String imageName;
  String imagePath;
  public String fullPath;
  int lenghtOfChildFile;
  
  public Save(Activity activity){
    this.activity = activity;
  }
  
  public String toInternalStorage(Bitmap bitmapImage, String dir){
    ContextWrapper cw = new ContextWrapper(activity);
    // path to /data/data/yourapp/app_data/imageDir
    directory = cw.getDir(dir, Context.MODE_PRIVATE);
    imagePath = directory.toString();
    Log.i("asdimaj", imagePath);
    
    File childFile[] = directory.listFiles();
    
    if(childFile == null || childFile.length == 0){
      lenghtOfChildFile = 0;
      
    }else{ lenghtOfChildFile = childFile.length; }
    
    imageName = "image_" + lenghtOfChildFile + ".jpg";
    
    checkFile = new File(directory, imageName);
    chechIfFileExists(bitmapImage);
    fullPath = imagePath + "/" + imageName;
    
    return fullPath;
  }
  
  void chechIfFileExists(Bitmap bitmapImage){
    if(!checkFile.exists()) {
      
      // Create imageDir
      File mypath = new File(directory, imageName);
      
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(mypath);
        // Use the compress method on the BitMap object to write image to the OutputStream
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }else {
      lenghtOfChildFile += 1;
      chechIfFileExists(bitmapImage);
    }
  }
  
  public String getFileNameByUri(Context context, Uri uri)
  {
    String fileName="unknown";//default fileName
    Uri filePathUri = uri;
    if (uri.getScheme().toString().compareTo("content")==0)
    {
      Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
      if (cursor.moveToFirst())
      {
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
        filePathUri = Uri.parse(cursor.getString(column_index));
        fileName = filePathUri.getLastPathSegment().toString();
      }
    }
    else if (uri.getScheme().compareTo("file")==0)
    {
      fileName = filePathUri.getLastPathSegment().toString();
    }
    else
    {
      fileName = fileName+"_"+filePathUri.getLastPathSegment();
    }
    return fileName;
  }
  
  public void loadImageFromStorage(String path, String fileName, int imageId){
    try {
      File f=new File(path, fileName);
      Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
      ImageView img=(ImageView)this.activity.findViewById(imageId);
      img.setImageBitmap(b);
    }
    catch (FileNotFoundException e){
      e.printStackTrace();
    }
  }
  
  public String getImageName() {
    return imageName;
  }
  
  public String getImagePath() {
    return imagePath;
  }
  
  public String getFullPath() {
    return fullPath;
  }
}
