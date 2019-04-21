package com.est.streamcorn.network.utils;

import android.content.Context;
import android.view.View;
import android.webkit.*;
import androidx.core.util.Consumer;
import io.reactivex.functions.Cancellable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HeadlessRequest implements Cancellable {

    private Consumer<Document> onSuccess;
    private Consumer<Throwable> onError;
    private HeadlessWebView webView;

    public HeadlessRequest(String url, String userAgent, int delay, Context context, Consumer<Document> onSuccess, Consumer<Throwable> onError) {
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

        private HeadlessWebView(String userAgent, final int delay, Context context) {
            super(context);

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
                    webView.evaluateJavascript("javascript:" +
                                    "setTimeout(function(){ " +
                                    "window.JSInterface.showHTML(" +
                                    "'<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>'" +
                                    ");" +
                                    " }," + delay + ");"
                            , null);
                }
            });
        }

        private void destroyWebView() {
            this.clearCache(true);
            this.loadUrl("about:blank");
            this.onPause();
            this.removeAllViews();
            this.destroyDrawingCache();
            this.pauseTimers();
            this.destroy();
        }

    }

    @Override
    public void cancel() {
        this.webView.stopLoading();
        this.webView.destroyWebView();
    }
}
