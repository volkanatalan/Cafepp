package net.cafepp.cafepp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.ViewPagerAdapter;
import net.cafepp.cafepp.fragments.ProductsFragment;
import net.cafepp.cafepp.fragments.StockFragment;
import net.cafepp.cafepp.fragments.TablesFragment;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.ArrayList;
import java.util.List;

public class CookActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cook);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    
    List<TableLocation> tableLocations = new ArrayList<>();
    
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  
    ViewPager viewPager = findViewById(R.id.viewPager);
    setupViewPager(viewPager, tableLocations);
  
    TabLayout tabLayout = findViewById(R.id.tabLayout);
    tabLayout.setupWithViewPager(viewPager);
  
    /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.fragmentContainer, TablesFragment.newInstance(getSupportFragmentManager()))
        .commit();*/
  }
  
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.cook, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    
    return super.onOptionsItemSelected(item);
  }
  
  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    
    if (id == R.id.nav_orders) {
      List<Fragment> fragments = getSupportFragmentManager().getFragments();
      Fragment currentFragment = fragments.get(fragments.size() - 1);
  
      if (currentFragment instanceof TablesFragment) {
        // Do nothing.
      } else {
        getSupportFragmentManager().popBackStack();
      }
      
    } else if (id == R.id.nav_stock) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.fragmentContainer, StockFragment.newInstance(), "StockFragment")
        .addToBackStack("StockFragment")
        .commit();
    
    } else if (id == R.id.nav_products) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.fragmentContainer, ProductsFragment.newInstance(), "ProductsFragment")
          .addToBackStack("StockFragment")
          .commit();
    
    }
    
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
  
  private void setupViewPager(ViewPager viewPager, List<TableLocation> locations) {
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    
    for (TableLocation loc : locations) {
      adapter.addFragment(loc.getName(), new TablesFragment());
    }
    
    viewPager.setAdapter(adapter);
  }
}
