package com.materight.streamcorn.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private int previousTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        previousTheme = AppCompatDelegate.getDefaultNightMode();
        super.onCreate(savedInstanceState);
    }

    protected void updateTheme() {
        if (previousTheme != AppCompatDelegate.getDefaultNightMode()) {
            recreate();
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }

    @Override
    protected void onResume() {
        updateTheme();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
