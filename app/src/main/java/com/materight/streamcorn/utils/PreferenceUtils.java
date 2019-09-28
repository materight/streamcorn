package com.materight.streamcorn.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;


public class PreferenceUtils {

    public static final String GENERAL_THEME = "general_theme";

    private static final String STREAM_NETWORK_POLICY = "stream_network_policy";
    private static final String DOWNLOAD_NETWORK_POLICY = "download_network_policy";

    private static PreferenceUtils instance;
    private final SharedPreferences sharedPreferences;

    private PreferenceUtils(@NonNull final Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtils getInstance(@NonNull final Context context) {
        if (instance == null) {
            instance = new PreferenceUtils(context.getApplicationContext());
        }
        return instance;
    }

    public final int getNightMode() {
        return getNightModeInt(sharedPreferences.getString(GENERAL_THEME, "daytime"));
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
        return sharedPreferences.getString(STREAM_NETWORK_POLICY, "only_wifi");
    }

    public final String getDownloadNetworkPolicy() {
        return sharedPreferences.getString(DOWNLOAD_NETWORK_POLICY, "only_wifi");
    }

}
