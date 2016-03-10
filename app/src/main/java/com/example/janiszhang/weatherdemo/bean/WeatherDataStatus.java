package com.example.janiszhang.weatherdemo.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by janiszhang on 2016/3/10.
 */
public class WeatherDataStatus {

    @SerializedName("HeWeather data service 3.0")
    private List<WeatherData> weatherData;

    public List<WeatherData> getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(List<WeatherData> weatherData) {
        this.weatherData = weatherData;
    }

}
