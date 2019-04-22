package com.est.streamcorn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.est.streamcorn.R;
import com.est.streamcorn.ui.fragments.main.ChannelFragment;
import com.est.streamcorn.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    BottomNavigationView navigationView;

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
        navigationView.setOnNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle(Utils.getColoredString("Stream<font color=\"%s\">Corn</font>", getResources().getColor(R.color.colorAccent, getTheme())));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        navigate(item.getItemId());
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        MenuItem settingsMenuItem = menu.findItem(R.id.settings);
        settingsMenuItem.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        });
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
            case R.id.navigation_downloads:
                //  TODO
                prevSelectedId = itemId;
                setTitle(R.string.app_name);
                navFragment = new ChannelFragment();
                break;
        }

        if (navFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                transaction.replace(R.id.frame_layout, navFragment).commit();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error replacing Fragment " + e);
            }
        }
    }
}
