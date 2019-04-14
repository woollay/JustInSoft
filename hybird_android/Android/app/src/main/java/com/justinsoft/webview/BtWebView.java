package com.justinsoft.webview;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.justinsoft.util.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 复写webview，添加进度条显示效果
 */
public class BtWebView extends WebView
{
    // 日志标记
    private static final String TAG = LogUtil.getClassTag(BtWebView.class);
    
    private BtWebChromeClient webChromeClient;
    
    private JsCallback jsCallback;
    
    public BtWebView(Context context)
    {
        super(context);
    }
    
    public BtWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        this.webChromeClient = new BtWebChromeClient(context);
        setWebChromeClient(this.webChromeClient);
        this.webChromeClient.addProgressBar(this);
        
        setWebViewClient(new BtWebViewClient());
        initSettings();
    }
    
    public BtWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    
    public BtWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    /**
     * 绑定activity
     * 
     * @param activity
     */
    public void bind(Activity activity)
    {
        // this.activity = activity;
        this.webChromeClient.bind(activity);
        
        // 添加给js回调的接口
        jsCallback = new JsCallback(activity, this);
        addJavascriptInterface(jsCallback, "jsCallback");
    }
    
    public void cropCallback(int resultCode)
    {
        this.webChromeClient.cropCallback(resultCode, null);
    }
    
    /**
     * 裁剪照片
     * 
     * @param resultCode
     */
    public void cropPicture(int resultCode)
    {
        this.webChromeClient.cropPicture(resultCode);
    }
    
    private void initSettings()
    {
        WebSettings webSettings = getSettings();
        // 让WebView能够执行javaScript
        webSettings.setJavaScriptEnabled(true);
        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式,一共有四种模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
        // webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // 设置webview保存表单数据
        webSettings.setSaveFormData(true);
        webSettings.setSupportMultipleWindows(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
        webSettings.setDisplayZoomControls(false);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(12);
        // 设置默认编码
        webSettings.setDefaultTextEncodingName("UTF-8");
        // 启用地理定位
        webSettings.setGeolocationEnabled(true);
        // ***最重要的方法，一定要设置，这就是出不来的主要原因
        webSettings.setDomStorageEnabled(true);
        // 是否可访问Content Provider的资源，默认值 true
        webSettings.setAllowContentAccess(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        
        // 自定义user agent
        webSettings.setUserAgentString("android");
        
        // 设置允许跨域访问
        allowAcrossRequest(webSettings);
        
        setLongClickable(true);
        setScrollbarFadingEnabled(true);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setDrawingCacheEnabled(true);
    }
    
    private void allowAcrossRequest(WebSettings webSettings)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        else
        {
            try
            {
                Class<?> clazz = webSettings.getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null)
                {
                    method.invoke(webSettings, true);
                }
            }
            catch (NoSuchMethodException e)
            {
                Log.e(TAG, "No method error:", e);
            }
            catch (InvocationTargetException e)
            {
                Log.e(TAG, "InvocationTargetException:", e);
            }
            catch (IllegalAccessException e)
            {
                Log.e(TAG, "IllegalAccessException:", e);
            }
        }
    }
}
