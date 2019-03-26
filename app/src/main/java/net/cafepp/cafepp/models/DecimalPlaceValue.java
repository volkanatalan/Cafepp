package net.cafepp.cafepp.models;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class DecimalPlaceValue {
  String midChar;
  int numberLimitAfterDot;
  int oldLength = 0;
  String str ="";
  boolean isDel;
  
  public DecimalPlaceValue(EditText editText, String midChar, int numberLimitAfterDot){
    this.midChar = midChar;
    this.numberLimitAfterDot = numberLimitAfterDot;
    EditTextWatcher editTextWatcher = new EditTextWatcher(editText);
    editText.addTextChangedListener(editTextWatcher);
  }
  
  private void doIt(final EditText et){
    et.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_UP) {
          if (str.length() != 0) {
            int cursorPosition = et.getSelectionStart()+1;
            String str2 = str.replace(midChar, "");
            int oldstrlength = str.length();
            int mod;
            int spaceNumber;
            String beforeDot;
            String afterDot;
            boolean isDot;
            String finalString = "";
            
            if (str2.charAt(0) == '.') {
              str2 = "" + 0 + str2;
              cursorPosition ++;
            }
            
            if (str2.charAt(0) == ' ') {
              str2 = str2.substring(1, str2.length());
            }
            
            if (!str2.contains(".")) {
              beforeDot = str2;
              afterDot = "";
              isDot = false;
              
            } else {
              beforeDot = str2.substring(0, str2.indexOf("."));
              afterDot = str2.substring((str2.indexOf(".") + 1), str2.length());
              isDot = true;
            }
            
            mod = beforeDot.length() % 3;
            spaceNumber = beforeDot.length() / 3;
            
            if (beforeDot.length() > 3) {
              if (mod != 0) {
                beforeDot = beforeDot.substring(0, mod) + midChar + beforeDot.substring(mod, beforeDot.length());
              }
              if (beforeDot.length() > 5) {
                for (int i = 1; i < spaceNumber; i++) {
                  if (mod == 0) {
                    beforeDot = beforeDot.substring(0, mod + i - 1 + (3 * i)) + midChar +
                                    beforeDot.substring(mod + i - 1 + (3 * i), beforeDot.length());
                  } else {
                    beforeDot = beforeDot.substring(0, mod + i + (3 * i)) + midChar +
                                    beforeDot.substring(mod + i + (3 * i), beforeDot.length());
                  }
                }
              }
            }
            
            if (isDot && str2.length() > str2.indexOf(".")) {
              if (afterDot.length() > numberLimitAfterDot) {
                afterDot = afterDot.substring(0, numberLimitAfterDot);
              }
              finalString = beforeDot + "." + afterDot;
              
            } else {
              finalString = beforeDot;
            }
            
            if (isDel) cursorPosition--;
            else if (finalString.length() - oldstrlength > 0) cursorPosition++;
            
            if (cursorPosition > finalString.length()) cursorPosition = finalString.length();
            if (cursorPosition < 0) cursorPosition = 0;
            
            et.setText(finalString);
            et.setSelection(cursorPosition);
            oldLength = finalString.length();
            
          } else{
            et.setText("");
            et.setSelection(0);
          }
        }
        //}
        return false;
      }
    });
  }
  
  class EditTextWatcher implements TextWatcher {
    EditText mEditText;
    
    EditTextWatcher(EditText editText){
      this.mEditText = editText;
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    
    }
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    
    }
    
    @Override
    public void afterTextChanged(Editable s) {
      str = mEditText.getText().toString();
      isDel = oldLength > str.length();
      doIt(mEditText);
    }
  }
}
