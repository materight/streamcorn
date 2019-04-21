package com.est.streamcorn.ui.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by Matteo on 04/02/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private int previousTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        previousTheme = AppCompatDelegate.getDefaultNightMode();
        super.onCreate(savedInstanceState);
    }

    protected void updateTheme(){
        if(previousTheme != AppCompatDelegate.getDefaultNightMode()) {
            Log.d(TAG, "updateTheme()");
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
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
