package com.est.streamcorn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.est.streamcorn.R;
import com.est.streamcorn.utils.PreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initTheme();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SettingsFragment())
                .commit();
    }


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            int currentTheme = AppCompatDelegate.getDefaultNightMode();

            // Restart on theme change
            findPreference(PreferenceUtils.GENERAL_THEME).setOnPreferenceChangeListener((preference, newValue) -> {
                int newTheme = PreferenceUtils.getNightModeInt((String) newValue);
                if(currentTheme == newTheme)
                    return false;

                AppCompatDelegate.setDefaultNightMode(newTheme);
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
                return true;
            });
        }
    }


    private void initTheme(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
