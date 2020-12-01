package com.ydp.mylibrary.http2;

import com.google.gson.Gson;
import com.ydp.mylibrary.BaseApp;
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
     * 不惜带加载框
     *
     * @param observable
     * @param listener
     * @param <T>
     */
    public static <T> void startRequest(final Observable<RequestResult<T>> observable, final RequestListener<T> listener) {

        if (observable == null) {
            return;
        }
        listener.onStart();
        if (CacheUtil.getInstance().cache(observable, listener)) {
            return;
        }
        setStartRequest(observable, listener);
    }

    /**
     * 携带加载弹框
     *
     * @param observable
     * @param listener
     * @param <T>
     */
    public static <T> void startRequestOrLoading(final Observable<RequestResult<T>> observable, final RequestListener<T> listener) {

        if (observable == null) {
            return;
        }
        listener.onStart();
        if (CacheUtil.getInstance().cache(observable, listener)) {
            return;
        }
        setStartRequest(observable, listener);
    }

    /**
     * 统一处理请求
     *
     * @param observable
     * @param listener
     * @param <T>
     */
    public static <T> void setStartRequest(final Observable<RequestResult<T>> observable, final RequestListener<T> listener) {
        startRequest(observable, new Observer<RequestResult<T>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RequestResult<T> result) {
                //执行到如果设置标签是 -1 表示不用区分返回的内容回传同是成功
                if (Key.SUCCESS == -1) {
                    CacheUtil.getInstance().onSuccess(observable, listener, result);
                    return;
                }
                if (result.code != Key.SUCCESS) {//不是200正确的话提用户展示提示语
                    ToastUtil.error(BaseApp.getContext(), result.message);
                    CacheUtil.getInstance().onSuccessError(observable, listener, result);
                } else {
                    CacheUtil.getInstance().onSuccess(observable, listener, result);
                }

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
     * build转换为
     *
     * @param object
     * @return
     */
    public static RequestBody buildJsonBody(Serializable object) {
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new Gson().toJson(object));
    }


}
