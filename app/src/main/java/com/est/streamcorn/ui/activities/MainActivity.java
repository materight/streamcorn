package com.est.streamcorn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.est.streamcorn.R;
import com.est.streamcorn.ui.fragments.main.ChannelFragment;
import com.est.streamcorn.utils.Utils;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    NavigationView navigationView;

    private static final String TAG = "MAinActivity";

    private Handler handler = new Handler();
    private int prevSelectedId = R.id.navigation_channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new ChannelFragment())
                .commit();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            boolean isTransparent = false;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (!isTransparent) {
                    Window window = getWindow();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.transparent));
                    isTransparent = true;
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (isTransparent) {
                    Window window = getWindow();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    isTransparent = false;
                }
            }
        };
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerSlideAnimationEnabled(false);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setTitle(Utils.getColoredString("Stream<font color=\"%s\">Corn</font>", getResources().getColor(R.color.colorAccent)));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        handler.postDelayed(() -> {
            navigate(item.getItemId());
        }, 250);
        drawerLayout.closeDrawers();
        return true;
    }

    private void navigate(int itemId) {
        Fragment navFragment = null;
        switch (itemId) {
            case R.id.navigation_channels:
                prevSelectedId = itemId;
                setTitle(R.string.app_name);
                navFragment = new ChannelFragment();
                break;
            case R.id.navigation_library:
                //  TODO
                prevSelectedId = itemId;
                setTitle(R.string.app_name);
                navFragment = new ChannelFragment();
                break;
            case R.id.navigation_favorites:
                //  TODO
                prevSelectedId = itemId;
                setTitle(R.string.app_name);
                navFragment = new ChannelFragment();
                break;
            case R.id.navigation_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                navigationView.getMenu().findItem(itemId).setChecked(false);
                navigationView.getMenu().findItem(prevSelectedId).setChecked(true);
                return;
        }

        if (navFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                transaction.replace(R.id.frame_layout, navFragment).commit();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}
