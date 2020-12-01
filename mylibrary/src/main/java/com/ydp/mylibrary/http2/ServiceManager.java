package com.ydp.mylibrary.http2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.Logger;
import com.ydp.mylibrary.BaseApp;


import java.io.IOException;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceManager {

    @SuppressLint("StaticFieldLeak")
    private static ServiceManager mInstance;
    public static Retrofit mRetrofit;
    private static HashMap<Class, Object> services = new HashMap<>();
    private static Context mContext;
    private static String BaseUrl = "";//默认空
    private OkHttpClient okHttpClient;
    //动态参数
    private static int timeout = 10;
    private static boolean loggingInterceptorBo = true;
    private static HashMap<String, Object> addHeader = new HashMap<>();

    /**
     * 添加头部关联
     * 请在请求前添加这些参数
     * 有更改可以直接替换此参数重新设置
     *
     * @param addHeader
     */
    public static void setAddHeader(HashMap<String, Object> addHeader) {
        ServiceManager.addHeader = addHeader;
    }

    /**
     * 是否开启输出请求回传的打印
     * 默认开启
     *
     * @param loggingInterceptor
     */
    public static void setLoggingInterceptor(boolean loggingInterceptor) {
        loggingInterceptorBo = loggingInterceptor;
    }

    /**
     * 设置超时时长 默认10秒
     *
     * @param timeout
     */
    public static void setTimeout(int timeout) {
        ServiceManager.timeout = timeout;
    }

    public static ServiceManager getInstance() {
        if (mInstance == null) {
            synchronized (ServiceManager.class) {
                if (mInstance == null) {
                    mInstance = new ServiceManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化调用 不管存不存在都会执行一遍 重构 建议放在执行一次即可
     *
     * @param url
     * @param context
     * @return
     */
    public static ServiceManager getInstance(String url, Context context) {
        mContext = context;
        BaseUrl = url;
        mInstance = new ServiceManager();
        return mInstance;
    }

    private ServiceManager() {
        mContext = BaseApp.getContext();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)//TODO 配置基础的url
                .addConverterFactory(GsonConverterFactory.create(GsonFactory.make()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getHttpClient())
                .build();
    }

    /**
     * 保留 请求接口对象 防止生成多余的对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createService(Class<T> clazz) {
        if (services.containsKey(clazz)) {
            return (T) services.get(clazz);
        }
        T t = mRetrofit.create(clazz);
        services.put(clazz, t);
        return t;
    }

    private OkHttpClient getHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(getLoggingInterceptor());
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                // 增加 API 版本号
                Request.Builder builder = originalRequest.newBuilder();
                //执行遍历添加头部参数
                for (Map.Entry<String, Object> stringObjectEntry : addHeader.entrySet()) {
                    Map.Entry entry = (Map.Entry) stringObjectEntry;
                    builder.addHeader((String) entry.getKey(), String.valueOf(entry.getValue()));
                }
//                builder.addHeader("x-app-id", SessionUtil.getInstance().getXAppId());
//                builder.addHeader("x-app-secret", SessionUtil.getInstance().getXAppSecret());
                // 重新构建请求
                return chain.proceed(builder.build());

            }
        });
        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            }
        });
        builder.connectTimeout(timeout, TimeUnit.SECONDS).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }).readTimeout(timeout, TimeUnit.SECONDS)
                .proxy(BuildConfig.DEBUG ? null : Proxy.NO_PROXY);
        //忽略ssl证书,android10及以上的版本就不用了
        if (Build.VERSION.SDK_INT < 29) {
            builder.sslSocketFactory(initSSLContextAndVerifier().getSocketFactory());
        }
        okHttpClient = builder.build();

        return okHttpClient;
    }

    /**
     * 数字证书校验，方便用charles抓包
     *
     * @return
     */
    private SSLContext initSSLContextAndVerifier() {
        SSLContext context = null;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
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

    /**
     * 拦截打印
     *
     * @return
     */
    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (message == null && !loggingInterceptorBo) {
                    return;
                }
                if (message.contains("\"code\":") || message.contains("<-- ") && !message.contains("<-- END HTTP")) {
                    Logger.e("OkHttp: " + message);
                }
            }

        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }


}
