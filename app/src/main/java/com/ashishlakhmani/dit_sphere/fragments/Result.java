package com.ashishlakhmani.dit_sphere.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.pagers.ResultPager;

import java.util.ArrayList;
import java.util.List;


public class Result extends Fragment {

    public Result() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        List<TabLayout.Tab> tabsList = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tabsList.add(tab);
            tabLayout.addTab(tab);
        }

        tabLayout.setupWithViewPager(viewPager);

        ResultPager resultPager = new ResultPager(getFragmentManager(), tabsList.size());
        viewPager.setAdapter(resultPager);

        for (int i = 1; i <= 8; i++) {
            if (i == 1)
                tabsList.get(8 - i).setText("1st");
            else if (i == 2)
                tabsList.get(8 - i).setText("2nd");
            else if (i == 3)
                tabsList.get(8 - i).setText("3rd");
            else
                tabsList.get(8 - i).setText(i + "th");
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager() != null) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        ViewCompat.setElevation(((HomeActivity) getActivity()).bottomNavigationView, 0);

                    } else {
                        ViewCompat.setElevation(((HomeActivity) getActivity()).bottomNavigationView, 100);
                        ((HomeActivity) getActivity()).setToolbarTitle("News");
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).setToolbarTitle("Result");
        super.onResume();
    }
}
