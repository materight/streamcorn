package com.est.streamcorn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Created by Matteo on 06/02/2018.
 */

public class PreferenceUtils {

    public static final String GENERAL_THEME = "general_theme";

    private static final String STREAM_NETWORK_POLICY = "stream_network_policy";
    private static final String DOWNLOAD_NETWORK_POLICY = "download_network_policy";

    private static PreferenceUtils sInstance;
    private final SharedPreferences mPreferences;

    private PreferenceUtils(@NonNull final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtils getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    public final int getNightMode() {
        return getNightModeInt(mPreferences.getString(GENERAL_THEME, "daytime"));
    }

    public static int getNightModeInt(String nightMode) {
        switch (nightMode) {
            case "dark":
                return AppCompatDelegate.MODE_NIGHT_YES;
            case "light":
                return AppCompatDelegate.MODE_NIGHT_NO;
            default:
                return AppCompatDelegate.MODE_NIGHT_AUTO;
        }
    }

    public final String getStreamNetworkPolicy() {
        return mPreferences.getString(STREAM_NETWORK_POLICY, "only_wifi");
    }

    public final String getDownloadNetworkPolicy() {
        return mPreferences.getString(DOWNLOAD_NETWORK_POLICY, "only_wifi");
    }

}
