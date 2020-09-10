package com.ydp.mylibrary.http;

/**
 * 存放产量配置
 */
public    class FormatContent {

    public static  FormatContent  formatContent = null;

    public  static  FormatContent  Instantiation(){
        if (formatContent == null){
            formatContent =new FormatContent();
        }
        return formatContent;
    }

    /**
     * 请求类型
     */
    public   static enum  EquestFormat{
        GET,
        POST,
    }
    /**
     * 是否启动loading
     */
    public   static enum  LoadingFormat{
        OPEN,
        CLOSE,
    }
}
