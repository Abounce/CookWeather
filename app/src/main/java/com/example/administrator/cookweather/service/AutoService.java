package com.example.administrator.cookweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.administrator.cookweather.bean.Weather;
import com.example.administrator.cookweather.utils.Constants;
import com.example.administrator.cookweather.utils.HttpUtil;
import com.example.administrator.cookweather.utils.ParseJson;
import com.example.administrator.cookweather.utils.SPUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class AutoService extends Service {
    public AutoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(AutoService.this,"后台更新启动",Toast.LENGTH_SHORT).show();
        updateWeather();

        updateBingPic();


        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);

    }

    private void updateWeather() {
        String weatherString = (String) SPUtils.get(this, "weather", "");
        if (!TextUtils.isEmpty(weatherString)) {
            //读取缓存数据
            Weather weather = ParseJson.HandleWeatherResponse(weatherString);
            String mWeatherId = weather.basic.weatherId;
            String url = "http://guolin.tech/api/weather?cityid=" + mWeatherId + "&key=854043d5ba7f4e16a16f353f70bd2f78";
            HttpUtil.GetHttp(url, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    Weather mweather = ParseJson.HandleWeatherResponse(response);

                    if (mweather != null && "ok".equals(mweather.status)) {
                        SPUtils.put(AutoService.this, "weather_id", response);
                        //mWeatherId = mweather.basic.weatherId;
                        //showWeatherInfo(mweather);
                    }
                }

            });
        }
    }

    private void updateBingPic() {
    HttpUtil.GetHttp(Constants.IMG_URL, new StringCallback() {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
            SPUtils.put(AutoService.this,"bing_pic","");

        }
    });
    }
}
