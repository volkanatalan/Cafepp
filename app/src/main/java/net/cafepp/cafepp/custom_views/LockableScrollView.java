package net.cafepp.cafepp.custom_views;
// https://gist.github.com/chittaranjan-khuntia/42d5429ac37b7aea3cb22fb51c8729b4

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockableScrollView extends ScrollView {
  // true if we can scroll (not locked)
  private boolean mScrollable = true;
  
  public LockableScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
  public LockableScrollView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }
  
  public LockableScrollView(Context context)
  {
    super(context);
  }
  
  public void setScrollingEnabled(boolean enabled) {
    mScrollable = enabled;
  }
  
  public boolean isScrollable() {
    return mScrollable;
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return false ;
  }
  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return false ;
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // if we can scroll pass the event to the superclass
        if (mScrollable) return super.onTouchEvent(ev);
        // only continue to handle the touch event if scrolling enabled
        return mScrollable; // mScrollable is always false at this point
      default:
        return super.onTouchEvent(ev);
    }
  }
  
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    // Don't do anything with intercepted touch events if
    // we are not scrollable
    if (!mScrollable) return false;
    else return super.onInterceptTouchEvent(ev);
  }
}
