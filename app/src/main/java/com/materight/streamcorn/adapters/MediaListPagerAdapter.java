package com.materight.streamcorn.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.materight.streamcorn.adapters.base.FragmentStatePagerAdapter;
import com.materight.streamcorn.scrapers.channels.Channel;
import com.materight.streamcorn.scrapers.models.MediaType;
import com.materight.streamcorn.ui.fragments.MediaListFragment;

public class MediaListPagerAdapter extends FragmentStatePagerAdapter {

    private Channel channel;
    private int fragmentCount;
    private String searchQuery;

    public MediaListPagerAdapter(FragmentManager fm, Channel channel) {
        super(fm);
        this.channel = channel;
        this.fragmentCount = (channel.getProperties().hasMovies() && channel.getProperties().hasTvSeries()) ? 2 : 1;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragmentCount == 2) {
            switch (position) {
                case 0:
                    return MediaListFragment.newInstance(channel.getProperties().getDomain(), MediaType.MOVIE);
                case 1:
                    return MediaListFragment.newInstance(channel.getProperties().getDomain(), MediaType.TV_SERIES);
                default:
                    return null;
            }
        } else if (position == 0) {
            if (channel.getProperties().hasMovies())
                return MediaListFragment.newInstance(channel.getProperties().getDomain(), MediaType.MOVIE);
            else
                return MediaListFragment.newInstance(channel.getProperties().getDomain(), MediaType.TV_SERIES);
        }
        return null;
    }

    public void setSearchQuery(String searchQuery) {
        for (Fragment fragment : mFragments) {
            if (fragment != null)
                ((MediaListFragment) fragment).setSearchQuery(searchQuery);
        }
    }

    @Override
    public int getCount() {
        return fragmentCount;
    }
}