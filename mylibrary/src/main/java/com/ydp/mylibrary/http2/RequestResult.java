package com.ydp.mylibrary.http2;

import com.google.gson.annotations.SerializedName;

/**
 * API请求结果
 */
public class RequestResult<T> {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public T data;


}
