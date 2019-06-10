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
        Log.d(TAG, "Created HeadlessREquest");
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

        //  Estensioni dei file che non devono essere caricati dalla webview
        private final String[] INGORED_EXTENSIONS = {"css", "ttf", "woff", "png", "jpg", "jpeg"};

        //  Delay nel caso di portezione Cloudfare
        private final int CLOUDFARE_DELAY = 5000;

        private boolean isDisposed;

        private HeadlessWebView(String userAgent, final int delay, Context context) {
            super(context);

            this.isDisposed = false;


            WebSettings settings = this.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBlockNetworkImage(true);
            settings.setDomStorageEnabled(true);
            settings.setLoadsImagesAutomatically(false);
            settings.setGeolocationEnabled(false);
            settings.setSupportZoom(false);
            settings.setUserAgentString(userAgent);

            this.setLayerType(View.LAYER_TYPE_NONE, null);
            this.addJavascriptInterface(new JavaScriptInterface(), "JSInterface");
            this.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (webView != null) {
                        webView.evaluateJavascript("javascript: document.getElementsByClassName('cf-browser-verification').length;", value -> {
                            if (Integer.parseInt(value) > 0) {
                                Log.d(TAG, "CloudFlare detected! Waiting for redirect...");
                            } else {
                                webView.evaluateJavascript("javascript:" +
                                        "setTimeout(function(){ " +
                                        "   window.JSInterface.showHTML(" +
                                        "       document.getElementsByTagName('html')[0].innerHTML" +
                                        "   );" +
                                        " },  " + delay + " );", null);
                            }
                        });
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (request.isRedirect() && webView != null) {
                        Log.d(TAG, "Redirecting to " + request.getUrl());
                        webView.loadUrl(request.getUrl().toString());
                        return true;
                    } else {
                        return false;
                    }
                }

                // TODO: Ignore css, images, font
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    if(request.isForMainFrame()){
                        onError.accept(new Exception(request.getUrl() + "  " + error.getErrorCode() + ": " + error.getDescription().toString()));
                    }
                }
            });

            this.setWebChromeClient(new WebChromeClient() {

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    //  Abilitare per mostrare i log della webview
                    //  Log.d(TAG, "ConsoleMessage from WebView: " + consoleMessage.message());
                    return true;
                }
            });
        }

        private void destroyWebView() {
            this.post(() -> {
                this.removeAllViews();
                this.clearCache(false);
                this.loadUrl("about:blank");
                this.onPause();
                this.removeAllViews();
                this.destroy();
                this.isDisposed = true;
            });
        }
    }

    @Override
    public void dispose() {
        this.webView.destroyWebView();
        this.webView = null;
    }

    @Override
    public boolean isDisposed() {
        return (this.webView == null || this.webView.isDisposed);
    }
}
