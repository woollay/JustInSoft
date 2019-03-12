package com.justinsoft.constant;

/**
 * 常量编码
 */
public interface Constant
{
    /**
     * 位置权限
     */
    int LOCATION_PERMISSION_CODE = 12;
    
    /**
     * 拍照权限
     */
    int CAMERA_PERMISSION_CODE = 11;
    
    /**
     * 录音权限
     */
    int RECORD_PERMISSION_CODE = 10;
    
    /**
     * 存储权限
     */
    int STORAGE_PERMISSION_CODE = 9;
    
    /**
     * 拍照编码
     */
    int TAKE_PICTURE_CODE = 14;
    
    /**
     * 压缩照片编码
     */
    int CROP_PICTURE_CODE = 15;
    
    /**
     * 拍照的异步消息编码
     */
    int TAKE_PICTURE_MSG_CODE = 140;
    
    /**
     * 拍照的返回码
     */
    String TAKE_PICTURE_RESULT_CODE = "tp_result_code";
}
