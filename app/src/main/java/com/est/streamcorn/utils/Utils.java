package com.est.streamcorn.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class Utils {

    private static final String TAG = "Utils";

    public static Spanned getColoredString(String string, @Nullable Integer color) {
        if (color == null) {
            return Html.fromHtml(String.format(string, ""));
        } else {
            String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(color), Color.green(color), Color.blue(color))));
            return Html.fromHtml(String.format(string, htmlColor));
        }
    }

    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static int resolveAttr(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.resourceId;
    }

    public static String encodeQuery(String query) {
        try {
            return URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Encoding query error, query: " + query);
            return "";
        }
    }

    private static boolean resolveNetworkPolicy(final Context context, String policy) {
        switch (policy) {
            case "always":
                return true;
            default:
            case "only_wifi":
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                return netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnectedOrConnecting();
        }
    }

    public static boolean canStream(final Context context) {
        return resolveNetworkPolicy(context, PreferenceUtils.getInstance(context).getStreamNetworkPolicy());
    }

    public static boolean canDownload(final Context context) {
        return resolveNetworkPolicy(context, PreferenceUtils.getInstance(context).getDownloadNetworkPolicy());
    }

    public static int getDownloadAllowedNetwork(final Context context) {
        String policy = PreferenceUtils.getInstance(context).getDownloadNetworkPolicy();
        switch (policy) {
            case "always":
                return DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE;
            default:
            case "only_wifi":
                return DownloadManager.Request.NETWORK_WIFI;
        }
    }
}
