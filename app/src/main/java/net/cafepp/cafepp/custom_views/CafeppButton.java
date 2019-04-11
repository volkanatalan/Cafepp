package net.cafepp.cafepp.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cafepp.cafepp.R;

public class CafeppButton extends LinearLayout {
  private Context context;
  
  private int width, height;
  private int padding;
  private int spacing;
  private Drawable imageViewDrawable;
  private String textViewText;
  private int textColor;
  private int textStyle;
  
  public CafeppButton(Context context) {
    super(context);
    this.context = context;
    init();
  }
  
  public CafeppButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    setAttrs(attrs);
    init();
  }
  
  public CafeppButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    setAttrs(attrs);
    init();
  }
  
  private void init() {
    setLayoutParams(new LinearLayoutCompat.LayoutParams(width, height));
    setOrientation(HORIZONTAL);
    setPadding(padding, padding, padding, padding);
    setGravity(Gravity.CENTER);
    setBackground(getResources().getDrawable(R.drawable.primary_color_rounded_corner_stroke));
  
    ImageView imageView = new ImageView(context);
    imageView.setLayoutParams(new LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    imageView.setImageDrawable(imageViewDrawable);
  
    TextView textView = new TextView(context);
    textView.setLayoutParams(new LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    textView.setPadding(spacing, 0, 0, 0);
    textView.setText(textViewText);
    textView.setTextColor(textColor);
    textView.setTypeface(null, textStyle);
  
    addView(imageView);
    addView(textView);
  
    setTouchListener();
  }
  
  @SuppressLint("ClickableViewAccessibility")
  private void setTouchListener() {
    setOnTouchListener((v, motionEvent) -> {
      switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
          setBackground(getResources().getDrawable(R.drawable.primary_color_rounded_corner_stroke_click));
          break;
        case MotionEvent.ACTION_UP:
          setBackground(getResources().getDrawable(R.drawable.primary_color_rounded_corner_stroke));
      }
      return true;
    });
  }
  
  private void setAttrs(AttributeSet set) {
    if(set == null){
      return;
    }
    
    TypedArray ta = context.obtainStyledAttributes(set, R.styleable.CafeppButton);
    width = ta.getDimensionPixelSize(R.styleable.CafeppButton_android_width, -1);
    height = ta.getDimensionPixelSize(R.styleable.CafeppButton_android_height, -2);
    padding = ta.getDimensionPixelSize(R.styleable.CafeppButton_android_padding, 0);
    spacing = ta.getDimensionPixelSize(R.styleable.CafeppButton_spacing, 0);
    imageViewDrawable = ta.getDrawable(R.styleable.CafeppButton_image);
    textViewText = ta.getString(R.styleable.CafeppButton_text);
    textColor = ta.getColor(R.styleable.CafeppButton_textColor, getResources().getColor(R.color.colorPrimary));
    textStyle = ta.getInt(R.styleable.CafeppButton_textStyle, 1);
    ta.recycle();
  }
}
