package com.ashishlakhmani.dit_sphere.pagers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ashishlakhmani.dit_sphere.fragments.club.FollowedClub;
import com.ashishlakhmani.dit_sphere.fragments.club.OthersClub;

public class ClubPager extends FragmentStatePagerAdapter{

    private int numOfTabs;

    public ClubPager(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FollowedClub();

            case 1:
                return new OthersClub();
        }

        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
