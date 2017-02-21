package com.example.administrator.cookweather.utils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

/**
 * @author yhy created at 2017/2/21 9:11
 */

public class HttpUtil {

    private HttpUtil(){

    }

      public static void GetHttp(String url,Callback stringCallback){
          OkHttpUtils
                  .get()
                  .url(url)
                  .build()
                  .execute(stringCallback);
      }
}
