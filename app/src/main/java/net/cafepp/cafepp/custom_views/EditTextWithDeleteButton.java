package net.cafepp.cafepp.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.cafepp.cafepp.R;

public class EditTextWithDeleteButton extends LinearLayout {
  private EditText editText;
  private ImageView deleteImageView;
  private Context context;
  private int paddingLeft, paddingTop, paddingRight, paddingBottom;
  private int buttonSize, textSize;
  
  public EditTextWithDeleteButton(Context context) {
    super(context);
    this.context = context;
  }
  
  public EditTextWithDeleteButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    setAttrs(attrs);
  }
  
  public EditTextWithDeleteButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
  }
  
  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
  
    setOrientation(HORIZONTAL);
    
    editText = new EditText(context);
    deleteImageView = new ImageView(context);
    
    LayoutParams buttonLP = new LayoutParams(buttonSize, buttonSize);
    buttonLP.gravity = Gravity.CENTER_VERTICAL;
    deleteImageView.setLayoutParams(buttonLP);
    deleteImageView.setImageResource(R.drawable.delete_x_red_rounded_square);
    deleteImageView.setVisibility(GONE);
  
    LayoutParams editTextLP = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
    editTextLP.weight = 1;
    editTextLP.gravity = Gravity.BOTTOM;
    editText.setLayoutParams(editTextLP);
    editText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    editText.setTextSize(textSize);
    editText.setTextColor(ContextCompat.getColor(context, R.color.white));
    editText.setTextAlignment(TEXT_ALIGNMENT_CENTER);
  
    addView(editText);
    addView(deleteImageView);
  
    deleteImageView.setOnClickListener(view -> editText.setText(""));
    
    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    
      }
  
      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    
      }
  
      @Override
      public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        if (text.equals(""))
          deleteImageView.setVisibility(GONE);
        else
          deleteImageView.setVisibility(VISIBLE);
  
        afterTextChange(text);
      }
    });
  }
  
  public void afterTextChange(String text){
  
  }
  
  private void setAttrs(AttributeSet set) {
    if(set == null){
      return;
    }
    
    float dens = getResources().getDisplayMetrics().density;
    
    TypedArray ta = context.obtainStyledAttributes(set, R.styleable.EditTextWithDeleteButton);
    
    textSize = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_textSize, 14);
    buttonSize = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_buttonSize, (int)(50 * dens));
    paddingLeft = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_padding, 0);
    paddingTop = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_padding, 0);
    paddingRight = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_padding, 0);
    paddingBottom = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_padding, 0);
    paddingLeft = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_paddingStart, 0);
    paddingTop = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_paddingTop, 0);
    paddingRight = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_paddingEnd, 0);
    paddingBottom = ta.getDimensionPixelSize(R.styleable.EditTextWithDeleteButton_android_paddingBottom, 0);
    
    ta.recycle();
  }
  
  public EditText getEditText(){
    return editText;
  }
}
