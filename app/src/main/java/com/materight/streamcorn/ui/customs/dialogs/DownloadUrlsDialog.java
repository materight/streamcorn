package com.materight.streamcorn.ui.customs.dialogs;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.materight.streamcorn.R;
import com.materight.streamcorn.utils.Utils;

public class DownloadUrlsDialog extends UrlsDialog {

    private static final String TAG = "DownloadUrlsDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleTextView.setText(getString(R.string.download_dialog_title));
        int dialogBackgroundColor = Utils.resolveAttr(this, R.attr.dialogBackgroundColor);
        int colorBackground = Utils.resolveAttr(this, android.R.attr.colorBackground);
        setUpSharedElementTransitions(dialogBackgroundColor, colorBackground);
    }

    @Override
    protected void processVideoUrl(String title, String mediaUrl) {
        Log.d(TAG, "Downloading " + mediaUrl);
        Uri url = Uri.parse(mediaUrl);
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (Utils.canDownload(this)) {
            DownloadManager.Request request = new DownloadManager.Request(url);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //  Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(Utils.getDownloadAllowedNetwork(this));
            //  Set whether this download may proceed over a roaming connection.
            request.setAllowedOverRoaming(false);
            //  Set the title of this download, to be displayed in notifications (if enabled).
            request.setTitle(title);
            //  Set a description of this download, to be displayed in notifications (if enabled)
            request.setDescription("Downloading File");
            //  Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp4");
            //  Enqueue a new download and same the referenceId
            long refId = downloadManager.enqueue(request);
        } else {
            //  Show permission dialog
        }
    }
}
