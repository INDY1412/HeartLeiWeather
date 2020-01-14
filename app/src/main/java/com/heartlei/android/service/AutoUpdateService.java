package com.heartlei.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.heartlei.android.WeatherActivity;
import com.heartlei.android.gson.AQI;
import com.heartlei.android.gson.Weather;
import com.heartlei.android.util.HttpUtil;
import com.heartlei.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
//每隔8小时在后台更新天气信息
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
        @Override
        public int onStartCommand(Intent intent , int flags , int startId) {
            updateWeather() ;
            updateBingPic();
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            //int anHour = 8 * 60 * 60 * 1000; //这是8小时的毫秒数
            int anHour =8 * 60 * 60 * 1000; //这是8小时的毫秒数
            //用 SystemClock.elapsedRealtime() 方法可以获取到系统开机至今所经历时间的毫秒数，
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent i = new Intent(this, AutoUpdateService.class);
            //当定时任务被触发的时候，服务的onStartCommand()方法会被执行
            PendingIntent pi = PendingIntent.getService(this, 0, i , 0);
            manager.cancel(pi);
            //ELAPSED_REALTIME_WAKEUP 表示让定时任务的触发时间从系统开机开始算起，但会唤醒CPU
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , triggerAtTime , pi);
            return super.onStartCommand(intent , flags , startId);
        }
    /**
     *更新天气信息
     */

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.cid;
            String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + weatherId + "&key=49ad7ab20d044826bd5138c93bcdb201";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText) ;
                    if (weather != null && "ok".equals(weather.status)) {
//将更新后的数据直接存储到
//SharedPreferences文件中就可以了，因为打开 WeatherActivity 的时候都会优先从 SharedPreferences
//缓存中读取数据。
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).
                                edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        Log.d("Service","update"+responseText);
                    }
                }
            });
        }
    }

    private void updateBingPic() {
        final String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic,new okhttp3.Callback(){
            @Override
            public void onResponse(okhttp3.Call call, Response response)throws IOException {
                final String bingPic = response.body().string();

                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic" ,bingPic);
                editor.apply() ;

            }
            @Override
            public void onFailure(okhttp3.Call call , IOException e) {
                e.printStackTrace();
            }
        }) ;

    }
}
