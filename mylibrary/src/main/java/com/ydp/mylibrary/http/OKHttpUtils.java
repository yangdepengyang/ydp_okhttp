package com.ydp.mylibrary.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ydp.mylibrary.util.LogUtils;
import com.ydp.mylibrary.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 请求工具类
 */
public class OKHttpUtils {
    //id地址请求的
    public static String Id = "";
    //htpp请求
    public static OkHttpClient okHttpClient = null;
    public Context context = null;

    /**
     * 判断网络是否可用
     *
     * @param context 当前上下文
     * @return   true 有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //=======================================================================
    // 双层验证的单例设计模式
    //=======================================================================
    //1.构造方法私有化
    private OKHttpUtils(Context context) {
        this.context = context;
        //避免多次创建
        if (okHttpClient == null) {
            //做一些配置的信息
            //do sth...
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            // int cacheSize = 10 * 1024 * 1024; //10Mb
            int cacheSize = 30; //10Mb
            okHttpClient = builder.cache(new Cache(context.getCacheDir(), cacheSize))//设置缓存目录和大小
                    .connectTimeout(5, TimeUnit.SECONDS) //连接时间 8s
                    .readTimeout(20, TimeUnit.SECONDS) //读取时间 20s
                    .writeTimeout(20, TimeUnit.SECONDS) //写入的时间20s
                    .build();
        }
    }

    //2.暴露出一个方法，返回当前类的对象
    private static OKHttpUtils mInstance;

    public static OKHttpUtils newInstance(Context context) {
        if (mInstance == null) {
            //实例化对象
            //加上一个同步锁，只能有一个执行路径进入
            synchronized (OKHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OKHttpUtils(context);
                }
            }
        }

        return mInstance;
    }

    //=======================================================================
    // Post异步请求
    //== =====================================================================

    /**
     * 提交键值对方法  POST
     * @param url 请求路径
     * @param mycallBack 回调对象
     */
    public  void postAsyncData(final String url, Map<String, Object> params, final MyCallBack mycallBack) {
        //做数据统一加密
        LogUtils.newInstance().e(url + "---传参集合----" + params.toString() + "----");



        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() != 0) {
            //添加键值对
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        RequestBody requestBody = builder.build();
        Request.Builder builder1 = new Request.Builder();
//        builder1.addHeader("token", SharePreferenceUtils.getString(Constant.token));  //将请求头以键值对形式添加，可添加多个请求头
//        builder1.addHeader("uid", SharePreferenceUtils.getString(Constant.uid));  //将请求头以键值对形式添加，可添加多个请求头
        //在这拼接上请求地址
        Request request = builder1.url(Id + url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mycallBack.onFailure(e, call, url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //正确返回数据中...
                if (response != null) {
                    String result = response.body().string();
                    if (result.isEmpty()) {
                        //错误信息为空 ，可能走到这里了
                        IOException e = new IOException();
                        mycallBack.onFailure(e, call, url);
                    } else {
                        if (isJson(result)) {
                            mycallBack.onResponse(result, call, url);
                        } else {
                            //返回数据有误不是json字符串
                            //错误信息为空 ，可能走到这里了
                            IOException e = new IOException();
                            mycallBack.onFailure(e, call, url);
                        }
                    }
                } else {
                    //错误信息为空 ，可能走到这里了
                    IOException e = new IOException();
                    mycallBack.onFailure(e, call, url);
                }
            }
        });
    }

    /**
     * 提交键值对方法  GET
     * @param url 请求路径
     * @param mycallBack 回调对象
     */
    public  void GetAsyncData(final String url, final MyCallBack mycallBack) {


        //2,创建一个Request
        final Request request = new Request.Builder()
                .header("Content-Encoding", "gzip")
//                .addHeader("token", SharePreferenceUtils.getString(Constant.token))
//                .addHeader("uid", SharePreferenceUtils.getString(Constant.uid))
//                .url(bo == true ? url : Id + url)
                .build();

        //3,新建一个call对象
        Call call = okHttpClient.newCall(request);
        //4，请求加入调度，这里是异步Get请求回调
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mycallBack.onFailure(e, call, url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    //响应成功
                    String result = response.body().string();
                    mycallBack.onResponse(result, call, url);
                } else {
                    mycallBack.onFailure(null, null, url);
                }
            }
        });

    }

    /**
     * 取消请求 全部请求
     */
    public  void okhttpcliencancel() {
        okHttpClient.dispatcher().cancelAll();
    }


    /**
     * 判断是否是json结构
     * @param value 传入json结构String
     * @return 对就返回 true
     */
    public  boolean isJson(String value) {
        if (value == null) {
            return false;
        }

        try {
            new JSONObject(value);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

}