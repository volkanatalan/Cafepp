package net.cafepp.cafepp.custom_views;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class OnTouchSwipeListener implements View.OnTouchListener {
  private View rightViewForDistance;
  private View leftViewForDistance;
  private int touchX1 = 0;
  private int touchX2 = 0;
  private int difference;
  private int MOVE_TOLERANCE = 20;
  private int REVEAL_TOLERANCE = 50;
  private int REVEAL_TIME = 1000;
  private int COVER_TIME = 1000;
  private boolean isOnLeft = false;
  private boolean isOnRight = false;
  
  public OnTouchSwipeListener (View viewOnLeft, View viewOnRight){
    this.leftViewForDistance = viewOnLeft;
    this.rightViewForDistance = viewOnRight;
  }
  
  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int surfaceLeft = v.getLeft();
    int surfaceBottom = v.getBottom();
    int surfaceWidth = v.getWidth();
    
    switch (event.getAction()) {
      
      case MotionEvent.ACTION_DOWN:
        
        touchX1 = (int) event.getRawX();
        difference = (int) (event.getRawX() - surfaceLeft);
        break;
      
      case MotionEvent.ACTION_MOVE:
        
        touchX2 = (int) event.getRawX();
        if (Math.abs(touchX1 - touchX2) >= MOVE_TOLERANCE) {
          onMove();
          int newLeft = touchX2 - difference;
          int newRight = newLeft + surfaceWidth;
          v.layout(newLeft, 0, newRight, surfaceBottom);
        }
        break;
      
      case MotionEvent.ACTION_UP:
        boolean swiped = Math.abs(touchX1 - touchX2) > REVEAL_TOLERANCE;
        boolean swipedLeft = touchX1 - touchX2 > REVEAL_TOLERANCE;
        boolean swipedRight = touchX2 - touchX1 > REVEAL_TOLERANCE;
        boolean moved = Math.abs(touchX1 - touchX2) < REVEAL_TOLERANCE &&
                            Math.abs(touchX1 - touchX2) > MOVE_TOLERANCE;
        boolean clicked = touchX2 == 0 || Math.abs(touchX1 - touchX2) < MOVE_TOLERANCE;
        
        onActionUp();
        
        if (clicked) onSurfaceClick(v);
        else if (swipedLeft && !isOnRight)
          onRevealRightSide(v);
        else if (swipedRight && !isOnLeft)
          onRevealLeftSide(v);
        else if (moved && isOnLeft)
          onRevealRightSide(v);
        else if (moved && isOnRight)
          onRevealLeftSide(v);
        else if ((moved || swiped) && (!isOnLeft || !isOnRight))
          onCover(v);
        
        touchX1 = 0; touchX2 = 0;
        break;
      
      default:
        return false;
    }
    return true;
  }
  
  public void onMove() {
  }
  
  public void onRevealRightSide(View v) {
    int layoutWidth = v.getWidth();
    int layoutHeight = v.getHeight();
    v.animate()
        .x(rightViewForDistance.getLeft() - layoutWidth)
        .setDuration(REVEAL_TIME)
        .start();
    v.layout(rightViewForDistance.getLeft() - layoutWidth, 0, rightViewForDistance.getLeft(), layoutHeight);
    isOnRight = false;
    isOnLeft = true;
    //isCovered = false;
  }
  
  public void onRevealLeftSide(View v) {
    int layoutWidth = v.getWidth();
    int layoutHeight = v.getHeight();
    v.animate()
        .x(leftViewForDistance.getRight())
        .setDuration(REVEAL_TIME)
        .start();
    v.layout(leftViewForDistance.getRight(), 0, leftViewForDistance.getRight() + layoutWidth, layoutHeight);
    isOnRight = true;
    isOnLeft = false;
    //isCovered = false;
  }
  
  public void onCover(View v) {
    v.animate()
        .x(0)
        .setDuration(COVER_TIME)
        .start();
    v.layout(0, 0, v.getWidth(), v.getHeight());
    isOnRight = false;
    isOnLeft = false;
    //isCovered = true;
  }
  
  public void onActionUp() {
  }
  
  public void onSurfaceClick(View v) {
  }
  
  public int getMoveTolerance() {
    return MOVE_TOLERANCE;
  }
  
  public void setMoveTolerance(int moveTolerance) {
    this.MOVE_TOLERANCE = moveTolerance;
  }
  
  public int getRevealTolerance() {
    return REVEAL_TOLERANCE;
  }
  
  public void setRevealTolerance(int revealTolerance) {
    this.REVEAL_TOLERANCE = revealTolerance;
  }
  
  public int getRevealTime() {
    return REVEAL_TIME;
  }
  
  public void setRevealTime(int revealTime) {
    this.REVEAL_TIME = revealTime;
  }
  
  public int getCoverTime() {
    return COVER_TIME;
  }
  
  public void setCoverTime(int coverTime) {
    this.COVER_TIME = coverTime;
  }
}
