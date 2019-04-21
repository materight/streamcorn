package com.est.streamcorn.ui.customs.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.est.streamcorn.R;
import com.est.streamcorn.network.UrlResolver;
import com.est.streamcorn.ui.activities.PlayerActivity;
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

    protected void processVideoUrl(String url, String title){
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
