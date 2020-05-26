# ydp_okhttp
okhttp请求框架 简封装自用包含 

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  添加依赖项
  	dependencies {
	        implementation 'com.github.yangdepengyang:ydp_okhttp:版本号'//0.0.1
	}
  使用技巧一
    Map<String, Object> map = new HashMap<>();
        map.put("key","值");
        OKHttpUtils.newInstance(this).postAsyncData("请求地址", map, new MyCallBack() {
            @Override
            public void onFailure(IOException e, Call call, String url) {

            }

            @Override
            public void onResponse(String result, Call call, String url) {

            }
        });
        
        技巧二
        继承回调 implements MyCallBack
        
         Map<String, Object> map = new HashMap<>();
        map.put("key","值");
        OKHttpUtils.newInstance(this).postAsyncData("请求地址", map, this)
        
