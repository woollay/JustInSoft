package com.justinsoft.util;

/**
 * 日志工具类
 */
public final class LogUtil
{
    /**
     * 私有化构造方法
     */
    private LogUtil()
    {
        super();
    }

    /**
     * 获取类标识
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Object> String getClassTag(Class<T> clazz)
    {
        return clazz.getSimpleName() + "$$$";
    }
}
