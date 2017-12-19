package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.omneagate.erbc.Adapter.TabPagerAdapter;
import com.omneagate.erbc.R;

public class ConnectionRegistrationFlipper extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_registration_flipper);
     /*   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        setTitle(getResources().getText(R.string.title));*/


        Toolbar   toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);

        title = (TextView) findViewById(R.id.title_toolbar);
       /* title.setText(R.string.print);

        setTitle("");
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.outward_size));*/



      //  getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(ConnectionRegistrationFlipper.this, ComplaintListActivity.class);
                startActivity(registerPage);
                finish();
            }
        });
        setupViews();
    }
    public void changeSize(String name,Float dem){
        title.setText(name);
        title.setTextSize(dem);

    }
    private void setupViews() {
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ServiceFragment(), "Service");
        adapter.addFragment(new ApplicationFragment(), "Application");
        viewPager.setAdapter(adapter);

    }
}
