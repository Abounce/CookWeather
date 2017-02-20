package com.example.administrator.cookweather.db_model;

import org.litepal.crud.DataSupport;

/**
 * @author yhy created at 2017/2/20 16:28
 */

public class County extends DataSupport{
    private int id;
    private String CountyName;
    private int WeatherCode;
    private int CityId;  //对应城市的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        CountyName = countyName;
    }

    public int getWeatherCode() {
        return WeatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        WeatherCode = weatherCode;
    }

    public int getCityId() {
        return CityId;
    }

    public void setCityId(int cityId) {
        CityId = cityId;
    }
}
