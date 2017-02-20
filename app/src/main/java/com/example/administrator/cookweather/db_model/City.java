package com.example.administrator.cookweather.db_model;

import org.litepal.crud.DataSupport;

/**
 * @author yhy created at 2017/2/20 16:26
 * 每个省对应市的表
 */

public class City extends DataSupport {
    private  int id;
    private  String CityName;
    private  int CityCode;
    private  int ProvinceId; //对应省的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public int getCityCode() {
        return CityCode;
    }

    public void setCityCode(int cityCode) {
        CityCode = cityCode;
    }

    public int getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(int provinceId) {
        ProvinceId = provinceId;
    }
}
