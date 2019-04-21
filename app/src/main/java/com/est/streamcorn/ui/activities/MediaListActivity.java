package com.est.streamcorn.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.est.streamcorn.R;
import com.est.streamcorn.adapters.MediaListPagerAdapter;
import com.est.streamcorn.network.channels.Channel;
import com.est.streamcorn.network.channels.ChannelService;
import com.est.streamcorn.utils.Constant;
import com.est.streamcorn.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaListActivity extends BaseActivity{

    private static final String TAG = "MediaListActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;

    private ChannelService channelService;
    private MediaListPagerAdapter mediaListPagerAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        final @Channel int channelId = intent.getIntExtra(Constant.CHANNEL_KEY, ChannelService.INVALID);
        channelService = ChannelService.getInstance(channelId);

        initTheme();

        if(channelService.hasMovie() && channelService.hasTvSeries()) {
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.title_movies)));
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.title_tv_series)));
        }
        else {
            tabLayout.setVisibility(View.GONE);
        }
        mediaListPagerAdapter = new MediaListPagerAdapter(super.getSupportFragmentManager(), channelService);
        viewPager.setAdapter(mediaListPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        search(handleSearchIntent(intent));
    }

    private String handleSearchIntent(Intent intent){
        return Intent.ACTION_SEARCH.equals(intent.getAction()) ? intent.getStringExtra(SearchManager.QUERY) : "";
    }

    private void search(String searchQuery){
        mediaListPagerAdapter.setSearchQuery(searchQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_list_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Reload default media when closing searchview
                search("");
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search: return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTheme(){
        setSupportActionBar(toolbar);
        int accent = getResources().getColor(R.color.colorAccent);
        getSupportActionBar().setTitle(Utils.getColoredString(channelService.getParametricName(), accent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
