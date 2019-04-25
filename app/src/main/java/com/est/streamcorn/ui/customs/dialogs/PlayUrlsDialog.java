package com.est.streamcorn.ui.customs.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.est.streamcorn.R;
import com.est.streamcorn.utils.Utils;

public class PlayUrlsDialog extends UrlsDialog {

    private static final String TAG = "PlayUrlsDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleTextView.setText(getString(R.string.play_dialog_title));
        int dialogBackgroundColor = Utils.resolveAttr(this, R.attr.dialogBackgroundColor);
        int colorAccent = Utils.resolveAttr(this, R.attr.colorAccent);
        setUpSharedElementTransitions(dialogBackgroundColor, colorAccent);
    }

    protected void processVideoUrl(String title, String url) {
        Log.d(TAG, "Starting VLC with url: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity");
        intent.setDataAndType(Uri.parse(url), "video/*");
        intent.putExtra("title", title);
        startActivity(intent);
        /*Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);*/
    }
}
