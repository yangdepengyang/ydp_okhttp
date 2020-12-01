package com.yang.rx_re;


import com.ydp.mylibrary.http2.RequestResult;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * @author huanghusheng
 * @date 2020/7/20.
 * @Description: ccv相关接口Api接口
 */
public interface ICCVService {

    /**
     */
    @FormUrlEncoded
    @POST("account/transCcvToPoint")
    Observable<RequestResult<Object>> transCcvToPoint();


    /**
     * 图片相关
     * @param body
     * @return
     */
    @Multipart
    @POST("upload")
    Observable<RequestResult<UploadResponse>> uploadImage(@Part MultipartBody.Part body);

    /**
     * 视频
     * @param body
     * @return
     */
    @Multipart
    @POST("upload-video")
    Observable<RequestResult<UploadResponse>> uploadVideo(@Part MultipartBody.Part body);

}
