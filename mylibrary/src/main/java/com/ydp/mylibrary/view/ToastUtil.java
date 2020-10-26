package com.ydp.mylibrary.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ydp.mylibrary.R;
import com.ydp.mylibrary.util.NetworkUtil;


/**
 * 自定义Toast工具类
 */
public class ToastUtil {

    private static Dialog mProgressDialog;

    public static void error(Context context,String message) {
        show(context,message, R.color.error_text_color_okttp2, R.drawable.toast_error_bg);

    }

    public static void success(Context context,String message) {
        show(context,message, R.color.success_text_color_okhttp2, R.drawable.toast_success_bg);
    }

    private static void show(Context context,String message,  int textColor, int backgroundRes) {

        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        layout.setBackgroundResource(backgroundRes);
        TextView messageTv = (TextView) layout.findViewById(R.id.toastMessageTv);
        messageTv.setTextColor(context.getResources().getColor(textColor));
        messageTv.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 6);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @SuppressLint("InflateParams")
    public static void showLoading(Context context) {
        showLoading(context, false);
    }

    public static void showLoading(Context context, boolean cancelable) {
        if (context == null) {
            return;
        }
        if(!NetworkUtil.isNetworkAvailable(context)){
            ToastUtil.error(context,"网络状态异常");
            return;
        }
        if (null == mProgressDialog && null != context) {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_layout, null);
            mProgressDialog = new Dialog(context, R.style.LoadingDialog_okhttp2);
            mProgressDialog.setContentView(view, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            mProgressDialog.setCancelable(cancelable);
        }
        if (null != mProgressDialog && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }


    public static void showLoading(Context context, boolean cancelable,String text) {
        if (context == null) {
            return;
        }
        if(!NetworkUtil.isNetworkAvailable(context)){
            ToastUtil.error(context,"网络状态异常");
            return;
        }
        if (null == mProgressDialog && null != context) {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_layout, null);
            mProgressDialog = new Dialog(context, R.style.LoadingDialog_okhttp2);
            mProgressDialog.setContentView(view, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            mProgressDialog.setCancelable(cancelable);
            TextView textView = view.findViewById(R.id.tv_tip);
            textView.setText(text);
        }
        if (null != mProgressDialog && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public static void hideLoading() {
        if (null != mProgressDialog) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }



    public static Dialog getProgressDialog() {
        return mProgressDialog;
    }
}
