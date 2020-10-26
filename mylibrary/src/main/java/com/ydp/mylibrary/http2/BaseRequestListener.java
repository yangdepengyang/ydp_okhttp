package com.ydp.mylibrary.http2;

import android.app.Activity;
import android.util.Log;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ydp.mylibrary.util.NetworkUtil;
import com.ydp.mylibrary.view.ToastUtil;


/**
 * @author JayChan <voidea@foxmail.com>
 * @version 1.0
 * @package com.weiju.ccmall.shared.basic
 * @since 2017-06-11
 */
abstract public class BaseRequestListener<T> implements RequestListener<T> {

    private Activity mContext;
    private SwipeRefreshLayout mRefreshLayout;

    public BaseRequestListener() {
    }

    public BaseRequestListener(Activity context) {
        mContext = context;
    }

    public BaseRequestListener(SwipeRefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
    }

    @Override
    public void onStart() {
        if (mContext != null && (mContext.isFinishing())) {
            return;
        }
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(true);
        } else if (mContext != null) {
            ToastUtil.showLoading(mContext);
        } else {
            onSt();
        }
        if(!NetworkUtil.isNetworkAvailable(mContext)){
            if(mRefreshLayout!=null){
                mRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        ToastUtil.hideLoading();
        if (mContext != null && (mContext.isFinishing())) {
            return;
        }

        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        } else if (mContext != null) {
            ToastUtil.hideLoading();
        } else {
            onE(e);
        }
    }

    @Override
    public void onSuccess(T result) {
        if (mContext != null && mContext.isFinishing()) {
            return;
        }
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        } else if (mContext != null) {
            ToastUtil.hideLoading();
        } else {
            onS(result);
        }
    }

    @Override
    public void onComplete() {
        if (mContext != null && mContext.isFinishing()) {
            return;
        }
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        } else if (mContext != null) {
            ToastUtil.hideLoading();
        } else {
            onC();
        }
    }

    protected void onSt() {

    }

    protected void onC() {

    }

    protected void onS(T result) {

    }

    protected void onE(Throwable e) {

    }
}
