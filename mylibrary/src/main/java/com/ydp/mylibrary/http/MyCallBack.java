package com.ydp.mylibrary.http;

import java.io.IOException;

import okhttp3.Call;

//自定义的一个接口，用于请求返回数据
//=======================================================================
// 得想办法，把咱们自己定义的两个方法在主线程中执行。。。
// 这样当我们在外部实现我们自己的接口的时候，实现接口中的方法就会在主线程中执行
//=======================================================================
public interface MyCallBack {
        // 失败的回调方法
         void onFailure(IOException e, Call call, String url);
        //成功回调
        void onResponse(String result, Call call, String url);

}