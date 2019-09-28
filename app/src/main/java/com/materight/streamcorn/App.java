package com.materight.streamcorn;

import android.app.Application;
import android.util.Log;
import androidx.appcompat.app.AppCompatDelegate;
import com.materight.streamcorn.utils.PreferenceUtils;
import io.reactivex.plugins.RxJavaPlugins;

public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        //  Set the theme
        AppCompatDelegate.setDefaultNightMode(PreferenceUtils.getInstance(this).getNightMode());

        //  Custom error handler for RxJava unhandled exceptions
        RxJavaPlugins.setErrorHandler(throwable -> Log.e(TAG, "RxJava exception not handled: ", throwable));
    }
}
