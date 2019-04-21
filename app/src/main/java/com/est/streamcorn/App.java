package com.est.streamcorn;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import com.est.streamcorn.utils.PreferenceUtils;

/**
 * Created by Matteo on 04/02/2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Set the theme
        AppCompatDelegate.setDefaultNightMode(PreferenceUtils.getInstance(this).getNightMode());
    }
}
