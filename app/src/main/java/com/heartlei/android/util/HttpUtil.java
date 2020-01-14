package com.heartlei.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    public static void sendOkHttpRequest(String address , okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
//发起一条 HTTP 请求只需要调用 sendOkHttpRequest() 方法，传入请求地址，并注册一
//个回调来处理服务器响应就可以了。
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
