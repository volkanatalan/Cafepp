package net.cafepp.cafepp.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import net.cafepp.cafepp.fragments.TableFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
  private final List<Fragment> mFragmentList = new ArrayList<>();
  private final List<String> mFragmentTitleList = new ArrayList<>();
  private SparseArray<TableFragment> registeredFragments = new SparseArray<>();
  
  public ViewPagerAdapter(FragmentManager manager) {
    super(manager);
  }
  
  
  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, int position) {
    Object fragment = super.instantiateItem(container, position);
    if (fragment instanceof TableFragment) {
      TableFragment tableFragment = (TableFragment) fragment;
      registeredFragments.put(position, tableFragment);
      tableFragment.update(((TableFragment)mFragmentList.get(position)).getTableLocation());
    }
    return fragment;
  }
  
  
  
  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    registeredFragments.remove(position);
    super.destroyItem(container, position, object);
  }
  
  
  
  public Fragment getRegisteredFragment(int position) {
    return registeredFragments.get(position);
  }
  
  
  
  public SparseArray<TableFragment> getRegisteredFragment() {
    return registeredFragments;
  }
  
  
  
  @Override
  public Fragment getItem(int position) {
    return mFragmentList.get(position);
  }
  
  
  
  @Override
  public int getCount() {
    return mFragmentList.size();
  }
  
  
  
  public void addFragment(String title, Fragment fragment) {
    mFragmentTitleList.add(title);
    mFragmentList.add(fragment);
  }
  
  
  
  @Override
  public CharSequence getPageTitle(int position) {
    return mFragmentTitleList.get(position);
  }
  
  
  
  public void clear() {
    mFragmentList.clear();
    mFragmentTitleList.clear();
  }
}
