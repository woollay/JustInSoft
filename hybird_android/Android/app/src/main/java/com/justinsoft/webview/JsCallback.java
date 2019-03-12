package com.justinsoft.webview;

import java.util.ArrayList;
import java.util.List;

import com.justinsoft.util.LogUtil;
import com.justinsoft.voice.VoiceMgr;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * 提供给JS的回调
 */
public class JsCallback
{
    private static final String TAG = LogUtil.getClassTag(JsCallback.class);
    
    private WebView webView;
    
    private Activity activity;
    
    public JsCallback(Activity activity, WebView webView)
    {
        this.activity = activity;
        this.webView = webView;
    }
    
    /**
     * 开始测试音量
     */
    @JavascriptInterface
    public void startVoice()
    {
        Log.i(TAG, "start voice.");
        VoiceMgr voiceMgr = VoiceMgr.getInstance();
        voiceMgr.startVoice();
        callbackJs("startVoice", null);
    }
    
    /**
     * 停止测试音量
     */
    @JavascriptInterface
    public void stopVoice()
    {
        Log.i(TAG, "stop voice.");
        VoiceMgr voiceMgr = VoiceMgr.getInstance();
        int maxDB = voiceMgr.stopVoice();
        
        List<String> values = new ArrayList<String>();
        values.add(maxDB + "");
        callbackJs("stopVoice", values);
    }
    
    /**
     * 拍照
     */
    @JavascriptInterface
    public void takePicture()
    {
        //TODO
    }
    
    /**
     * 回调JS
     * 
     * @param name JS方法名
     * @param values JS参数
     */
    private void callbackJs(String name, List<String> values)
    {
        final StringBuilder callbackJs = new StringBuilder();
        callbackJs.append("javascript:");
        callbackJs.append(name);
        callbackJs.append("(");
        if (values != null && values.size() > 0)
        {
            int size = values.size();
            for (int i = 0; i < size; i++)
            {
                callbackJs.append("'");
                callbackJs.append(values.get(i));
                callbackJs.append("'");
                if (i != size - 1)
                {
                    callbackJs.append(",");
                }
            }
        }
        callbackJs.append(")");
        
        Log.i(TAG, "callback js:" + callbackJs);
        
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                final int version = Build.VERSION.SDK_INT;
                // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
                if (version < 18)
                {
                    webView.loadUrl(callbackJs.toString());
                }
                else
                {
                    webView.evaluateJavascript(callbackJs.toString(), new ValueCallback<String>()
                    {
                        @Override
                        public void onReceiveValue(String value)
                        {
                            Log.i(TAG, "js result:" + value);
                        }
                    });
                }
            }
        });
        
    }
}
