package com.justinsoft.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

/**
 * 图片处理工具类
 */
public final class PictureUtil
{
    // 日志标记
    private static final String TAG = LogUtil.getClassTag(PictureUtil.class);
    
    private PictureUtil()
    {
    }
    
    /**
     * 拍照
     *
     * @param activity 当前activity
     * @param imageUri 拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Activity activity, Uri imageUri, int requestCode)
    {
        Log.i(TAG, "start to take a picture.");
        // 调用系统相机
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(cameraIntent, requestCode);
        Log.i(TAG, "end to take a picture.");
    }
    
    /**
     * 打开相册
     *
     * @param activity 当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openPicture(Activity activity, int requestCode)
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }
    
    /**
     * 裁剪图片
     * 
     * @param activity 当前activity
     * @param orgUri 剪裁原图的Uri
     * @param desUri 剪裁后的图片的Uri
     * @param crop:{aspectX X方向的比例;aspectY:Y方向的比例;width:剪裁图片的宽度;height:剪裁图片高度}
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropPicture(Activity activity, Uri orgUri, Uri desUri, Crop crop, int requestCode)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", crop.getAspectX());
        intent.putExtra("aspectY", crop.getAspectY());
        intent.putExtra("outputX", crop.getWidth());
        intent.putExtra("outputY", crop.getHeight());
        intent.putExtra("scale", true);
        // 将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        // 取消人脸识别
        intent.putExtra("noFaceDetection", true);
        // true:返回bitmap，false：返回bitmap
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, requestCode);
    }
    
    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String getPictureBase64(Bitmap bitmap)
    {
        String result = null;
        ByteArrayOutputStream out = null;
        try
        {
            if (bitmap != null)
            {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                
                byte[] bitmapBytes = out.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error:", e);
        }
        finally
        {
            IOUtil.close(out);
        }
        return result;
    }
    
    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getPicture(Context context, Uri uri)
    {
        InputStream in1 = null;
        InputStream in2 = null;
        try
        {
            in1 = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            // optional
            onlyBoundsOptions.inDither = true;
            // optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeStream(in1, null, onlyBoundsOptions);
            
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
            {
                return null;
            }
            // 图片分辨率以480x800为标准
            // 这里设置高度为800f
            float hh = 800f;
            // 这里设置宽度为480f
            float ww = 480f;
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            // be=1表示不缩放
            int be = 1;
            if (originalWidth > originalHeight && originalWidth > ww)
            {
                // 如果宽度大的话根据宽度固定大小缩放
                be = (int)(originalWidth / ww);
            }
            else if (originalWidth < originalHeight && originalHeight > hh)
            {
                // 如果高度高的话根据宽度固定大小缩放
                be = (int)(originalHeight / hh);
            }
            if (be <= 0)
            {
                be = 1;
            }
            // 比例压缩
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            // 设置缩放比例
            bitmapOptions.inSampleSize = be;
            // optional
            bitmapOptions.inDither = true;
            // optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            in2 = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in2, null, bitmapOptions);
            // 再进行质量压缩
            return compressPicture(bitmap);
            
        }
        catch (Exception e)
        {
            Log.e(TAG, "Failed to get picture.", e);
        }
        finally
        {
            IOUtil.close(in1);
            IOUtil.close(in2);
        }
        return null;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressPicture(Bitmap image)
    {
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        try
        {
            out = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            int options = 100;
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            while (out.toByteArray().length / 1024 > 100)
            {
                // 重置baos即清空baos
                out.reset();
                // 第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差 ，第三个参数：保存压缩后的数据的流
                // 这里压缩options%，把压缩后的数据存放到baos中
                image.compress(Bitmap.CompressFormat.JPEG, options, out);
                options -= 10;// 每次都减少10
            }
            // 把压缩后的数据baos存放到ByteArrayInputStream中
            in = new ByteArrayInputStream(out.toByteArray());
            // 把ByteArrayInputStream数据生成图片
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, null);
            return bitmap;
        }
        finally
        {
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }
    
    /**
     * 裁剪对象
     */
    public static class Crop
    {
        private int aspectX;
        
        private int aspectY;
        
        private int width;
        
        private int height;
        
        public Crop(int aspectX, int aspectY, int width, int height)
        {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            this.width = width;
            this.height = height;
        }
        
        private int getAspectX()
        {
            return aspectX;
        }
        
        private int getAspectY()
        {
            return aspectY;
        }
        
        private int getWidth()
        {
            return width;
        }
        
        private int getHeight()
        {
            return height;
        }
    }
}