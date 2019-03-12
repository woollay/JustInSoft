package com.justinsoft.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.justinsoft.R;
import com.justinsoft.constant.Constant;
import com.justinsoft.permission.PermissionMgr;
import com.justinsoft.util.LogUtil;
import com.justinsoft.webview.BtWebView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // 日志句柄
    private static final String TAG = LogUtil.getClassTag(MainActivity.class);

    private BtWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        // 1.打开权限设置
        loadPermission(this);

        // 2.设置webview
        initWebView();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 拍照
            case Constant.TAKE_PICTURE_CODE: {
                Log.i(TAG, "camera result:" + resultCode);
                Log.i(TAG, "camera data:" + (data == null));
                // 取消拍照
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i(TAG, "abort to take a picture.");
                } else {
                    Log.i(TAG, "successfully to take a picture.");
                }
                this.webView.cropPicture(resultCode);
                break;
            }
            case Constant.CROP_PICTURE_CODE: {
                Log.i(TAG, "crop result:" + resultCode);
                Log.i(TAG, "crop data:" + data);
                Log.i(TAG, "crop data.getData:" + data.getData());
                Bitmap photo = data.getParcelableExtra("data");
                Log.i(TAG, "crop picture:" + photo);
                // String base64Pic = PictureUtil.getPictureBase64(photo);
                // Log.i(TAG, "base64Pic:" + base64Pic);

                this.webView.cropCallback(resultCode);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i(TAG, "update permission now.");
        PermissionMgr permissionMgr = PermissionMgr.getInstance();
        permissionMgr.updatePermission(this, requestCode, permissions, grantResults);
        if (permissionMgr.hasRequestAllPermission()) {
            refreshHomepage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            webView.destroy();
        }
    }

    /**
     * 检查并请求打开权限设置
     *
     * @param activity
     */
    private void loadPermission(Activity activity) {
        Map<Integer, Set<String>> allPermission = new HashMap<Integer, Set<String>>();

        Set<String> locationPermission = new HashSet<String>();
        locationPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        locationPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        allPermission.put(Constant.LOCATION_PERMISSION_CODE, locationPermission);

        Set<String> audioPermission = new HashSet<String>();
        audioPermission.add(Manifest.permission.RECORD_AUDIO);
        allPermission.put(Constant.RECORD_PERMISSION_CODE, audioPermission);

        Set<String> cameraPermission = new HashSet<String>();
        cameraPermission.add(Manifest.permission.CAMERA);
        allPermission.put(Constant.CAMERA_PERMISSION_CODE, cameraPermission);

        Set<String> storagePermission = new HashSet<String>();
        storagePermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        storagePermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        allPermission.put(Constant.STORAGE_PERMISSION_CODE, storagePermission);

        PermissionMgr.getInstance().requestPermission(activity, allPermission);
    }

    private void initWebView() {
        // 1、从activity中获取WebView对象
        Log.i(TAG, "start to init webview.");
        webView = findViewById(R.id.main_webview);
        webView.bind(this);

        refreshHomepage();
    }

    /**
     * 刷新主页
     */
    private void refreshHomepage() {
        // 2、加载主页
        Log.i(TAG, "start to load homepage.");
        webView.loadUrl("file:///android_asset/index.html");
        Log.i(TAG, "end to load homepage.");
    }

    private void init() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
}
