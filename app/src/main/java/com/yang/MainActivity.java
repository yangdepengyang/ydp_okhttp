package com.yang;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.rx_re.ICCVService;
import com.ydp.mylibrary.http.MyCallBack;
import com.ydp.mylibrary.http.OKHttpUtils;
import com.ydp.mylibrary.http2.APIManager;
import com.ydp.mylibrary.http2.RequestResult;
import com.ydp.mylibrary.http2.ServiceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.observers.ResourceObserver;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //请求一
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
        //请求二
         ICCVService iccvService = ServiceManager.getInstance().createService(ICCVService.class);
         APIManager.startRequest(iccvService.transCcvToPoint(), new ResourceObserver<RequestResult<Object>>() {
            @Override
            public void onNext(RequestResult<Object> objectRequestResult) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

}
