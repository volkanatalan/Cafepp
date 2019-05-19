package net.cafepp.cafepp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.fragments.HomeFragment;
import net.cafepp.cafepp.fragments.TablesFragment;

public class HomeActivity extends AppCompatActivity
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
  
    // Launch HomeFragment at the beginning.
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.fragmentContainer, new HomeFragment(), "HomeFragment");
    ft.commit();
  }
  
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
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
      getSupportFragmentManager().popBackStackImmediate();
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.fragmentContainer, new HomeFragment(), "HomeFragment");
      ft.commit();
      
    }
    else if (id == R.id.nav_tables) {
      // Open fragment.
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.fragmentContainer, new TablesFragment(), "TablesFragment");
      ft.addToBackStack("TablesFragment");
      ft.commit();
  
    }
    else if (id == R.id.nav_stock) {
  
    }
    else if (id == R.id.nav_statistics) {
  
    }
    else if (id == R.id.nav_devices) {
      Intent intent = new Intent(this, DevicesActivity.class);
      startActivity(intent);
    }
    else if (id == R.id.nav_settings) {
  
    }
    
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
