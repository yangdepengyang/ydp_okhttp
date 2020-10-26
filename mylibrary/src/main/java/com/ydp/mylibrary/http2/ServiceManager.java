package com.ydp.mylibrary.http2;

import android.annotation.SuppressLint;

import java.util.HashMap;

import retrofit2.Retrofit;

public class ServiceManager {

    @SuppressLint("StaticFieldLeak")
    private static ServiceManager mInstance;
    public static Retrofit mRetrofit;
    private static HashMap<Class, Object> services = new HashMap<>();

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


    public <T> T createService(Class<T> clazz) {
        if (services.containsKey(clazz)) {
            return (T) services.get(clazz);
        }
        T t = mRetrofit.create(clazz);
        services.put(clazz, t);
        return t;
    }





}
