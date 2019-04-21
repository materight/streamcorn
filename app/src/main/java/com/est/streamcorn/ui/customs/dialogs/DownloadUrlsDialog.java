package com.est.streamcorn.ui.customs.dialogs;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.est.streamcorn.R;
import com.est.streamcorn.network.UrlResolver;
import com.est.streamcorn.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Matteo on 02/02/2018.
 */

public class DownloadUrlsDialog extends UrlsDialog {

    private static final String TAG = "DownloadUrlsDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int dialogBackgroundColor = Utils.resolveAttr(this, R.attr.dialogBackgroundColor);
        int colorBackground = Utils.resolveAttr(this, android.R.attr.colorBackground);
        setUpSharedElementTransitions(dialogBackgroundColor, colorBackground);
    }

    @Override
    protected void processVideoUrl(String videoUrl, String title) {
        Log.d(TAG, "downloading " + videoUrl);
        Uri url = Uri.parse(videoUrl);
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (Utils.canDownload(this)) {
            DownloadManager.Request request = new DownloadManager.Request(url);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(Utils.getDownloadAllowedNetwork(this));
            //Set whether this download may proceed over a roaming connection.
            request.setAllowedOverRoaming(false);
            //Set the title of this download, to be displayed in notifications (if enabled).
            request.setTitle(title);
            //Set a description of this download, to be displayed in notifications (if enabled)
            request.setDescription("Downloading File");
            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp4");
            //Enqueue a new download and same the referenceId
            long refId = downloadManager.enqueue(request);
        } else {
            //Show dialog
        }
    }
}
