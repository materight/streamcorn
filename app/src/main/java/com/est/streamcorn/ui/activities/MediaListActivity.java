package com.est.streamcorn.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.MediaListPagerAdapter;
import com.est.streamcorn.scrapers.ChannelService;
import com.est.streamcorn.scrapers.channels.Channel;
import com.est.streamcorn.utils.Constants;
import com.est.streamcorn.utils.Utils;
import com.google.android.material.tabs.TabLayout;

public class MediaListActivity extends BaseActivity {

    private static final String TAG = "MediaListActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private Channel channel;
    private MediaListPagerAdapter mediaListPagerAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        final String channelId = intent.getStringExtra(Constants.CHANNEL_KEY);
        channel = ChannelService.getChannelInstance(channelId, MediaListActivity.this);

        initTheme();

        if (channel == null) {
            Toast.makeText(MediaListActivity.this, R.string.channel_unknown, Toast.LENGTH_SHORT).show();
        } else if (channel.getProperties().hasMovies() && channel.getProperties().hasTvSeries()) {
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.title_movies)));
            tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.title_tv_series)));
        } else {
            tabLayout.setVisibility(View.GONE);
        }
        mediaListPagerAdapter = new MediaListPagerAdapter(super.getSupportFragmentManager(), channel);
        viewPager.setAdapter(mediaListPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        search(handleSearchIntent(intent));
    }

    private String handleSearchIntent(Intent intent) {
        return Intent.ACTION_SEARCH.equals(intent.getAction()) ? intent.getStringExtra(SearchManager.QUERY) : "";
    }

    private void search(String searchQuery) {
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
                //  Reload default media when closing searchview
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

    private void initTheme() {
        this.setSupportActionBar(toolbar);
        int accent = getResources().getColor(R.color.colorAccent, this.getTheme());
        this.getSupportActionBar().setTitle(Utils.getColoredString(channel.getProperties().getParametricName(), accent));
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
