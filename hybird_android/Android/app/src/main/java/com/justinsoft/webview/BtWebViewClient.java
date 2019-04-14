package com.justinsoft.webview;

import com.justinsoft.util.LogUtil;

import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BtWebViewClient extends WebViewClient
{
    // 日志标记
    private static final String TAG = LogUtil.getClassTag(BtWebViewClient.class);
    
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        Log.i(TAG, "start to override url loading 0");
        view.loadUrl(url);
        return true;
    }
    
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
    {
        super.onReceivedHttpError(view, request, errorResponse);
        Log.e(TAG, "failed to receive msg");
    }
    
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
    {
        // 注意：super句话一定要删除，或者注释掉，否则又走handler.cancel() 默认的不支持https的了。
        // super.onReceivedSslError(view, handler, error);
        Log.e(TAG, "failed to receive ssl msg");
        // 接受所有网站的证书
        handler.proceed();
    }
}
