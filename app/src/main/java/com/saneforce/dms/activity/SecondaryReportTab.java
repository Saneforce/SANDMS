package com.saneforce.dms.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentHostCallback;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.saneforce.dms.R;
import com.saneforce.dms.adapter.ReportViewAdapter;
import com.saneforce.dms.sqlite.DBController;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class SecondaryReportTab extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    DotsIndicator dotsIndicator;
    FragmentHostCallback<?> mHost;
    private FragmentManager mChildFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_report_tab);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.dots_indicator);

        LinearLayout llToolbar = findViewById(R.id.ll_toolbar);

        DBController dbController = new DBController(SecondaryReportTab.this);

        tabLayout.addTab(tabLayout.newTab().setText("Order"));
        tabLayout.addTab(tabLayout.newTab().setText("No Order"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        GeoTagTabAdapter adapter = new GeoTagTabAdapter(SecondaryReportTab.this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    public class GeoTagTabAdapter extends FragmentPagerAdapter {

        int totalTabs;

        public GeoTagTabAdapter(Context context, FragmentManager fm, int totalTabs) {
            super(fm);
            this.totalTabs = totalTabs;
        }
        // this is for fragment tabs
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //retailer
                    return SecondaryNoOrderFragment.newInstance(1);
                case 1:
                    //distributor
                    return SecondaryNoOrderFragment.newInstance(2);
                default:
                    return null;
            }
        }
        // this counts total number of tabs
        @Override
        public int getCount() {
            return totalTabs;
        }
    }


    @Override
    public void onDestroy() {
        tabLayout = null;
        viewPager = null;
        dotsIndicator = null;
        super.onDestroy();
    }
}