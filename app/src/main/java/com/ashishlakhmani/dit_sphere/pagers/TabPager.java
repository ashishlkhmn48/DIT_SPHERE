package com.ashishlakhmani.dit_sphere.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ashishlakhmani.dit_sphere.fragments.interact.FollowedThread;
import com.ashishlakhmani.dit_sphere.fragments.interact.OthersThread;

public class TabPager extends FragmentStatePagerAdapter {
    private int numOfTabs;
    FollowedThread followedThread;
    OthersThread othersThread;


    public TabPager(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        FollowedThread followedThread = new FollowedThread();
        this.followedThread = followedThread;
        OthersThread othersThread = new OthersThread();
        this.othersThread = othersThread;

        switch (position) {
            case 0:
                return followedThread;

            case 1:
                return othersThread;
        }

        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}
