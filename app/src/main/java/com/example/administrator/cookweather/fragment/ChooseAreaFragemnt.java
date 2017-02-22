package com.example.administrator.cookweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.cookweather.R;
import com.example.administrator.cookweather.activity.MainActivity;
import com.example.administrator.cookweather.activity.WeatherActivity;
import com.example.administrator.cookweather.db_model.City;
import com.example.administrator.cookweather.db_model.County;
import com.example.administrator.cookweather.db_model.Province;
import com.example.administrator.cookweather.utils.Constants;
import com.example.administrator.cookweather.utils.HttpUtil;
import com.example.administrator.cookweather.utils.ParseJson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class ChooseAreaFragemnt extends Fragment {
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.list_view)
    ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist=new ArrayList<>();

    public static final int LEVEL_PROVINCE=0;//省

    public static final int LEVEL_CITY=1;//市

    public static final int LEVEL_COUNTY=2;//县

    private int current_level;//当前级别

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private ProgressDialog progressDialog;

    private Province selectedProvince; //选中的省

    private City selectedCity;//选中的城市




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area_fragemnt, container, false);
        ButterKnife.bind(this, view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (current_level==LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCity();
                }else if (current_level==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounty();
                }else if (current_level==LEVEL_COUNTY){
                    String weatherCode = countyList.get(position).getWeatherCode();
                    if (getActivity() instanceof MainActivity){
                    Intent intent=new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id",weatherCode);
                    startActivity(intent);
                    getActivity().finish();

                    }else if (getActivity() instanceof  WeatherActivity){
                       WeatherActivity activity= (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherCode);
                    }

                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_level==LEVEL_COUNTY){
                    queryCity();
                }else if (current_level==LEVEL_CITY){
                    queryProvince();
                }
            }
        });

        queryProvince();
    }

    private void queryCity() {
        titleText.setText(selectedProvince.getProvinceName());
        back.setVisibility(View.VISIBLE);
        //从数据库中查询
        cityList= DataSupport.where("ProvinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            datalist.clear();
            for (City city:cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level=LEVEL_CITY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            String url=Constants.baseProvince+provinceCode;
            queryFromService(url,"city");

        }

    }

    private void queryProvince() {
        titleText.setText("中国");
        back.setVisibility(View.GONE);
        //从数据库找
        provinceList= DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            datalist.clear();
            for (Province province:provinceList) {
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level=LEVEL_PROVINCE;
        }else {
            //从服务器上获取
            String url= Constants.baseProvince;
            queryFromService(url,"province");
        }
    }

    private void queryCounty() {
        titleText.setText(selectedCity.getCityName());
        back.setVisibility(View.VISIBLE);
        //从数据库中找
        countyList=DataSupport.where("CityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            datalist.clear();
            for (County county:countyList){
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level=LEVEL_COUNTY;

        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String url=Constants.baseProvince+provinceCode+"/"+cityCode;
            queryFromService(url,"county");
        }
    }


    private void queryFromService(String url, final String type) {
        showDialog();
       HttpUtil.GetHttp(url, new StringCallback() {

           @Override
           public void onError(Call call, Exception e, int id) {
                    closeDialog();
               Toast.makeText(getContext(), "加载数据失败", Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onResponse(String response, int id) {
               boolean result=false;
               if ("province".equals(type)){
                    result = ParseJson.handleProvinceResponse(response);


               }else if ("city".equals(type)){
                   result=ParseJson.handleCityResponse(response,selectedProvince.getId());

               }else if ("county".equals(type)){
                   result=ParseJson.handleCountyResponse(response,selectedCity.getId());
               }
               if (result){
                   closeDialog();
                   if ("province".equals(type)){
                       queryProvince();
                   }else if ("city".equals(type)){

                     queryCity();
                   }else if ("county".equals(type)){
                       queryCounty();
                   }
               }

           }
       });

    }

    private void showDialog(){
        if (progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }
    private void closeDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }

    }
}
