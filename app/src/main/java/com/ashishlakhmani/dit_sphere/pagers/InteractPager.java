package com.ashishlakhmani.dit_sphere.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ashishlakhmani.dit_sphere.fragments.interact.FollowedThread;
import com.ashishlakhmani.dit_sphere.fragments.interact.OthersThread;

public class InteractPager extends FragmentStatePagerAdapter {
    private int numOfTabs;

    public InteractPager(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FollowedThread();

            case 1:
                return new OthersThread();
        }

        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}
