package com.example.administrator.cookweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author yhy created at 2017/2/21 17:01
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public  String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
