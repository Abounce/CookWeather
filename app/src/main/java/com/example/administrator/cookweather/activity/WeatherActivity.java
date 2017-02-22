package com.example.administrator.cookweather.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.cookweather.R;
import com.example.administrator.cookweather.bean.Forecast;
import com.example.administrator.cookweather.bean.Weather;
import com.example.administrator.cookweather.utils.Constants;
import com.example.administrator.cookweather.utils.HttpUtil;
import com.example.administrator.cookweather.utils.ParseJson;
import com.example.administrator.cookweather.utils.SPUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class WeatherActivity extends AppCompatActivity {

    @BindView(R.id.nav_button)
    Button navButton;
    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.sport_text)
    TextView sportText;
    @BindView(R.id.weather_layout)
    ScrollView weatherLayout;
    @BindView(R.id.weather_iv)
    ImageView weatherIv;
    @BindView(R.id.swipe_refresh)
 public    SwipeRefreshLayout swipeRefresh;

  //  @BindView(R.id.drawer_layout)
   public DrawerLayout drawerLayout;
    private String mWeatherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        String weatherString = (String) SPUtils.get(this, "weather", "");
        if (!TextUtils.isEmpty(weatherString)) {
            //读取缓存数据
            Weather weather = ParseJson.HandleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //从网络上获取输一局
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        String bing_pic = (String) SPUtils.get(this, "bing_pic", "");
        if (!TextUtils.isEmpty(bing_pic)) {
            Glide.with(this).load(bing_pic).into(weatherIv);
        } else {
            loadBingPic();
        }
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void loadBingPic() {
        HttpUtil.GetHttp(Constants.IMG_URL, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                SPUtils.put(WeatherActivity.this, "bing_pic", response);
                Glide.with(WeatherActivity.this).load(response).into(weatherIv);
            }
        });

    }

    public void requestWeather(String weather_id) {
        String url = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=854043d5ba7f4e16a16f353f70bd2f78";
        HttpUtil.GetHttp(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(WeatherActivity.this, "请求网络失败", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                Weather weather = ParseJson.HandleWeatherResponse(response);
                if (weather != null && "ok".equals(weather.status)) {
                    SPUtils.put(WeatherActivity.this, "weather_id", response);
                    mWeatherId = weather.basic.weatherId;
                    showWeatherInfo(weather);
                } else {
                    Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                }
                swipeRefresh.setRefreshing(false);
            }
        });
        loadBingPic();
    }


    //展示数据
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updatetime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updatetime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }


}
