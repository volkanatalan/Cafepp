package net.cafepp.cafepp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.fragments.AddTableFragment;
import net.cafepp.cafepp.fragments.AddTableLocationFragment;
import net.cafepp.cafepp.fragments.ControlTableFragment;
import net.cafepp.cafepp.fragments.HomeFragment;
import net.cafepp.cafepp.fragments.ProductsFragment;
import net.cafepp.cafepp.fragments.StatisticsFragment;
import net.cafepp.cafepp.fragments.StockFragment;
import net.cafepp.cafepp.fragments.TableContentFragment;
import net.cafepp.cafepp.fragments.TablesFragment;

import java.util.List;

public class AdministratorActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  
  private Context mContext;
  private TablesFragment mTablesFragment;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    mContext = this;
    
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    navigationView.setCheckedItem(R.id.nav_home);
  
    // Launch HomeFragment at the beginning.
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.fragmentContainer, new HomeFragment(), "HomeFragment");
    ft.addToBackStack("HomeFragment");
    ft.commit();
  }
  
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    
    FragmentManager fragmentManager = getSupportFragmentManager();
    List<Fragment> fragments = fragmentManager.getFragments();
    
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    }
    
    else if (fragments.size() > 0){
      Fragment currentFragment = fragments.get(fragments.size() - 1);
  
      if (currentFragment instanceof HomeFragment) {
        fragmentManager.popBackStack();
        super.onBackPressed();
      }
  
      else if (currentFragment instanceof AddTableLocationFragment) {
        fragmentManager.popBackStack();
        
        AddTableFragment addTableFragment = (AddTableFragment) fragmentManager.findFragmentByTag("AddTableFragment");
        if (addTableFragment != null)
          addTableFragment.showInterlayer(false);
      }
      
      else if (currentFragment instanceof AddTableFragment) {
        AddTableFragment addTableFragment = (AddTableFragment) currentFragment;
        addTableFragment.onOptionsItemSelectedActionDone();
      }
      
      else if (currentFragment instanceof TableContentFragment){
        getSupportFragmentManager().popBackStack();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("TablesFragment");
        if (fragment instanceof TablesFragment) {
          TablesFragment tablesFragment = (TablesFragment) fragment;
          tablesFragment.updateViewPager();
        }
      }


      else if (currentFragment instanceof ControlTableFragment) {
        getSupportFragmentManager().popBackStack();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("TableContentFragment");
        if (fragment instanceof TableContentFragment) {
          TableContentFragment tablesFragment = (TableContentFragment) fragment;
          tablesFragment.hideInterLayer();
        }
      }
      
      else {
        super.onBackPressed();
      }
    }
    
    else {
      super.onBackPressed();
    }
  }
  
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
  
    if (id == R.id.nav_home) {
      // Open HomeFragment.
      //openFragment(new HomeFragment(), "HomeFragment");
      getSupportFragmentManager().popBackStack("HomeFragment", 0);
      
    }
    else if (id == R.id.nav_tables) {
      // Open TablesFragment.
      openFragment(new TablesFragment(), "TablesFragment");
  
    }
    else if (id == R.id.nav_products) {
      // Open ProductsFragment.
      openFragment(new ProductsFragment(), "ProductsFragment");
    }
    else if (id == R.id.nav_stock) {
      // Open ProductsFragment.
      openFragment(new StockFragment(), "StockFragment");
  
    }
    else if (id == R.id.nav_statistics) {
      // Open ProductsFragment.
      openFragment(new StatisticsFragment(), "StatisticsFragment");
  
    }
    else if (id == R.id.nav_devices) {
      // Open DevicesActivity.
      Intent intent = new Intent(this, DevicesActivity.class);
      startActivity(intent);
    }
    else if (id == R.id.nav_settings) {
  
    }
    
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
  
  
  private void openFragment(Fragment fragment, String tag) {
    // Pop back to HomeFragment.
    getSupportFragmentManager().popBackStackImmediate("HomeFragment", 0);
  
    // Open fragment.
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.fragmentContainer, fragment, "tag");
    ft.addToBackStack("tag");
    ft.commit();
  }
}
