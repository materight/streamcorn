package com.est.streamcorn.ui.customs.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import androidx.core.app.ActivityOptionsCompat;
import com.est.streamcorn.R;
import com.est.streamcorn.scrapers.models.StreamUrl;

import java.util.ArrayList;

public class MorphDialog {

    private static final String TAG = "MorphDialog";

    private enum Type {
        PLAY_URLS,
        DOWNLOAD_URLS
    }

    private Activity activity;
    private Type type;
    private String title = "";
    private View animationView = null;
    private ArrayList<StreamUrl> urls;

    private MorphDialog() {
    }

    private static MorphDialog Builder(Activity activity) {
        MorphDialog md = new MorphDialog();
        md.activity = activity;
        return md;
    }

    public static MorphDialog PlayDialog(Activity activity) {
        MorphDialog md = Builder(activity);
        md.type = Type.PLAY_URLS;
        md.urls = new ArrayList<>();
        return md;
    }

    public static MorphDialog DownloadDialog(Activity activity) {
        MorphDialog md = Builder(activity);
        md.type = Type.DOWNLOAD_URLS;
        md.urls = new ArrayList<>();
        return md;
    }

    public MorphDialog title(String title) {
        this.title = title;
        return this;
    }

    public MorphDialog streamUrls(ArrayList<StreamUrl> urls) {
        this.urls = urls;
        return this;
    }

    public MorphDialog withMorphAnimationFrom(View view) {
        this.animationView = view;
        return this;
    }

    public void show() {
        Class<?> dialogClass = null;
        switch (type) {
            case PLAY_URLS:
                dialogClass = PlayUrlsDialog.class;
                break;
            case DOWNLOAD_URLS:
                dialogClass = DownloadUrlsDialog.class;
                break;
        }
        Intent dialogIntent = new Intent(activity, dialogClass);
        dialogIntent.putExtra("list", urls);
        dialogIntent.putExtra("title", title);
        if (animationView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, animationView, activity.getString(R.string.transition_dialog));
            activity.startActivity(dialogIntent, options.toBundle());
        } else {
            activity.startActivity(dialogIntent);
        }

    }


}
