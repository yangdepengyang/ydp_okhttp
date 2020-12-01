package com.ydp.mylibrary.http2;

/**
 */
public interface RequestListener<E> {
    void onStart();

    void onSuccess(RequestResult<E> result);

    void onSuccessError(RequestResult<E> result);

    void onError(Throwable e);

    void onComplete();
}
