package com.ydp.mylibrary.http2;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;

/**
 * API请求结果
 * Created by JayChan on 2016/12/13.
 */
public class RequestResult<T> implements Serializable {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public T data;


}
