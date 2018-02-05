package com.ashishlakhmani.dit_sphere.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.ashishlakhmani.dit_sphere.pagers.ClubPager;


public class Club extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;

    public Club() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        initialize(view);
        tabTask();

        return view;
    }

    private void initialize(View view) {

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.club_view_pager);

    }

    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).setToolbarTitle("Clubs");
        super.onResume();
    }

    private void tabTask() {

        TabLayout.Tab tab1 = tabLayout.newTab();
        tabLayout.addTab(tab1);

        TabLayout.Tab tab2 = tabLayout.newTab();
        tabLayout.addTab(tab2);

        //link tab layout with viewpager
        tabLayout.setupWithViewPager(viewPager);

        final ClubPager pager = new ClubPager(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pager);
        //ViewPager sets case 0 tab automatically..

        tab1.setIcon(R.drawable.interact_3);
        tab1.setText("Other Clubs");

        tab2.setIcon(R.drawable.interact);
        tab2.setText("Followed Clubs");

        // addOnPageChangeListener event change the tab on slide
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

}
