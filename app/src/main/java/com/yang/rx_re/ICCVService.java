package com.yang.rx_re;


import com.ydp.mylibrary.http2.RequestResult;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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




}
