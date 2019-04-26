package com.est.streamcorn.ui.customs.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.StreamUrlAdapter;
import com.est.streamcorn.scrapers.ServerService;
import com.est.streamcorn.scrapers.models.StreamUrl;
import com.est.streamcorn.ui.activities.BaseActivity;
import com.est.streamcorn.ui.customs.transitions.MorphDialogToFab;
import com.est.streamcorn.ui.customs.transitions.MorphFabToDialog;
import io.reactivex.disposables.CompositeDisposable;

import java.util.ArrayList;

public abstract class UrlsDialog extends BaseActivity {
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.dialog_container)
    ViewGroup container;
    @BindView(R.id.title)
    TextView titleTextView;
    @BindView(R.id.urls_list)
    RecyclerView urlsList;

    private static final String TAG = "UrlsDialog";

    private String mediaTitle;
    private CompositeDisposable compositeDisposable;
    private MaterialDialog resolveDialog;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urls_dialog);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mediaTitle = intent.getStringExtra("title");
        ArrayList<StreamUrl> list = (ArrayList<StreamUrl>) intent.getSerializableExtra("list");

        StreamUrlAdapter adapter = new StreamUrlAdapter(list);
        urlsList.setLayoutManager(new LinearLayoutManager(this));
        urlsList.setAdapter(adapter);

        //  Click fuori dal dialog
        root.setOnClickListener(view -> finishAfterTransition());

        compositeDisposable = new CompositeDisposable();
        resolveDialog = new MaterialDialog.Builder(this)
                .title(R.string.please_wait)
                .content(R.string.resolving_urls)
                .progress(true, 0)
                .cancelable(false)
                .negativeText(android.R.string.cancel)
                .cancelListener(dialog -> compositeDisposable.clear())
                .dismissListener(dialog -> compositeDisposable.clear())
                .build();

        adapter.setOnItemClickListener((view, item) -> {
            if (item.isFile()) {
                processVideoUrl(mediaTitle, item.getUrl());
            } else {
                //  Show the dialog
                resolveDialog.show();

                //  Resolve the url
                compositeDisposable.add(ServerService.resolve(item.getUrl(), UrlsDialog.this)
                        .subscribe(response -> {
                            processVideoUrl(mediaTitle, response);
                            resolveDialog.dismiss();
                        }, throwable -> {
                            Log.e(TAG, "Error resolving url: ", throwable);
                            resolveDialog.dismiss();
                            int message = (throwable instanceof UnsupportedOperationException) ? R.string.server_not_supported : R.string.error_resolving_urls;
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }));
            }
        });
    }

    protected abstract void processVideoUrl(String title, String url);

    protected void setUpSharedElementTransitions(int dialogColorResourceId, int fabColorResourceId) {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog(dialogColorResourceId, fabColorResourceId);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab(dialogColorResourceId, fabColorResourceId);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        sharedEnter.addTarget(container);
        sharedReturn.addTarget(container);

        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        resolveDialog.dismiss();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }


}
