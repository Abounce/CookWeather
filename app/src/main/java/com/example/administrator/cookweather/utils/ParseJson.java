package com.example.administrator.cookweather.utils;

import android.text.TextUtils;

import com.example.administrator.cookweather.db_model.City;
import com.example.administrator.cookweather.db_model.County;
import com.example.administrator.cookweather.db_model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yhy created at 2017/2/20 16:34
 * 解析json并保存到数据库中
 */

public class ParseJson {
    /**
     *
     * @param response 返回的数据
     * @return  保存数据库是否成功
     */

    public static boolean handleProvinceResponse(String response){
      if (!TextUtils.isEmpty(response)){
          try {
              JSONArray allprovince = new JSONArray(response);
              for (int i = 0; i < allprovince.length(); i++) {
                  JSONObject objectProvince = allprovince.getJSONObject(i);
                  Province province=new Province();
                  province.setProvinceName(objectProvince.getString("name"));
                  province.setProvinceCode(objectProvince.getInt("id"));
                  province.save();

              }
              return true;

          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
        return false;

    }
    /**
     *
     * @param response 返回的数据
     * @return  保存数据库是否成功
     */
    public static boolean handleCityResponse(String response,int provinceid){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject objectCity = allCity.getJSONObject(i);
                    City city=new City();
                    city.setCityCode(objectCity.getInt("id"));
                    city.setCityName(objectCity.getString("name"));
                    city.setProvinceId(provinceid);
                    city.save();
                }
                  return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;

    }
    /**
     *
     * @param response 返回的数据
     * @return  保存数据库是否成功
     */
    public static boolean handleCountyResponse(String response,int cityid){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allcounty = new JSONArray(response);
                for (int i = 0; i <allcounty.length() ; i++) {
                    JSONObject objectCounty = allcounty.getJSONObject(i);
                    County county=new County();
                    county.setWeatherCode(objectCounty.getInt("weather_id"));
                    county.setCountyName(objectCounty.getString("name"));
                    county.setCityId(cityid);
                    county.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;

    }

}
