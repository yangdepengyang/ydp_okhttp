package com.ydp.mylibrary.http2;

import android.content.Context;



import com.google.gson.Gson;
import com.ydp.mylibrary.view.ToastUtil;


import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * APIManager
 */
public class APIManager {

    public static <T> void startRequest(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     *不惜带加载框
     * @param observable
     * @param listener
     * @param context
     * @param <T>
     */
    public static <T> void startRequest(final Observable<RequestResult<T>> observable, final RequestListener<T> listener, Context context) {

        if (observable == null) {
            return;
        }
        listener.onStart();
        if (CacheUtil.getInstance().cache(observable, listener)) {
            return;
        }
        startRequest(observable, new Observer<RequestResult<T>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(RequestResult<T> result) {
                String message = result.message;
                CacheUtil.getInstance().onSuccess(observable, listener, result.data);
            }

            @Override
            public void onError(Throwable e) {

                if (e instanceof SocketTimeoutException) {
                    CacheUtil.getInstance().onError(observable, listener, new Exception("请求超时"));
                } else if (e instanceof IllegalStateException) {
                    CacheUtil.getInstance().onError(observable, listener, new Exception("服务器数据异常"));
                } else if (e instanceof UnknownHostException) {
                    CacheUtil.getInstance().onError(observable, listener, new Exception("网络状态异常"));
                } else {
                        if (!"Null is not a valid element".equals(e.getMessage()))
                        CacheUtil.getInstance().onError(observable, listener, e);
                }
            }

            @Override
            public void onComplete() {
                CacheUtil.getInstance().onComplete(observable, listener);
            }
        });
    }

    /**
     *携带加载弹框
     * @param observable
     * @param listener
     * @param context
     * @param <T>
     */
    public static <T> void startRequestOrLoading(final Observable<RequestResult<T>> observable, final RequestListener<T> listener, Context context) {

        if (observable == null) {
            return;
        }
        listener.onStart();
        if (CacheUtil.getInstance().cache(observable, listener)) {
            return;
        }
        ToastUtil.showLoading(context);
        startRequest(observable, new Observer<RequestResult<T>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(RequestResult<T> result) {
                ToastUtil.hideLoading();
                String message = result.message;
                CacheUtil.getInstance().onSuccess(observable, listener, result.data);
            }

            @Override
            public void onError(Throwable e) {

                if (e instanceof SocketTimeoutException) {
                    CacheUtil.getInstance().onError(observable, listener, new Exception("请求超时"));
                } else if (e instanceof IllegalStateException) {
                    CacheUtil.getInstance().onError(observable, listener, new Exception("服务器数据异常"));
                } else if (e instanceof UnknownHostException) {
                    CacheUtil.getInstance().onError(observable, listener, new Exception("网络状态异常"));
                } else {
                    if (!"Null is not a valid element".equals(e.getMessage()))
                        CacheUtil.getInstance().onError(observable, listener, e);
                }
            }

            @Override
            public void onComplete() {
                ToastUtil.hideLoading();
                CacheUtil.getInstance().onComplete(observable, listener);
            }
        });
    }


    public static RequestBody buildJsonBody(Serializable object) {
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new Gson().toJson(object));
    }

}
