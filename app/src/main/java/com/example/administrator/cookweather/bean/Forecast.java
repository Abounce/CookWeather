package com.example.administrator.cookweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author yhy created at 2017/2/21 17:27
 */

public class Forecast {

    public  String date;
    @SerializedName("tmp")
    public  Temperature temperature;
    @SerializedName("cond")
    public  More more;

    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
