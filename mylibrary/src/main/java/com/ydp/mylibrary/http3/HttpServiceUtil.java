package com.ydp.mylibrary.http3;

import android.os.Build;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpServiceUtil {

    public static void get(final String url, Callback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * json 请求
     *
     * @param requestBody
     * @param url
     * @param callback
     */
    public static void post(RequestBody requestBody, String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        getHttpClient().newCall(request).enqueue(callback);
    }

    /**
     * 表单请求
     *
     * @param formBody
     * @param url
     * @param callback
     */
    public static void post(FormBody formBody, String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        getHttpClient().newCall(request).enqueue(callback);
    }


    /**
     * 提交分块请求
     *
     * @param part
     * @param url
     * @param callback
     */
    public static void post(MultipartBody part, String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(part)
                .build();
        getHttpClient().newCall(request).enqueue(callback);
    }



    public static OkHttpClient getHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //                   网络缓存拦截器
                .addInterceptor(interceptor)
                .addInterceptor (new LoggingInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS);


        if (Build.VERSION.SDK_INT < 29) {
            builder.sslSocketFactory(initSSLContextAndVerifier()
                    .getSocketFactory());
        }
        OkHttpClient okHttpClient = builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }).readTimeout(20, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    /**
     * 网络拦截器
     * 进行网络操作的时候进行拦截
     */
    final static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .addHeader("x-app-id", "1")
                    .addHeader("x-app-secret", "1")
                    .addHeader("Authorization", "2")

                    .build();
            Response response = chain.proceed(request);
            return response;
        }
    };


    /**
     * 数字证书校验，方便用charles抓包
     *
     * @return
     */
    private static SSLContext initSSLContextAndVerifier() {
        SSLContext context = null;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            context = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            context.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {

        }

        return context;
    }
}
