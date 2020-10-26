package com.ydp.mylibrary.view;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.ydp.mylibrary.R;

public class UiUtils {

    private static Dialog mProgressDialog;


    /**
     * 隐藏键盘
     *
     * @param view
     */
    public static void hideKeyboard(final View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("InflateParams")
    public static void showLoading(Context context) {
        showLoading(context, false);
    }

    public static void showLoading(Context context, boolean cancelable) {
        if (context == null) {
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

    public static void hideLoading() {
        if (null != mProgressDialog) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }


}
