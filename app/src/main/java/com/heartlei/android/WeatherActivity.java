package com.heartlei.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.heartlei.android.gson.AQI;
import com.heartlei.android.gson.Forecast;
import com.heartlei.android.gson.Weather;
import com.heartlei.android.service.AutoUpdateService;
import com.heartlei.android.util.HttpUtil;
import com.heartlei.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;

    public DrawerLayout drawerLayout;
    private Button navButton;
    private Button localButton;

    public SwipeRefreshLayout swipeRefresh;

    private String mWeatherId;//，用于记录城市的天气id
    private String parentCity = "beijing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //private String weatherId;//，用于记录城市的天气id

        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21)
        {
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );//表示活动的布局会显示在状态栏上面
            getWindow().setStatusBarColor(Color.TRANSPARENT);//将状态栏设置成透明色。
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件

        weatherLayout =  findViewById(R.id.weather_layout);
        titleCity =  findViewById(R.id.title_city);
        titleUpdateTime =  findViewById(R.id.title_update_time);
        degreeText =  findViewById(R.id.degree_text) ;
        weatherInfoText =  findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText =findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        swipeRefresh =findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置下拉刷新进度条的颜色
        aqiText.setText("28");
        pm25Text.setText("19");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather" , null);
        if (weatherString != null)
        {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.cid;
            requestParentCity(mWeatherId);
            for(int i = 0; i < 10000000; i++);
            showWeatherInfo(weather);
        }
        else {
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestParentCity(mWeatherId);
            for(int i = 0; i < 10000000; i++);
            requestWeather(mWeatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestParentCity(mWeatherId);
                for(int i = 0; i < 10000000; i++);
                requestWeather(mWeatherId);//，当触发了下拉刷新操作的时候，就会回调这个监听器的onRefresh()方法
            }
        });
        //初始化各控件
        bingPicImg = findViewById(R.id.bing_pic_img);
        String bingPic = prefs.getString("bing_pic" ,null);//尝试从SharedPreferences 中读取缓存的背景图片
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);//有缓存的话就直接使用 Glide 来加载这张图片，
        } else {
            loadBingPic();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//打开滑动菜单
            }
        });

        localButton = findViewById(R.id.local_button);//定位当前城市并显示天气
        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < 10000000; i++);
                requestWeather("auto_ip");
            }
        });
    }

    public void requestParentCity(final String weatherId) {


        final String weatherUrl="https://free-api.heweather.com/s6/weather?location="+weatherId+"&key=49ad7ab20d044826bd5138c93bcdb201";
        HttpUtil.sendOkHttpRequest(weatherUrl,new okhttp3.Callback(){
            @Override
            public void onResponse(okhttp3.Call call, Response response)throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                parentCity = weather.basic.parent_city;
                System.out.println("hhh3423h"+parentCity);
            }
            @Override
            public void onFailure(okhttp3.Call call , IOException e) {
                e.printStackTrace();
                //通过 runOnUiTread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取城市信息失败..",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }) ;
    }

    /**
    *根据天气 id 请求城市天气信息
    */
    public void requestWeather(final String weatherId) {

        requestParentCity(mWeatherId);

        final String weatherUrl="https://free-api.heweather.com/s6/weather?location="+weatherId+"&key=49ad7ab20d044826bd5138c93bcdb201";
        HttpUtil.sendOkHttpRequest(weatherUrl,new okhttp3.Callback(){
            @Override
            public void onResponse(okhttp3.Call call, Response response)throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){

                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).
                                    edit ();
                            editor.putString ("weather",responseText);
                            editor.apply();
                            //注意这里要修改mWeatherId的值因为打开app后mWeatherId
                            //缓存在本地的是上一次关闭时记录的值，而当选择其它地区时进行一次新的服务器请求
                            //再次刷新应该保存新的mWeatherId
                            mWeatherId = weather.basic.cid;
                            showWeatherInfo(weather);
                        } else {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                       }
                        swipeRefresh.setRefreshing(false);//用于表示刷新事件结束，并隐藏刷新进度条。
                    }
                });
            }
            @Override
            public void onFailure(okhttp3.Call call , IOException e) {
                e.printStackTrace();
                //通过 runOnUiTread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败..",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }) ;

        final String aqiUrl="https://free-api.heweather.net/s6/air/now?location="+parentCity+"&key=49ad7ab20d044826bd5138c93bcdb201";
        //final String aqiUrl="https://free-api.heweather.net/s6/air/now?location=beijing&key=49ad7ab20d044826bd5138c93bcdb201";
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText=response.body().string();
                final AQI Aqi=Utility.handleAQIResponse(responseText);
                //Log.d("Response","is "+responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if((Aqi != null) && "ok".equals(Aqi.status))
                        {
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();

                            editor.putString("weather",responseText);
                            editor.apply();

                            showAQIInfo(Aqi);
                        }else
                        {
                            //Toast.makeText(WeatherActivity.this,"获取天气信息AQI失败",Toast.LENGTH_LONG).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }

                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(WeatherActivity.this,"获取天气信息AQI失败..",Toast.LENGTH_LONG).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.location;
        String updateTime = weather.update.loc.split(" ")[1];
        String degree = weather.now.tmp + "℃";
        String weatherInfo = weather.now.cond_txt;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(int i=0;i<3;i++ )
        {View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataText=view.findViewById(R.id.date_text);
            TextView infoText=view.findViewById(R.id.info_text);
            TextView maxText=view.findViewById(R.id.max_text);
            TextView minText=view.findViewById(R.id.min_text);

            dataText.setText(weather.daily_forecast.get(i).date);
            infoText.setText(weather.daily_forecast.get(i).cond_txt_n);
            maxText.setText(weather.daily_forecast.get(i).tmp_max);
            minText.setText(weather.daily_forecast.get(i).tmp_min);
            forecastLayout.addView(view);
        }
        String comfort = "舒适度: " + weather.lifestyle.get(0).txt;
        String carWash = "洗车指数: " + weather.lifestyle.get(6).txt;
        String sport = "运动建议: " + weather.lifestyle.get(3).txt;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void showAQIInfo(AQI Aqi) {

        if(Aqi!=null)
        {
            aqiText.setText(Aqi.air_now_city.aqi);
            pm25Text.setText(Aqi.air_now_city.pm25);
        }
    }

    private void loadBingPic() {
        final String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic,new okhttp3.Callback(){
            @Override
            public void onResponse(okhttp3.Call call, Response response)throws IOException {
                final String bingPic = response.body().string();

                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic" ,bingPic);
                editor.apply() ;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into
                                (bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(okhttp3.Call call , IOException e) {
                e.printStackTrace();

            }
        }) ;

    }

}
