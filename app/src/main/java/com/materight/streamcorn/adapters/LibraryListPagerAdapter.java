package com.materight.streamcorn.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.materight.streamcorn.adapters.base.FragmentStatePagerAdapter;
import com.materight.streamcorn.scrapers.models.MediaType;
import com.materight.streamcorn.ui.fragments.LibraryListFragment;

public class LibraryListPagerAdapter extends FragmentStatePagerAdapter {

    private String searchQuery;

    public LibraryListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LibraryListFragment.newInstance(MediaType.MOVIE);
            case 1:
                return LibraryListFragment.newInstance(MediaType.TV_SERIES);
            default:
                return null;
        }
    }

    public void setSearchQuery(String searchQuery) {
        for (Fragment fragment : mFragments) {
            if (fragment != null)
                ((LibraryListFragment) fragment).setSearchQuery(searchQuery);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
