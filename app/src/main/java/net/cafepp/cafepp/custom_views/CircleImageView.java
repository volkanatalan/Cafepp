package net.cafepp.cafepp.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;

public class CircleImageView extends AppCompatImageView {
  private Bitmap bitmap;
  private Paint paint, paintBorder;
  private BitmapShader bitmapShader;
  private int imageWidth, imageHeight, borderWidth;
  private float shadowRadius = 4f;
  private float shadowDx = 2f, shadowDy = 2f;
  private boolean isImageSet = false;
  
  public CircleImageView(Context context) {
    super(context);
    start();
  }
  
  public CircleImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    start();
  }
  
  public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    start();
  }
  
  private void start() {
    borderWidth = dp(3);
    
    paint = new Paint();
    paint.setAntiAlias(true);
  
    paintBorder = new Paint();
    paintBorder.setColor(Color.BLACK);
    //setBorderColor(Color.BLACK);
    paintBorder.setAntiAlias(true);
    this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
    paintBorder.setShadowLayer(shadowRadius, 2f, 2f, Color.BLACK);
  }
  
  @SuppressLint("DrawAllocation")
  @Override
  protected void onDraw(Canvas canvas) {
    loadBitmap();
  
    if (bitmap != null) {
      bitmapShader = new BitmapShader(Bitmap.createScaledBitmap(bitmap, canvas.getWidth(),
          canvas.getHeight(), false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      paint.setShader(bitmapShader);
      int center = imageWidth / 2;
      canvas.drawCircle(center + borderWidth, center + borderWidth,
          center + borderWidth - shadowRadius, paintBorder);
      canvas.drawCircle(center + borderWidth, center + borderWidth,
          center - shadowRadius, paint);
    }
  }
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = measureWidth(widthMeasureSpec);
    int height = measureHeight(heightMeasureSpec);
    
    imageWidth = width - (borderWidth * 2) - (int)(shadowRadius + shadowDx);
    imageHeight = height - (borderWidth * 2) - (int)(shadowRadius + shadowDy);
  
    setMeasuredDimension(width, height);
  }
  
  private int measureWidth(int measureSpec) {
    int result;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else {
      result = imageWidth;
    }
    
    return result;
  }
  
  private int measureHeight(int measureSpecHeight) {
    int result;
    int specMode = MeasureSpec.getMode(measureSpecHeight);
    int specSize = MeasureSpec.getSize(measureSpecHeight);
    
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else {
      result = imageHeight;
    }
    
    return (result);
  }
  
  private void loadBitmap() {
    Drawable drawable = this.getDrawable();
  
    if (drawable != null && !isImageSet) {
      if (drawable instanceof BitmapDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        bitmap = bitmapDrawable.getBitmap();
      } else if (drawable instanceof RoundedBitmapDrawable) {
        RoundedBitmapDrawable roundedBitmapDrawable = (RoundedBitmapDrawable) drawable;
        bitmap = roundedBitmapDrawable.getBitmap();
      }
    }
  }
  
  int dp(int px) {
    Resources r = getResources();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
  }
  
  public void setImage(String imagePath) {
    bitmap = BitmapFactory.decodeFile(imagePath);
    isImageSet = true;
  }
  
  public void setBorderColor(int borderColor) {
    if (paintBorder != null)
      paintBorder.setColor(borderColor);
    this.invalidate();
  }
  
  public void setBorderWidthPx(int px) {
    borderWidth = px;
    this.invalidate();
  }
  
  public void setBorderWidthDp(int dp) {
    borderWidth = dp(dp);
    this.invalidate();
  }
  
  public void modifyShadow(int radius, int dx, int dy, int color) {
    paintBorder.setShadowLayer(radius, dx, dy, color);
    this.invalidate();
  }
}
