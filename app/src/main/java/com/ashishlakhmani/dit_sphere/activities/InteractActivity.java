package com.ashishlakhmani.dit_sphere.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.pagers.TabPager;

public class InteractActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interact);

        initialize();
        tabTask();
        toolBarTask();
    }

    private void initialize() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.interact_view_pager);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);
    }

    private void tabTask() {

        TabLayout.Tab tab1 = tabLayout.newTab();
        tabLayout.addTab(tab1);

        TabLayout.Tab tab2 = tabLayout.newTab();
        tabLayout.addTab(tab2);

        //link tab layout with viewpager
        tabLayout.setupWithViewPager(viewPager);

        final TabPager adapter = new TabPager
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //ViewPager sets case 0 tab automatically..

        tab1.setIcon(R.drawable.interact_3);
        tab1.setText("Other Threads");

        tab2.setIcon(R.drawable.interact);
        tab2.setText("Followed Threads");


        // addOnPageChangeListener event change the tab on slide
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }


    private void toolBarTask() {
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(appBarLayout, 0);
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        setToolbarTitle(sharedPreferences.getString("branch", "").toUpperCase() + " Department");
    }

    public void setToolbarTitle(String title) {
        setTitle("");
        TextView toolbar_text = findViewById(R.id.toolbar_text);
        toolbar_text.setText(title);
    }

}
