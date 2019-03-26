package net.cafepp.cafepp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.custom_views.DeviceAdmin;
import net.cafepp.cafepp.custom_views.EditTextWithDeleteButton;
import net.cafepp.cafepp.fragments.CategoriesFragment;
import net.cafepp.cafepp.fragments.SearchProductFragment;

/**
 * {@link MenuActivity} is the first activity that customers will see, when they take
 * the tablet to make an order.
 */

public class MenuActivity extends AppCompatActivity {
  
  private EditTextWithDeleteButton searchEditText;
  private CategoriesFragment categoriesFragment;
  private RelativeLayout toolbar, searchEditTextBase;
  private ImageView backgroundImageView;
  private ImageView companyLogo;
  private ValueAnimator animatorMoveToolbarCenter;
  private boolean isToolbarCenterUp = false;
  private String searchText;
  private SearchProductFragment searchProductFragment;
  private TextWatcher textWatcher;
  private ComponentName compName;
  private int pressBackCount = 0;
  private CountDownTimer countDownTimer;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);
    
    searchEditTextBase = findViewById(R.id.searchEditTextBase);
    companyLogo = findViewById(R.id.companyLogo);
    searchEditText = findViewById(R.id.searchEditText);
    toolbar = findViewById(R.id.toolbar);
    backgroundImageView = findViewById(R.id.backgroundImage);
  
    compName = new ComponentName(this, DeviceAdmin.class);
    
    categoriesFragment = new CategoriesFragment();
    
    openCategoriesFragment(false);
    setTextWatcher();
    onClickCompanyLogo();
    setCountDownTimer();
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    setupToolbarCenterMoveAnimation();
  }
  
  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    hideActionBar();
  }
  
  private void hideActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
  }
  
  @Override
  public void onBackPressed() {
    
    if (isToolbarCenterUp)
      onClickSearchButton(null);
    else
      super.onBackPressed();
  }
  
  /**
   * It is used to pop back to {@link CategoriesFragment}.
   */
  private void onClickCompanyLogo() {
    companyLogo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getSupportFragmentManager().popBackStackImmediate(
            "categories", FragmentManager.POP_BACK_STACK_INCLUSIVE);
      }
    });
  }
  
  public void onClickSearchButton(View view) {
    
    if (!isToolbarCenterUp) {
      // Open SearchProductFragment.
      searchProductFragment = SearchProductFragment.newInstance("");
      getSupportFragmentManager()
          .beginTransaction()
          .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
              R.anim.enter_from_right, R.anim.exit_to_left)
          .replace(R.id.fragmentContainer, searchProductFragment, "searchProductFragment")
          .addToBackStack(null)
          .commit();
      
      // Add textChangesListener to searchEditText.
      searchEditText.getEditText().addTextChangedListener(textWatcher);
    } else {
      // Remove textChangesListener from searchEditText.
      searchEditText.getEditText().removeTextChangedListener(textWatcher);
      getSupportFragmentManager().popBackStack();
    }
    
    // Set toolbar center animation's starting and ending values.
    if (isToolbarCenterUp)
      animatorMoveToolbarCenter.setFloatValues(toolbar.getHeight(), 0);
    else
      animatorMoveToolbarCenter.setFloatValues(0, toolbar.getHeight());
    
    // Move toolbar center up.
    if (!animatorMoveToolbarCenter.isRunning()) {
      animatorMoveToolbarCenter.start();
    }
  }
  
  /**
   * If SearchEditText is shown, hides it; else locks the screen to prevent customers to
   * reach undesired activities. Thus to reach the application back, device needs to be unlocked.
   */
  public void onClickBackButton(View view) {
    if (isToolbarCenterUp) {
      onClickSearchButton(null);
    } else if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
      if (isPressedTwice()) {
        lockScreen();
        onBackPressed();
      }
    } else {
      onBackPressed();
    }
  }
  
  private void setTextWatcher(){
    textWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      
      }
    
      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      
      }
    
      @Override
      public void afterTextChanged(Editable editable) {
        searchText = editable.toString();
        searchProductFragment.onTextChanged(searchText);
      }
    };
  }
  
  private void openCategoriesFragment(boolean withAnim) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
  
    if (withAnim) {
      transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
          R.anim.enter_from_right, R.anim.exit_to_left);
    }
  
    transaction
        .replace(R.id.fragmentContainer, categoriesFragment, "categoriesFragment")
        .commit();
  }
  
  
  /**
   * To delete all characters in the SearchEditText.
   */
  public void clearSearchEditText() {
    searchEditText.getEditText().setText("");
  }
  
  private void setupToolbarCenterMoveAnimation() {
    animatorMoveToolbarCenter = ValueAnimator.ofFloat(0, toolbar.getHeight());
    animatorMoveToolbarCenter.setDuration(500);
    animatorMoveToolbarCenter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float animatedValue = (Float)valueAnimator.getAnimatedValue();
        
        companyLogo.setY(animatedValue * -1);
        searchEditTextBase.setY(toolbar.getHeight() - animatedValue);
      }
    });
    animatorMoveToolbarCenter.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        isToolbarCenterUp = !isToolbarCenterUp;
  
        if (isToolbarCenterUp) {
          searchEditText.getEditText().requestFocusFromTouch();
          ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
              .showSoftInput(searchEditText.getEditText(), 0);
        } else {
          searchEditText.getEditText().setText("");
          searchEditText.getEditText().clearFocus();
          ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
              .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
      }
    });
  }
  
  /**
   * Locks the screen if permission is granted, else asks permission.
   */
  private void lockScreen() {
    DevicePolicyManager policyManager;
    policyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
    if (policyManager.isAdminActive(compName)) {
      policyManager.lockNow();
    } else {
      Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
      intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.admin_request));
      startActivityForResult(intent, 1);
    }
  }
  
  private boolean isPressedTwice() {
    pressBackCount++;
    countDownTimer.start();
    if (pressBackCount > 1){
      countDownTimer.cancel();
      return true;
    } else return false;
  }
  
  private void setCountDownTimer() {
    countDownTimer = new CountDownTimer(1000, 1000) {
      @Override
      public void onTick(long l) {
    
      }
  
      @Override
      public void onFinish() {
        pressBackCount = 0;
        Toast.makeText(MenuActivity.this, getString(R.string.Press_twice_back_button_to_exit),
            Toast.LENGTH_SHORT).show();
      }
    };
  }
}