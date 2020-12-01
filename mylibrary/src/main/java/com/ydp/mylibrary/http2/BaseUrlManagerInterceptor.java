package com.ydp.mylibrary.http2;

import androidx.annotation.NonNull;
import android.text.TextUtils;



import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class BaseUrlManagerInterceptor implements Interceptor{

    // Retrofit初始化时候的url
    private String mOriginalBaseUrl;
    private String mNewBaseUrl;

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HttpUrl requestUrl = oldRequest.url();
        String oldUrl = requestUrl.toString();
        mNewBaseUrl= "";
        if (TextUtils.isEmpty(mOriginalBaseUrl)
                || TextUtils.isEmpty(mNewBaseUrl)
                || TextUtils.equals(mOriginalBaseUrl, mNewBaseUrl)
                || !oldUrl.startsWith(mOriginalBaseUrl)) {
            return chain.proceed(chain.request());
        }

        String newUrl = mNewBaseUrl + oldUrl.substring(mOriginalBaseUrl.length(), oldUrl.length());
        Request newRequest = oldRequest
                .newBuilder()
                .url(newUrl)
                .build();
        return chain.proceed(newRequest);
    }

    public void setOriginalBaseUrl(String originalBaseUrl) {
        mOriginalBaseUrl = originalBaseUrl;
    }

    public String getOriginalBaseUrl() {
        return mOriginalBaseUrl;
    }

    public String getNewBaseUrl() {
        return mNewBaseUrl;
    }

    public void setNewBaseUrl(String newBaseUrl) {
        mNewBaseUrl = newBaseUrl;
    }
}
