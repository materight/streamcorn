package com.est.streamcorn.network;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.reactivex.functions.Cancellable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Matteo on 01/01/2018.
 */

public class HeadlessRequest implements Cancellable {


    private Callback<Document> successCallback;
    private Callback<Throwable> errorCallback;
    private HeadlessWebView webView;

    public interface Callback<T>{
        void consume(T response);
    }


    public static HeadlessRequest create(String url, Context context, int waitMillis, Callback<Document> successCallback, Callback<Throwable> errorCallback){
        return new HeadlessRequest(url, context, waitMillis, successCallback, errorCallback);
    }

    private HeadlessRequest(String url, Context context, int waitMillis, Callback<Document> successCallback, Callback<Throwable> errorCallback) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        webView = new HeadlessWebView(context, waitMillis);
        webView.loadUrl(url);
    }

    public class JavaScriptInterface {

        private Context context;
        private String html;

        JavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            this.html = html;
            Document document = Jsoup.parse(html);
            successCallback.consume(document);
        }
    }

    private class HeadlessWebView extends WebView {

        private final int waitMillis;

        public HeadlessWebView(Context context, int waitMillis){
            super(context);

            this.waitMillis = waitMillis;

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
            cookieManager.setAcceptCookie(false);

            WebSettings settings = this.getSettings();

            setLayerType(View.LAYER_TYPE_NONE,null);
            settings.setJavaScriptEnabled(true);
            settings.setBlockNetworkImage(true);
            settings.setDomStorageEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setLoadsImagesAutomatically(false);
            settings.setGeolocationEnabled(false);
            settings.setSupportZoom(false);
            settings.setUserAgentString(NetworkUtils.USER_AGENT);

            JavaScriptInterface jInterface = new JavaScriptInterface(context);
            this.addJavascriptInterface(jInterface, "JSInterface");

            this.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.evaluateJavascript("javascript:" +
                            "setTimeout(function(){ " +
                                "window.JSInterface.showHTML(" +
                                    "'<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>'" +
                                ");"+
                            " }," + waitMillis + ");"
                            ,null);
                }
            });
        }

        public void destroyWebView() {
            clearCache(true);
            loadUrl("about:blank");
            onPause();
            removeAllViews();
            destroyDrawingCache();
            pauseTimers();
            destroy();
        }
    }

    @Override
    public void cancel(){
        webView.stopLoading();
        webView.destroyWebView();
    }
}
