package com.yang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yang.rx_re.ICCVService;
import com.ydp.mylibrary.http.MyCallBack;
import com.ydp.mylibrary.http.OKHttpUtils;
import com.ydp.mylibrary.http2.APIManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements MyCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, Object> map = new HashMap<>();
        map.put("key","值");
        OKHttpUtils.newInstance(this).postAsyncDataOrLoading(this,"https://www.baidu.com/", map, new MyCallBack() {
            @Override
            public void onFailure(IOException e, Call call, String url) {

            }
            @Override
            public void onResponse(String result, Call call, String url) {

            }
        });


        OKHttpUtils.newInstance(this).postAsyncDataOrLoading(this,"https://www.baidu.com/", map, new MyCallBack() {
            @Override
            public void onFailure(IOException e, Call call, String url) {

            }
            @Override
            public void onResponse(String result, Call call, String url) {

            }
        });
    }


    @Override
    public void onFailure(IOException e, Call call, String url) {

    }

    @Override
    public void onResponse(String result, Call call, String url) {

    }
}
