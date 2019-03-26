package net.cafepp.cafepp.custom_views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CircleImage {
  private Context mContext;
  private Resources mResources;
  private Bitmap mBitmap;
  RoundedBitmapDrawable drawable;
  int mstrokeColor;
  
  public CircleImage() {
  }
  
  public Drawable getDrawable() {
    return drawable;
  }
  
  public CircleImage(Context context , String path, int strokeColor) {
    this.mContext = context;
    this.mResources = context.getResources();
    this.mstrokeColor = strokeColor;
    
    try {
      if(path != null) {
        File f = new File(path);
        mBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
      }
    }
    catch (FileNotFoundException e){
      Log.e("eee", e.getMessage());
    }
    
    // Convert the ImageView image to a rounded corners image with border
    drawable = createRoundedBitmapDrawableWithBorder(mBitmap);
    
  }
  
  CircleImage(Context context , int image, int strokeColor){
    this.mContext = context;
    this.mResources = context.getResources();
    this.mstrokeColor = strokeColor;
    this.mBitmap = BitmapFactory.decodeResource(context.getResources(), image);
    
    // Convert the ImageView image to a rounded corners image with border
    drawable = createRoundedBitmapDrawableWithBorder(mBitmap);
  }
  
  public CircleImage(Context context, Bitmap bitmap, int strokeColor){
    this.mContext = context;
    this.mResources = context.getResources();
    this.mstrokeColor = strokeColor;
    this.mBitmap = bitmap;
    
    // Convert the ImageView image to a rounded corners image with border
    drawable = createRoundedBitmapDrawableWithBorder(mBitmap);
  }
  
  
  private RoundedBitmapDrawable createRoundedBitmapDrawableWithBorder(Bitmap bitmap){
    int bitmapWidth = bitmap.getWidth();
    int bitmapHeight = bitmap.getHeight();
    int borderWidthHalf = 10; // In pixels
    //Toast.makeText(mContext,""+bitmapWidth+"|"+bitmapHeight,Toast.LENGTH_SHORT).show();
    
    // Calculate the bitmap radius
    int bitmapRadius = Math.min(bitmapWidth,bitmapHeight)/2;
    
    int bitmapSquareWidth = Math.min(bitmapWidth,bitmapHeight);
    //Toast.makeText(mContext,""+bitmapMin,Toast.LENGTH_SHORT).show();
    
    int newBitmapSquareWidth = bitmapSquareWidth+borderWidthHalf;
    //Toast.makeText(mContext,""+newBitmapMin,Toast.LENGTH_SHORT).show();
    
    Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth,newBitmapSquareWidth,Bitmap.Config.ARGB_8888);
    
    // Initialize a new Canvas to draw empty bitmap
    Canvas canvas = new Canvas(roundedBitmap);
    
    // Draw a solid color to canvas
    canvas.drawColor(Color.WHITE);
    
    // Calculation to draw bitmap at the circular bitmap center position
    int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth - ((borderWidthHalf + bitmapSquareWidth - bitmapWidth)/2);
    int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;
    
    canvas.drawBitmap(bitmap, x, y, null);
    
    // Initializing a new Paint instance to draw circular border
    Paint borderPaint = new Paint();
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(borderWidthHalf*2);
    borderPaint.setColor(ContextCompat.getColor(mContext, mstrokeColor));
    
    
    canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, newBitmapSquareWidth/2, borderPaint);
    
    // Create a new RoundedBitmapDrawable
    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources,roundedBitmap);
    
    // Set the corner radius of the bitmap drawable
    roundedBitmapDrawable.setCornerRadius(bitmapRadius);
    
    roundedBitmapDrawable.setAntiAlias(true);
    
    // Return the RoundedBitmapDrawable
    return roundedBitmapDrawable;
  }
}
