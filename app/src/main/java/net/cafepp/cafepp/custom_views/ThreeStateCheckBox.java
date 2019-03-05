package net.cafepp.cafepp.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import net.cafepp.cafepp.R;

@SuppressLint("AppCompatCustomView")
public class ThreeStateCheckBox extends CheckBox {
  static private final int UNCHECKED = 0;
  static private final int CHECKED = 1;
  static private final int MIX = -1;
  private int state;
  
  public ThreeStateCheckBox(Context context) {
    super(context);
    init();
  }
  
  public ThreeStateCheckBox(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }
  
  public ThreeStateCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }
  
  private void init()
  {
    state = CHECKED;
    updateBtn();
    
    setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      
      // checkbox status is changed from unchecked to checked.
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        switch (state)
        {
          case MIX:
            state = CHECKED;
            break;
          case UNCHECKED:
            state = CHECKED;
            break;
          case CHECKED:
            state = UNCHECKED;
            break;
        }
        updateBtn();
      }
    });
    
  }
  
  private void updateBtn()
  {
    int btnDrawable = R.drawable.checkbox;
    switch (state)
    {
      case MIX:
        btnDrawable = R.drawable.checkbox;
        break;
      case UNCHECKED:
        btnDrawable = R.drawable.checkboxnotselectedknifefork10;
        break;
      case CHECKED:
        btnDrawable = R.drawable.checkboxselected;
        break;
    }
    setButtonDrawable(btnDrawable);
    
  }
  public int getState()
  {
    return state;
  }
  
  public void setState(int state)
  {
    this.state = state;
    updateBtn();
  }
}
