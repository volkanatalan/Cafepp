package net.cafepp.cafepp.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import net.cafepp.cafepp.R;
import net.cafepp.cafepp.adapters.ViewPagerAdapter;
import net.cafepp.cafepp.databases.TableDatabase;
import net.cafepp.cafepp.fragments.TablesFragment;
import net.cafepp.cafepp.objects.TableLocation;

import java.util.List;

public class CustomerActivity extends AppCompatActivity {
  
  private TableDatabase tableDatabase;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_customer);
    
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  
    tableDatabase = new TableDatabase(this);
    List<TableLocation> tableLocations = tableDatabase.getTableLocations();
    
    ViewPager viewPager = findViewById(R.id.viewPager);
    setupViewPager(viewPager, tableLocations);
  
    TabLayout tabLayout = findViewById(R.id.tabLayout);
    tabLayout.setupWithViewPager(viewPager);
  }
  
  
  private void setupViewPager(ViewPager viewPager, List<TableLocation> locations) {
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
  
    for (TableLocation loc : locations) {
      adapter.addFragment(loc.getName(), new TablesFragment());
    }
    
    viewPager.setAdapter(adapter);
  }
}
