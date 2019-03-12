package com.justinsoft.util;

import java.io.Closeable;
import java.io.IOException;

import android.util.Log;

public final class IOUtil
{
    // 日志标记
    private static final String TAG = LogUtil.getClassTag(IOUtil.class);

    private IOUtil()
    {
    }

    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void close(Closeable closeable)
    {
        if (null != closeable)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Failed to get close io.", e);
            }
        }
    }
}
