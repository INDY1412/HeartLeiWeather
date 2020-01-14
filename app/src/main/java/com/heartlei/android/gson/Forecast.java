package com.heartlei.android.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    /*
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;
    public class Temperature {
        public String max;
        public String min;
    }
    public class More {
        @SerializedName("txt d")
        public String info;
    }*/
    /**
     * cond_code_d : 103
     * cond_code_n : 101
     * cond_txt_d : 晴间多云
     * cond_txt_n : 多云
     * date : 2017-10-26
     * hum : 57
     * pcpn : 0.0
     * pop : 0
     * pres : 1020
     * tmp_max : 16
     * tmp_min : 8
     * uv_index : 3
     * vis : 16
     * wind_deg : 0
     * wind_dir : 无持续风向
     * wind_sc : 微风
     * wind_spd : 5
     */

    public String cond_code_d;
    public String cond_code_n;
    public String cond_txt_d;
    public String cond_txt_n;
    public String date;
    public String hum;
    public String pcpn;
    public String pop;
    public String pres;
    public String tmp_max;
    public String tmp_min;
    public String uv_index;
    public String vis;
    public String wind_deg;
    public String wind_dir;
    public String wind_sc;
    public String wind_spd;
}
