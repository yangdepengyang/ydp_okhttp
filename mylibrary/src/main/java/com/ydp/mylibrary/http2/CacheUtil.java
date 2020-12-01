package com.ydp.mylibrary.http2;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Request;

public class CacheUtil {

    private static final String TAG = "CacheUtil";

    private Map<String, CacheEntity> caches = new HashMap<>();

    private Field upstreamField;
    private Field originalCallField;
    private Method request;

    private static CacheUtil INSTANCE;

    public static CacheUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (CacheUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CacheUtil();
                }
            }
        }
        return INSTANCE;
    }

    private Request getRequest(Observable observable) {
        try {
            if (upstreamField == null) {
                synchronized (this) {
                    upstreamField = observable.getClass().getDeclaredField("upstream");
                    upstreamField.setAccessible(true);
                    Object upstream = upstreamField.get(observable);
                    originalCallField = upstream.getClass().getDeclaredField("originalCall");
                    originalCallField.setAccessible(true);
                    Object originalCall = originalCallField.get(upstream);
                    request = originalCall.getClass().getDeclaredMethod("request");
                    request.setAccessible(true);
                    return (okhttp3.Request)request.invoke(originalCall);
                }
            }
            Object upstream = upstreamField.get(observable);
            Object originalCall = originalCallField.get(upstream);
            return (okhttp3.Request)request.invoke(originalCall);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean cache(Observable observable, RequestListener listener) {
        Request request = getRequest(observable);
        //Log.d(TAG, "url -> " + (request != null ? request.url() : null));
        if (request == null || request.method() == null || !request.method().toLowerCase().equals("get")) {
            Log.d(TAG, "cache fail");
            return false;
        }
        String key = request.url().toString();
        if (caches.containsKey(key)) {
            CacheEntity ent = caches.get(key);
            if (System.currentTimeMillis() - ent.cacheTime > 10000) {
                ent = CacheEntity.newEntity();
                caches.put(key, ent);
                ent.addListener(listener);
                Log.d(TAG, "cache fail, timeout");
                return false;
            } else {
                ent.addListener(listener);
                Log.d(TAG, "cache success");
                return true;
            }
        } else {
            CacheEntity entity = CacheEntity.newEntity();
            entity.addListener(listener);
            caches.put(key, entity);
        }
        Log.d(TAG, "cache fail, new req");
        return false;
    }

    private List<RequestListener> getListeners(Observable observable, boolean pop) {
        Request request = getRequest(observable);
        if (request == null || request.method() == null || !request.method().toLowerCase().equals("get")) {
            return null;
        }
        String key = request.url().toString();
        if (caches.containsKey(key)) {
            CacheEntity ent = caches.get(key);
            if (pop) {
                caches.remove(key);
            }
            return ent.listeners;
        } else {
            return null;
        }
    }

    public <T> void onSuccess(Observable observable, RequestListener listener,  RequestResult<T> data) {
        List<RequestListener> listeners = getListeners(observable, false);
        if (listeners != null) {
            for (RequestListener l: listeners) {
                l.onSuccess(data);
            }
        } else {
            listener.onSuccess(data);
        }
    }
    public <T> void onSuccessError (Observable observable, RequestListener listener, RequestResult<T> data) {
        List<RequestListener> listeners = getListeners(observable, false);
        if (listeners != null) {
            for (RequestListener l: listeners) {
                l.onSuccessError(data);
            }
        } else {
            listener.onSuccessError(data);
        }
    }

    public void onError(Observable observable, RequestListener listener, Throwable throwable) {
        List<RequestListener> listeners = getListeners(observable, false);
        if (listeners != null) {
            for (RequestListener l: listeners) {
                l.onError(throwable);
            }
        } else {
            listener.onError(throwable);
        }
    }

    public void onComplete(Observable observable, RequestListener listener) {
        List<RequestListener> listeners = getListeners(observable, true);
        if (listeners != null) {
            for (RequestListener l: listeners) {
                l.onComplete();
            }
        } else {
            listener.onComplete();
        }
    }

    static class CacheEntity {
        long cacheTime;
        List<RequestListener> listeners;
        static CacheEntity newEntity() {
            CacheEntity ent = new CacheEntity();
            ent.cacheTime = System.currentTimeMillis();
            ent.listeners = new ArrayList<>();
            return ent;
        }

        void addListener(RequestListener listener) {
            listeners.add(listener);
        }
    }
}
