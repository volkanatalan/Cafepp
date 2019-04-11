package net.cafepp.cafepp.custom_views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.cafepp.cafepp.R;

public class EditTextWithDeleteButton extends LinearLayout {
  private EditText editText;
  private ImageView deleteImageView;
  private Context context;
  private LayoutParams editTextLayoutParams;
  
  public EditTextWithDeleteButton(Context context) {
    super(context);
    this.context = context;
  }
  
  public EditTextWithDeleteButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }
  
  public EditTextWithDeleteButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
  }
  
  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    float screenDensity = getResources().getDisplayMetrics().density;
  
    setOrientation(HORIZONTAL);
    
    editText = new EditText(context);
    deleteImageView = new ImageView(context);
    
    deleteImageView.setLayoutParams(new LayoutParams(
        (int)(screenDensity * 50),
        (int)(screenDensity * 50)));
    deleteImageView.setImageResource(R.drawable.delete_x_red_rounded_square);
    deleteImageView.setVisibility(INVISIBLE);
  
    editTextLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
    editTextLayoutParams.weight = 1;
    editText.setLayoutParams(editTextLayoutParams);
    editText.setPadding(20, 0, 20, 0);
    editText.setTextSize(24f);
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
          deleteImageView.setVisibility(INVISIBLE);
        else
          deleteImageView.setVisibility(VISIBLE);
  
        afterTextChange(text);
      }
    });
  }
  
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    int editTextMarginLeftRight = 10;
    editTextLayoutParams.leftMargin = deleteImageView.getWidth() + editTextMarginLeftRight;
    editTextLayoutParams.rightMargin = editTextMarginLeftRight;
  }
  
  public void afterTextChange(String text){
  
  }
  
  public EditText getEditText(){
    return editText;
  }
}
