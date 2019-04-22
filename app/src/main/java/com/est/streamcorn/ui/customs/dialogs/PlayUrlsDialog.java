package com.est.streamcorn.ui.customs.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.est.streamcorn.R;
import com.est.streamcorn.utils.Utils;

/**
 * Created by Matteo on 02/02/2018.
 */

public class PlayUrlsDialog extends UrlsDialog {

    private static final String TAG = "PlayUrlsDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int dialogBackgroundColor = Utils.resolveAttr(this, R.attr.dialogBackgroundColor);
        int colorAccent = Utils.resolveAttr(this, R.attr.colorAccent);
        setUpSharedElementTransitions(dialogBackgroundColor, colorAccent);
    }

    protected void processVideoUrl(String url, String title) {
        Log.d(TAG, "Starting VLC with url: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity");
        intent.setDataAndType(Uri.parse(url), "video/*");
        startActivity(intent);
        /*Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);*/
    }
}
