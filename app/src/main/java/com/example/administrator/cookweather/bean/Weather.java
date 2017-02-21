package com.example.administrator.cookweather.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yhy created at 2017/2/21 17:32
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public  Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
