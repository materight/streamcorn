package com.est.streamcorn.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.est.streamcorn.adapters.base.FragmentStatePagerAdapter;
import com.est.streamcorn.network.channels.ChannelService;
import com.est.streamcorn.ui.fragments.MediaListFragment;

public class MediaListPagerAdapter extends FragmentStatePagerAdapter {

    private ChannelService channelService;
    private int fragmentCount;
    private String searchQuery;

    public MediaListPagerAdapter(FragmentManager fm, ChannelService channelService) {
        super(fm);
        this.channelService = channelService;
        this.fragmentCount = (channelService.hasMovie() && channelService.hasTvSeries()) ? 2 : 1;
    }

    @Override
    public Fragment getItem(int position) {
        if(fragmentCount == 2){
            switch (position){
                case 0: return MediaListFragment.newInstance(channelService.getChannelId(), MediaListFragment.MOVIE_LIST);
                case 1: return MediaListFragment.newInstance(channelService.getChannelId(), MediaListFragment.TV_SERIES_LIST);
                default: return null;
            }
        }
        else if(position == 0){
            if(channelService.hasMovie())
                return MediaListFragment.newInstance(channelService.getChannelId(), MediaListFragment.MOVIE_LIST);
            else
                return MediaListFragment.newInstance(channelService.getChannelId(), MediaListFragment.TV_SERIES_LIST);
        }
        return null;
    }

    public void setSearchQuery(String searchQuery){
        for (Fragment fragment: mFragments) {
            if(fragment != null)
                ((MediaListFragment) fragment).setSearchQuery(searchQuery);
        }
    }

    @Override
    public int getCount() {
        return fragmentCount;
    }
}