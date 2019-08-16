package com.example.packlest;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class ViewPagerAdapter extends FragmentPagerAdapter {

    ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new PackingListFragment();
        } else if (position == 1) {
            fragment = new ItemFragment();
        } else if (position == 2) {
            fragment = new ItemCategoryFragment();
        } else if (position == 3) {
            fragment = new TripParameterFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Lists";
        } else if (position == 1) {
            title = "Items";
        } else if (position == 2) {
            title = "Item Categories";
        } else if (position == 3) {
            title = "Trip Params";
        }
        return title;
    }
}

