package com.ydp.mylibrary.http2;

public class Key {
    public static int SUCCESS = 200;//成功的指令

    /**
     * 执行了这个后 这个是判断数据接收成功后code
     * 返回结构视奏不走区分成功和失败
     * -1表示走成功不区分对错用户自行判断
     * 默认值 200
     * @param success
     */
    public static void setSUCCESS(int success) {
        SUCCESS = success;
    }

}
