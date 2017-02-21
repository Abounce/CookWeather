package com.example.administrator.cookweather.bean;

/**
 * @author yhy created at 2017/2/21 17:17
 */

public class AQI {
    public AQICity city;

    private class AQICity {
        public String aqi;
        public String pm25;
    }
}
