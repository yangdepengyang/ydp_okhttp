package com.ydp.mylibrary.http3;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 案例
 */
public class ServiceManager {
    private static final String  BASE_URL ="地址";

    /**
     * 无参是使用
     * @param callback
     */
    public static void getUserInfo(Callback callback) {
        HttpServiceUtil.get(String.format("%sliveapi/live/user/live-check-user", BASE_URL), callback);
    }


    /**
     * get 使用
     *
     */
    public static void getListAll(int pageOffset, int pageSize, Callback callback) {
        String url = String.format("%sliveapi/live/free/live-list-all?page=%s&pageSize=%s&keyWord=%s%s", BASE_URL, pageOffset, pageSize);
        HttpServiceUtil.get(url, callback);
    }

    /**
     * post 上传json使用
     *
     */
    public static void addLive(String title, String liveImage, Callback callback) {

        String url = String.format("%sliveapi/live/stream/live-room-creat", BASE_URL);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("cover", liveImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(json.toString(), JSON);
        HttpServiceUtil.post(requestBody, url, callback);
    }

    /**
     * 表单格式 post
     */
    public static void clickShareOrGoods(String liveId, String type, String productId, Callback callback) {
        String url = String.format("%slive/clickShareOrGoods", BASE_URL);
        //创建表单请求参数
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("liveId", liveId);
        builder.add("type", type);
        builder.add("productId", productId);
        FormBody formBody = builder.build();
        HttpServiceUtil.post(formBody, url, callback);
    }
}
