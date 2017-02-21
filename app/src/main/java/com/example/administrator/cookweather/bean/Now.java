package com.example.administrator.cookweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author yhy created at 2017/2/21 17:18
 */

public class Now {
    @SerializedName("tmp")
    public  String temperature;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
