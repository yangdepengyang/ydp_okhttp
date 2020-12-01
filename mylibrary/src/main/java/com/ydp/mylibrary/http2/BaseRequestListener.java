package com.ydp.mylibrary.http2;

import android.app.Activity;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ydp.mylibrary.BaseApp;
import com.ydp.mylibrary.view.ToastUtil;


/**
 * 回调工具类
 */
abstract public class BaseRequestListener<T> implements RequestListener<T> {

    private Activity mContext;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean successBo = false;//执行的是默认是不执行的这个针对的是 成功时候是否需要提示框弹出
    private boolean cancelable = false;//针对是否需要点击返回键取消加载框

    /**
     * 执行loading
     * 是否可以手动关闭  默认是不可以的
     *
     * @param cancelable
     */
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    /**
     * 什么都不执行
     */
    public BaseRequestListener() {

    }

    /***
     * 正常的 loading
     * @param activity
     */
    public BaseRequestListener(Activity activity) {
        mContext = activity;
    }

    public BaseRequestListener(Activity activity, boolean successBo) {
        mContext = activity;
        this.successBo = successBo;
    }

    /**
     * 配合原生的加载
     *
     * @param refreshLayout
     */
    public BaseRequestListener(SwipeRefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
    }

    /**
     * 配合原生的加载
     * 增加参数是否需要
     *
     * @param refreshLayout
     */
    public BaseRequestListener(SwipeRefreshLayout refreshLayout, boolean successBo) {
        mRefreshLayout = refreshLayout;
        this.successBo = successBo;
    }

    @Override
    public void onStart() {
        //启动loading 显示
        if (mContext != null && (mContext.isFinishing())) {
            return;
        }
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(true);
        } else if (mContext != null) {
            ToastUtil.showLoading(mContext, cancelable);
        }
        onSt();
    }

    @Override
    public void onError(Throwable e) {
        setShowClose();
        ToastUtil.error(BaseApp.getContext(), e.getMessage());
        onE(e);

    }

    @Override
    public void onSuccess(RequestResult<T> result) {
        setShowClose();
        if (successBo) {
            ToastUtil.success(BaseApp.getContext(), result.message);
        }
        onS(result);

    }

    @Override
    public void onSuccessError(RequestResult<T> result) {
        setShowClose();
        ToastUtil.error(BaseApp.getContext(), result.message);
        onSE(result);

    }

    @Override
    public void onComplete() {
        setShowClose();
        onC();

    }

    protected void onSt() {

    }

    protected void onC() {

    }

    protected void onS(RequestResult<T> result) {

    }

    protected void onSE(RequestResult<T> result) {

    }

    protected void onE(Throwable e) {

    }

    /**
     * 执行关闭loading操作
     */
    public void setShowClose() {
        if (mContext != null && mContext.isFinishing()) {
            return;
        }
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        } else if (mContext != null) {
            ToastUtil.hideLoading();
        }
    }
}
