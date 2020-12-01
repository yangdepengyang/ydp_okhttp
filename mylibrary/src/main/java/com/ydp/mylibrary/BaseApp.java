package com.ydp.mylibrary;

import android.app.Application;
import android.content.Context;

public class BaseApp extends Application {

    private  static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext =this;

    }

    /**
     * 获取系统常量
     * @return
     */
    public  static Context getContext(){
        return mContext;
    }
}
