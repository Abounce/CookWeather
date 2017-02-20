package com.example.administrator.cookweather.utils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * @author yhy created at 2017/2/20 16:55
 */

public class BaseApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
