package com.justinsoft.permission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.justinsoft.util.LogUtil;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public final class PermissionMgr
{
    // 日志标记
    private static final String TAG = LogUtil.getClassTag(PermissionMgr.class);
    
    // 权限管理
    private static PermissionMgr permissionMgr;
    
    private static final Lock LOCK = new ReentrantLock();
    
    private Set<Integer> requestedPermissionCode;
    
    private Set<Integer> grantPermissionCode;
    
    private Map<Integer, Set<String>> allPermission;
    
    private PermissionMgr()
    {
        requestedPermissionCode = new HashSet<Integer>();
        grantPermissionCode = new HashSet<Integer>();
    }
    
    public static PermissionMgr getInstance()
    {
        if (null == permissionMgr)
        {
            LOCK.tryLock();
            try
            {
                if (null == permissionMgr)
                {
                    permissionMgr = new PermissionMgr();
                }
                return permissionMgr;
            }
            catch (Exception e)
            {
                Log.e(TAG, "failed to get permission", e);
            }
            finally
            {
                LOCK.unlock();
            }
        }
        return permissionMgr;
    }
    
    /**
     * 请求权限
     *
     * @param activity
     * @param allPermission
     */
    public void requestPermission(Activity activity, Map<Integer, Set<String>> allPermission)
    {
        this.allPermission = allPermission;
        requestLeftPermission(activity, allPermission);
    }
    
    /**
     * 判断权限请求结束后，是否获取到权限
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public void updatePermission(Activity activity, int requestCode, String permissions[], int[] grantResults)
    {
        Log.i(TAG, "request permission code:" + requestCode);
        Log.i(TAG, "all permission code:" + allPermission.keySet());
        Log.i(TAG, "all permission:" + permissions);
        if (!allPermission.keySet().contains(requestCode))
        {
            Log.i(TAG, "invalid permission");
            return;
        }
        requestedPermissionCode.add(requestCode);
        
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            Log.i(TAG, "grant permission now:" + requestCode);
            grantPermissionCode.add(requestCode);
        }
        else
        {
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            Log.i(TAG, "no permission now:" + requestCode);
        }
        
        Map<Integer, Set<String>> ungrantPermission = new HashMap<Integer, Set<String>>();
        ungrantPermission.putAll(allPermission);
        ungrantPermission.keySet().removeAll(requestedPermissionCode);
        Log.i(TAG, "Left permission:" + ungrantPermission.keySet());
        requestLeftPermission(activity, ungrantPermission);
    }
    
    /**
     * 是否已经申请了足够的权限
     *
     * @return
     */
    public boolean hasRequestAllPermission()
    {
        Map<Integer, Set<String>> ungrantPermission = new HashMap<Integer, Set<String>>();
        ungrantPermission.putAll(allPermission);
        ungrantPermission.keySet().removeAll(requestedPermissionCode);
        return ungrantPermission.isEmpty();
    }
    
    /**
     * 请求剩余权限
     * 
     * @param activity
     * @param allPermission
     */
    private void requestLeftPermission(Activity activity, Map<Integer, Set<String>> allPermission)
    {
        for (Map.Entry<Integer, Set<String>> entry : allPermission.entrySet())
        {
            int grantPermission = -1;
            boolean isLocationPermission = false;
            int requestCode = entry.getKey();
            Set<String> batchPermission = entry.getValue();
            for (String permission : batchPermission)
            {
                int resultPermission = ContextCompat.checkSelfPermission(activity, permission);
                if (resultPermission == PackageManager.PERMISSION_GRANTED)
                {
                    this.requestedPermissionCode.add(requestCode);
                    grantPermission = PackageManager.PERMISSION_GRANTED;
                }
                
                if (permission.equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)
                    || permission.equalsIgnoreCase(Manifest.permission.ACCESS_COARSE_LOCATION))
                {
                    isLocationPermission = true;
                }
            }
            
            if (grantPermission != PackageManager.PERMISSION_GRANTED)
            {
                boolean result = shouldPermission(activity, batchPermission);
                if (isLocationPermission)
                {
                    List<String> allProvider = getLocationProvider(activity);
                    if (null == allProvider || allProvider.isEmpty())
                    {
                        result = false;
                    }
                }
                if (!result)
                {
                    Log.i(TAG, "It needs to request permission now.");
                    ActivityCompat.requestPermissions(activity, batchPermission.toArray(new String[] {}), requestCode);
                }
                return;
            }
        }
    }
    
    private boolean shouldPermission(Activity activity, Set<String> batchPermission)
    {
        for (String permission : batchPermission)
        {
            if (!this.requestedPermissionCode.contains(permission))
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                {
                    Log.i(TAG, "grant permission:" + permission);
                    return true;
                }
            }
        }
        return false;
    }
    
    private List<String> getLocationProvider(Activity activity)
    {
        LocationManager locationManager = (LocationManager)activity.getSystemService(Activity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        List<String> allProviderName = locationManager.getProviders(criteria, true);
        Log.i(TAG, "provider:" + allProviderName);
        return allProviderName;
    }
}
