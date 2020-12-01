package com.yang.rx_re;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {

    @SerializedName("url")
    public String url;

    public UploadResponse(String url) {
        this.url = url;
    }
}