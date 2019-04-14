package com.justinsoft.webview;

import java.io.File;

import com.justinsoft.constant.Constant;
import com.justinsoft.util.LogUtil;
import com.justinsoft.util.PictureUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ProgressBar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BtWebChromeClient extends WebChromeClient
{
    // 日志标记
    private static final String TAG = LogUtil.getClassTag(BtWebChromeClient.class);
    
    private ProgressBar progressBar;
    
    private Activity activity;
    
    private Uri cameraUri;
    
    private Uri cropUri;
    
    private File cameraFile;
    
    private ValueCallback<Uri> oldValueCallback;
    
    private ValueCallback<Uri[]> valueCallback;
    
    String path = Environment.getExternalStorageDirectory() + "";
    
    /**
     * 构造方法
     *
     * @param context
     */
    public BtWebChromeClient(Context context)
    {
        initProgressBar(context);
    }
    
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
    {
        Log.i(TAG, "start to set location permission.");
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }
    
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                result.confirm();
            }
        });
        
        DialogInterface.OnKeyListener listener = new DialogInterface.OnKeyListener()
        {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                return true;
            }
        };
        // 屏蔽keycode等于84之类的按键
        builder.setOnKeyListener(listener);
        
        builder.setCancelable(false);
        builder.create();
        builder.show();
        return true;
    }
    
    public void openFileChooser(ValueCallback<Uri> filePathCallback)
    {
        Log.d(TAG, "call openFileChooser01");
        oldValueCallback = filePathCallback;
        takePicture();
    }
    
    // For Android 3.0+
    public void openFileChooser(ValueCallback filePathCallback, String acceptType)
    {
        Log.d(TAG, "call openFileChooser02");
        oldValueCallback = filePathCallback;
        takePicture();
    }
    
    // For Android 4.1
    public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture)
    {
        Log.d(TAG, "call openFileChooser03");
        oldValueCallback = filePathCallback;
        takePicture();
    }
    
    // For Android 5.0+
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
        FileChooserParams fileChooserParams)
    {
        // super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        Log.d(TAG, "call openFileChooser04");
        valueCallback = filePathCallback;
        
        takePicture();
        return true;
    }

    @Override
    public void onProgressChanged(android.webkit.WebView view, int newProgress)
    {
        if (newProgress == 100)
        {
            progressBar.setVisibility(GONE);
        }
        else
        {
            if (progressBar.getVisibility() == GONE)
            {
                progressBar.setVisibility(VISIBLE);
            }
            progressBar.setProgress(newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }

    /**
     * 添加进度条
     *
     * @param webView
     */
    public void addProgressBar(WebView webView)
    {
        webView.addView(this.progressBar);
    }
    
    /**
     * 拍照异步回调
     *
     * @param resultCode
     * @param uri
     */
    public void cropCallback(int resultCode, Uri uri)
    {
        
        if (resultCode == Activity.RESULT_CANCELED)
        {
            takePictureCancel();
        }
        else
        {
            if (null != oldValueCallback)
            {
                oldValueCallback.onReceiveValue(cropUri);
            }
            else if (null != valueCallback)
            {
                valueCallback.onReceiveValue(new Uri[] {cropUri});
            }
            
            oldValueCallback = null;
            valueCallback = null;
        }
        
        if (cameraFile != null)
        {
            Log.i(TAG, "delete cached camera file:" + cameraFile.getPath());
            cameraFile.delete();
        }
    }
    
    /**
     * 开始裁剪照片
     *
     * @param resultCode
     */
    public void cropPicture(int resultCode)
    {
        if (resultCode == Activity.RESULT_CANCELED)
        {
            takePictureCancel();
        }
        else
        {
            cropPicture(activity, cameraUri);
        }
    }
    
    /**
     * 绑定Activity
     *
     * @param activity
     */
    public void bind(Activity activity)
    {
        this.activity = activity;
    }
    
    /**
     * 拍照
     */
    private void takePicture()
    {
        try
        {
            cameraFile = new File(path, SystemClock.currentThreadTimeMillis() + ".jpg");
            Log.i(TAG, "camera file path=" + cameraFile.toString());
            cameraUri = Uri.fromFile(cameraFile);
            Log.i(TAG, "camera uri=" + cameraUri.toString());
            PictureUtil.takePicture(this.activity, cameraUri, Constant.TAKE_PICTURE_CODE);
        }
        catch (Throwable t)
        {
            Log.e(TAG, "ERROR:", t);
        }
    }
    
    /**
     * 开始裁剪照片
     *
     * @param activity
     * @param cameraUri
     */
    private void cropPicture(Activity activity, Uri cameraUri)
    {
        File cropFile = new File(path, SystemClock.currentThreadTimeMillis() + ".jpg");
        Log.i(TAG, "crop file path=" + cropFile.toString());
        
        cropUri = Uri.fromFile(cropFile);
        Log.i(TAG, "crop uri=" + cropUri.toString());
        PictureUtil.Crop crop = new PictureUtil.Crop(1, 1, 250, 250);
        PictureUtil.cropPicture(activity, cameraUri, cropUri, crop, Constant.CROP_PICTURE_CODE);
    }
    
    /**
     * 取消拍照
     */
    private void takePictureCancel()
    {
        if (null != oldValueCallback)
        {
            oldValueCallback.onReceiveValue(null);
        }
        if (null != valueCallback)
        {
            valueCallback.onReceiveValue(null);
        }
        oldValueCallback = null;
        valueCallback = null;
    }
    
    private void initProgressBar(Context context)
    {
        this.progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        this.progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(context, 3), 0, 0));
        ClipDrawable drawable = new ClipDrawable(new ColorDrawable(Color.BLUE), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        this.progressBar.setProgressDrawable(drawable);
    }
    
    /**
     * 方法描述：根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dp2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
}
