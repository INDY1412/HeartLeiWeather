package com.heartlei.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    //使用@SerializedName注解的方式来让JSON宇段和Java字段之间建立映射关系
    //json里命名和我们自己的变量命名有差别
    /*@SerializedName( "city")
    public String cityName;//城市名

    @SerializedName("id")
    public String weatherId;//城市对应的天气id

    public Update update;//loc表示天气的更新时间

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }*/

    /**
     * cid : CN101010100
     * location : 北京
     * parent_city : 北京
     * admin_area : 北京
     * cnty : 中国
     * lat : 39.90498734
     * lon : 116.40528870
     * tz : 8.0
     */

    public String cid;
    public String location;
    public String parent_city;
    public String admin_area;
    public String cnty;
    public String lat;
    public String lon;
    public String tz;
}
