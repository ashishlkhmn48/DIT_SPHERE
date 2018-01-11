package com.ashishlakhmani.dit_sphere.pagers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ashishlakhmani.dit_sphere.fragments.result.ResultViewer;

public class ResultPager extends FragmentStatePagerAdapter {

    private int num_of_tabs;

    public ResultPager(FragmentManager fm, int num_of_tabs) {
        super(fm);
        this.num_of_tabs = num_of_tabs;
    }

    @Override
    public Fragment getItem(int position) {
        ResultViewer resultViewer = new ResultViewer();
        Bundle bundle = new Bundle();
        bundle.putString("semester", String.valueOf(position + 1));
        resultViewer.setArguments(bundle);
        return resultViewer;
    }

    @Override
    public int getCount() {
        return num_of_tabs;
    }


}
