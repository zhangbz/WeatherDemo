package com.example.janiszhang.weatherdemo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.janiszhang.weatherdemo.MainActivity;
import com.example.janiszhang.weatherdemo.bean.WeatherData;
import com.example.janiszhang.weatherdemo.bean.WeatherDataStatus;
import com.example.janiszhang.weatherdemo.receiver.AutoUpdateReceiver;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by janiszhang on 2016/3/11.
 */
public class AutoUpdateService extends Service{

    private String mCityname;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 *60 * 1000;//8小时
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences sp = getSharedPreferences("last", Context.MODE_PRIVATE);
        mCityname = sp.getString("cityname", "");
        String httpArg = null;
        if (!TextUtils.isEmpty(mCityname)) {
            try {
                httpArg = "city=" + URLEncoder.encode(mCityname, "UTF-8");//中文需要编码
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String str = null;
            str = MainActivity.request(MainActivity.HTTPURL, httpArg);
            Gson gson = new Gson();
            WeatherDataStatus weatherDataStatus = gson.fromJson(str, WeatherDataStatus.class);
            WeatherData weatherData = weatherDataStatus.getWeatherData().get(0);
            saveData(weatherData);
        }
    }


    private void saveData(WeatherData weatherData) {
//        mWritableDatabase = mWeatherDataDB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //这里数据的获取和upUIData的重复了,是可以优化的地方
        //另外这里的字符串硬编码太多了,容易出问题,最好的办法是在MyDbHelper中提取出来
        contentValues.put("cityname", mCityname);
        contentValues.put("updatetime", weatherData.getBasic().getUpdate().getLoc());
        contentValues.put("weatherstate", weatherData.getNow().getCond().getTxt());
        contentValues.put("temperature", weatherData.getNow().getFl());
        contentValues.put("level", weatherData.getAqi().getCity().getAqi());
        contentValues.put("evaluate", weatherData.getAqi().getCity().getQlty());
        contentValues.put("date0", weatherData.getDaily_forecast().get(0).getDate());
        contentValues.put("state0", weatherData.getDaily_forecast().get(0).getCond().getTxt_d());
        contentValues.put("max0", weatherData.getDaily_forecast().get(0).getTmp().getMax());
        contentValues.put("min0", weatherData.getDaily_forecast().get(0).getTmp().getMin());
        contentValues.put("date1", weatherData.getDaily_forecast().get(1).getDate());
        contentValues.put("state1", weatherData.getDaily_forecast().get(1).getCond().getTxt_d());
        contentValues.put("max1", weatherData.getDaily_forecast().get(1).getTmp().getMax());
        contentValues.put("min1", weatherData.getDaily_forecast().get(1).getTmp().getMin());
        contentValues.put("date2", weatherData.getDaily_forecast().get(2).getDate());
        contentValues.put("state2", weatherData.getDaily_forecast().get(2).getCond().getTxt_d());
        contentValues.put("max2", weatherData.getDaily_forecast().get(2).getTmp().getMax());
        contentValues.put("min2", weatherData.getDaily_forecast().get(2).getTmp().getMin());
        contentValues.put("savetime", System.currentTimeMillis() + "");
        Uri uri = Uri.parse("content://com.example.janiszhang.weatherdemo.provider/weatherdata");
            getContentResolver().update(uri,contentValues,"cityname = ?" , new String[] {mCityname});
    }

}
