package net.cafepp.cafepp.custom_views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;

public class ImageSliderView extends RelativeLayout {
  private Context context;
  private ArrayList<String> imagePaths;
  private int currentImage, nextImage, prevImage;
  private int currentFrame = 1, nextFrame = 2, prevFrame = 0;
  private int SLIDE_ANIMATION_DURATION = 500;
  private int SLIDE_DELAY = 3000;
  private int mWidth;
  private ArrayList<ImageView> imageViews = new ArrayList<>();
  
  public ImageSliderView(Context context) {
    super(context);
    this.context = context;
    init();
  }
  
  public ImageSliderView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    init();
  }
  
  public ImageSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
    init();
  }
  
  private void init(){
    ImageView prevFrameImage = new ImageView(context);
    prevFrameImage.setLayoutParams(new LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    prevFrameImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
  
    ImageView currentFrameImage = new ImageView(context);
    currentFrameImage.setLayoutParams(new LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    currentFrameImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
  
    ImageView nextFrameImage = new ImageView(context);
    nextFrameImage.setLayoutParams(new LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    nextFrameImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
  
    addView(prevFrameImage);
    addView(currentFrameImage);
    addView(nextFrameImage);
  
    imageViews.add(prevFrameImage);
    imageViews.add(currentFrameImage);
    imageViews.add(nextFrameImage);
  }
  
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
  
    mWidth = getWidth();
  
    imageViews.get(0).setX(mWidth * -1);
    imageViews.get(2).setX(mWidth);
  
  
    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (imagePaths != null && imagePaths.size() > 1)
          changeFrame(true);
      }
    });
  }
  
  private void changeFrame(final boolean next){
    float animStartValue = Math.abs(imageViews.get(currentFrame).getX());
    animStartValue = animStartValue == mWidth ? 0f : animStartValue;
  
    /*Log.i("slide", "*****************changeFrame*****************");
    Log.i("slide", "imageViews.get(currentFrame).getX(): " + imageViews.get(currentFrame).getX());
    Log.i("slide", "animStartValue: " + animStartValue);
    Log.i("slide", "prevFrame: " + prevFrame);
    Log.i("slide", "currentFrame: " + currentFrame);
    Log.i("slide", "nextFrame: " + nextFrame);
    Log.i("slide", " ");*/
    
    final ValueAnimator animator = ValueAnimator.ofFloat(animStartValue, mWidth);
    animator.setDuration(SLIDE_ANIMATION_DURATION);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float animatedValue = (float) valueAnimator.getAnimatedValue();
  
        if (next) {
          imageViews.get(prevFrame).setX((mWidth * -1) - animatedValue);
          imageViews.get(currentFrame).setX(animatedValue * -1);
          imageViews.get(nextFrame).setX(mWidth - animatedValue);
  
         /* Log.i("slide", "*****************onAnimationUpdate*****************");
          Log.i("slide", "animatedValue: " + animatedValue);
          Log.i("slide", "prevFrame x: " + imageViews.get(prevFrame).getX());
          Log.i("slide", "currentFrame x: " + imageViews.get(currentFrame).getX());
          Log.i("slide", "nextFrame x: " + imageViews.get(nextFrame).getX());
          Log.i("slide", " ");*/
        } else {
          imageViews.get(prevFrame).setX((mWidth * -1) + animatedValue);
          imageViews.get(currentFrame).setX(animatedValue);
          imageViews.get(nextFrame).setX(mWidth + animatedValue);
        }
      }
    });
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation, boolean isReverse) {
  
  
        /*Log.i("slide", "*****************onAnimationStart*****************");
        Log.i("slide", "prevFrame x: " + imageViews.get(prevFrame).getX());
        Log.i("slide", "currentFrame x: " + imageViews.get(currentFrame).getX());
        Log.i("slide", "nextFrame x: " + imageViews.get(nextFrame).getX());
        Log.i("slide", " ");*/
      }
  
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        Log.i("slide", "*****************onAnimationEnd*****************");
        if (imageViews.get(nextFrame).getX() == 0f || imageViews.get(nextFrame).getX() == mWidth * 2) {
  
          /*Log.i("slide", "imageViews.get(nextFrame).getX(): " + imageViews.get(nextFrame).getX());
          Log.i("slide", "--------before updateFrameAndImageNums--------");
          Log.i("slide", "prevFrame: " + prevFrame);
          Log.i("slide", "currentFrame: " + currentFrame);
          Log.i("slide", "nextFrame: " + nextFrame);
          Log.i("slide", "prevImage: " + prevImage);
          Log.i("slide", "currentImage: " + currentImage);
          Log.i("slide", "nextImage: " + nextImage);
          Log.i("slide", " ");*/
          updateFrameAndImageNums(next);
  
          /*Log.i("slide", "--------after updateFrameAndImageNums--------");
          Log.i("slide", "imageViews.get(currentFrame).getX(): " + imageViews.get(currentFrame).getX());
          Log.i("slide", "prevFrame: " + prevFrame);
          Log.i("slide", "currentFrame: " + currentFrame);
          Log.i("slide", "nextFrame: " + nextFrame);
          Log.i("slide", "prevImage: " + prevImage);
          Log.i("slide", "currentImage: " + currentImage);
          Log.i("slide", "nextImage: " + nextImage);
          Log.i("slide", " ");*/
          
          if (next) {
            
            String nextImagePath = imagePaths.get(nextImage);
  
            // Set next image to the next frame
            imageViews.get(nextFrame).setImageURI(getURIFromPath(nextImagePath));
            
            // Put first frame next to the last frame.
            imageViews.get(nextFrame).setX(mWidth);
            /*Log.i("slide", "*************after move frame****************");
            Log.i("slide", "getX(): " + getX());
            Log.i("slide", "getY(): " + getY());
            Log.i("slide", "getWidth(): " + getWidth());
            Log.i("slide", "getHeight(): " + getHeight());
            
            Log.i("slide", "imageViews.get(prevFrame).getX(): " + imageViews.get(prevFrame).getX());
            Log.i("slide", "imageViews.get(prevFrame).getY(): " + imageViews.get(prevFrame).getY());
            Log.i("slide", "imageViews.get(prevFrame).getWidth(): " + imageViews.get(prevFrame).getWidth());
            Log.i("slide", "imageViews.get(prevFrame).getHeight(): " + imageViews.get(prevFrame).getHeight());
  
            Log.i("slide", "imageViews.get(currentFrame).getX(): " + imageViews.get(currentFrame).getX());
            Log.i("slide", "imageViews.get(currentFrame).getY(): " + imageViews.get(currentFrame).getY());
            Log.i("slide", "imageViews.get(currentFrame).getWidth(): " + imageViews.get(currentFrame).getWidth());
            Log.i("slide", "imageViews.get(currentFrame).getHeight(): " + imageViews.get(currentFrame).getHeight());
  
            Log.i("slide", "imageViews.get(nextFrame).getX(): " + imageViews.get(nextFrame).getX());
            Log.i("slide", "imageViews.get(nextFrame).getY(): " + imageViews.get(nextFrame).getY());
            Log.i("slide", "imageViews.get(nextFrame).getWidth(): " + imageViews.get(nextFrame).getWidth());
            Log.i("slide", "imageViews.get(nextFrame).getHeight(): " + imageViews.get(nextFrame).getHeight());*/
            
          } else {
            // Put last frame next to the first frame.
            imageViews.get(nextFrame).setX(imageViews.get(prevFrame).getX() - mWidth);
            
            // Set previous image to previous frame.
            imageViews.get(prevFrame).setImageURI(getURIFromPath(imagePaths.get(prevImage)));
          }
        }
      }
    });
    
    animator.start();
  }
  
  private void updateFrameAndImageNums(boolean next) {
    if (next) {
      currentFrame++;
      currentFrame %= 3;
    
      nextFrame++;
      nextFrame %= 3;
    
      prevFrame++;
      prevFrame %= 3;
    
      currentImage++;
      currentImage %= imagePaths.size();
    
      nextImage++;
      nextImage %= imagePaths.size();
    
      prevImage++;
      prevImage %= imagePaths.size();
    
    } else {
      currentFrame--;
      currentFrame = currentFrame < 0 ? 2 : currentFrame;
    
      nextFrame--;
      nextFrame = nextFrame < 0 ? 2 : nextFrame;
    
      prevFrame--;
      prevFrame = prevFrame < 0 ? 2 : prevFrame;
    
      currentImage--;
      currentImage = currentImage < 0 ? imagePaths.size() - 1 : currentImage;
    
      nextImage--;
      nextImage  = nextImage < 0 ? imagePaths.size() - 1 : nextImage;
    
      prevImage--;
      prevImage  = prevImage < 0 ? imagePaths.size() - 1 : prevImage;
    }
  }
  
  private Uri getURIFromPath(String imagePath) {
    File imgFile = new  File(imagePath);
    return Uri.fromFile(imgFile);
  }
  
  public ImageSliderView setImagePaths(ArrayList<String> imagePaths) {
    if (imagePaths == null) {
      try {
        throw new NullPointerException("ArrayList<String> imagePaths cannot be null.");
      } catch (NullPointerException e) {
        Log.e(getClass().getName(), e.getMessage());
      }
    }
    else if (imagePaths.size() == 0) {
      try {
        throw new Exception("ArrayList<String> imagePaths size is 0.");
      } catch (Exception e) {
        Log.e(getClass().getName(), e.getMessage());
      }
    }
    else {
      this.imagePaths = imagePaths;
      
      if (imagePaths.size() == 1){
        imageViews.get(currentFrame).setImageURI(getURIFromPath(imagePaths.get(0)));
      }
      else {
        prevImage = imagePaths.size() - 1;
        currentImage = 0;
        nextImage = 1;
        
        imageViews.get(prevFrame).setImageURI(getURIFromPath(imagePaths.get(prevImage)));
        imageViews.get(currentFrame).setImageURI(getURIFromPath(imagePaths.get(currentImage)));
        imageViews.get(nextFrame).setImageURI(getURIFromPath(imagePaths.get(nextImage)));
      }
    }
    return this;
  }
}