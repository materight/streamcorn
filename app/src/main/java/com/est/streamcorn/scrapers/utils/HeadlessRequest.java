package com.est.streamcorn.scrapers.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import androidx.core.util.Consumer;
import io.reactivex.disposables.Disposable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HeadlessRequest implements Disposable {

    private static final String TAG = "HeadlessRequest";

    private Consumer<Document> onSuccess;
    private Consumer<Throwable> onError;
    private HeadlessWebView webView;

    public HeadlessRequest(String url, String userAgent, int delay, Context context, Consumer<Document> onSuccess, Consumer<Throwable> onError) {
        Log.d(TAG, "Creating on thread " + Thread.currentThread().getId());
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.webView = new HeadlessWebView(userAgent, delay, context);
        this.webView.loadUrl(url);
    }

    private class JavaScriptInterface {
        @JavascriptInterface
        public void showHTML(String html) {
            onSuccess.accept(Jsoup.parse(html));
        }
    }

    private class HeadlessWebView extends WebView {

        private boolean isDisposed;

        private HeadlessWebView(String userAgent, final int delay, Context context) {
            super(context);

            this.isDisposed = false;

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
            cookieManager.setAcceptCookie(false);

            WebSettings settings = this.getSettings();

            setLayerType(View.LAYER_TYPE_NONE, null);
            settings.setJavaScriptEnabled(true);
            settings.setBlockNetworkImage(true);
            settings.setDomStorageEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setLoadsImagesAutomatically(false);
            settings.setGeolocationEnabled(false);
            settings.setSupportZoom(false);
            settings.setUserAgentString(userAgent);

            this.addJavascriptInterface(new JavaScriptInterface(), "JSInterface");
            this.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    view.evaluateJavascript("javascript:" +
                                    "setTimeout(function(){ " +
                                    "window.JSInterface.showHTML(" +
                                    "'<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>'" +
                                    ");" +
                                    " }," + delay + ");"
                            , null);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    onError.accept(new Exception(error.getDescription().toString()));
                }
            });

            this.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    Log.d(TAG, "From WebView: " + consoleMessage.message());
                    return true;
                }
            });
        }

        private void destroyWebView() {
            this.clearCache(true);
            this.loadUrl("about:blank");
            this.onPause();
            this.removeAllViews();
            this.pauseTimers();
            this.destroy();
            this.isDisposed = true;
        }

    }

    @Override
    public void dispose() {
        Log.d(TAG, "Disposing on thread " + Thread.currentThread().getId());
        this.webView.stopLoading();
        this.webView.destroyWebView();
    }

    @Override
    public boolean isDisposed() {
        return this.webView.isDisposed;
    }
}
