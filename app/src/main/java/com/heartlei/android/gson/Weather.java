package com.heartlei.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    /*
    public String status;//成功返回ok，失败则会返回具体的原因
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;*/
    public Basic basic;
    public Now now;
    public String status;
    public Update update;
    public List<Forecast> daily_forecast;
    public List<Lifestyle> lifestyle;
}
