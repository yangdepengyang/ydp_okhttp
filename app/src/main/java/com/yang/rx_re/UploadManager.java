package com.yang.rx_re;

import android.app.Activity;
import android.net.Uri;


import com.ydp.mylibrary.http2.APIManager;
import com.ydp.mylibrary.http2.RequestListener;
import com.ydp.mylibrary.http2.ServiceManager;
import com.ydp.mylibrary.http2.UploadUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class UploadManager {


    public static void uploadImage(File file, RequestListener<UploadResponse> responseRequestListener, Activity activity) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);
        //执行请求的对象
        ICCVService uploadService = ServiceManager.getInstance().createService(ICCVService.class);
        APIManager.startRequest(uploadService.uploadImage(fileBody), responseRequestListener);
    }

    public static void uploadFile(File file, RequestListener<UploadResponse> responseRequestListener) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);
        ICCVService uploadService = ServiceManager.getInstance().createService(ICCVService.class);
        APIManager.startRequest(uploadService.uploadVideo(fileBody), responseRequestListener);
    }

    /**
     * 带压缩功能的上传图片
     *
     * @param activity
     * @param uri
     * @param responseRequestListener
     */
    public static void uploadImageStart(final Activity activity, Uri uri, final RequestListener<UploadResponse> responseRequestListener) {
        File file = UploadUtil.uri2File(uri, activity);
        Luban.with(activity)
                .load(file)                     //传人要压缩的图片
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        responseRequestListener.onStart();
                        // 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        uploadImage(file, responseRequestListener,activity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 当压缩过程出现问题时调用
                        responseRequestListener.onError(e);
                    }
                }).launch();
    }

    /**
     * 带压缩功能的上传图片
     * 视频压缩暂时没有配置
     * @param activity
     * @param uri
     * @param responseRequestListener
     */
    public static void uploadVideoStart(Activity activity, String uri, final RequestListener<UploadResponse> responseRequestListener) {
        File file = new File(uri);
        uploadFile(file, responseRequestListener);
    }

}
