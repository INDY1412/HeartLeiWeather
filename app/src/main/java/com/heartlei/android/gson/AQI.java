package com.heartlei.android.gson;

import java.util.List;

public class AQI {
    /*public AQICity city;
    public class AQICity {
        public String aqi;
        public String pm25;
    }*/

    public Basic basic;
    public Update update;
    public String status;
    public AirNowCity air_now_city;
    public List<AirNowStation> air_now_station;
}
