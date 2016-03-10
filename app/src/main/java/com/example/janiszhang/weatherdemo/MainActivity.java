package com.example.janiszhang.weatherdemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.janiszhang.weatherdemo.bean.WeatherData;
import com.example.janiszhang.weatherdemo.bean.WeatherDataStatus;
import com.example.janiszhang.weatherdemo.db.MyDBHelper;
import com.example.janiszhang.weatherdemo.service.AutoUpdateService;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public static final String HTTPURL = "http://apis.baidu.com/heweather/weather/free";
    String httpArg = "city=beijing";

    private EditText mCityName;
    private Button mQueryButton;
    private TextView mUpateTime;
    private Button mUpdateButton;
    private TextView mWeatherState;
    private TextView mTemperature;
    private TextView mWeatherLevel;
    private TextView mWeatherEvaluagte;
    private TextView mDate0;
    private TextView mState0;
    private TextView mMax0;
    private TextView mMin0;
    private TextView mDate1;
    private TextView mState1;
    private TextView mMax1;
    private TextView mMin1;
    private TextView mDate2;
    private TextView mState2;
    private TextView mMax2;
    private TextView mMin2;

    private boolean shouldUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        SharedPreferences last = getSharedPreferences("last", Context.MODE_PRIVATE);
        String cityname = last.getString("cityname", "");
        if (!TextUtils.isEmpty(cityname)) {
            Uri uri = Uri.parse("content://com.example.janiszhang.weatherdemo.provider/weatherdata");
            Cursor cursor = getContentResolver().query(uri, null, "cityname = ?", new String[]{mCityName.getText() + ""}, null);
            if (cursor.moveToFirst()) {// !!!!!!!!!!!!!!!!!!!!!!
                //数据库查询
                updateUIfromDatabase(cursor);
            }
        }

        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mCityName.getText())) {
                    //本地查询
                    Uri uri = Uri.parse("content://com.example.janiszhang.weatherdemo.provider/weatherdata");
                    Log.i("zhangbz", "1");
                    Cursor cursor = getContentResolver().query(uri, null, "cityname = ?", new String[]{mCityName.getText() + ""}, null);
                    if (cursor.moveToFirst()) {// !!!!!!!!!!!!!!!!!!!!!!
                        Log.i("zhangbz", cursor.getString(cursor.getColumnIndex("savetime")));

                       if ((System.currentTimeMillis() - Long.valueOf(cursor.getString(cursor.getColumnIndex("savetime")))) < (1000*60)) {//Long.getLong()?????????
                          //数据库查询
                           Log.i("zhangbz", "3");
                           updateUIfromDatabase(cursor);
                           Log.i("zhangbz", "数据库查询");
                       } else {
                           //网络查询并update到数据库
                           shouldUpdate = true;
                           Log.i("zhangbz", "4");
                           try {
                               httpArg = "city=" + URLEncoder.encode(mCityName.getText().toString(), "UTF-8");//中文需要编码
                           } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                           }
                           Log.i("zhangbz", "网络查询并update到数据库");
                           new MyAsyncTask().execute(httpArg);

                       }
                    } else {
                        //网络查询并insert到数据库
                        shouldUpdate = false;//需要insert
                        try {
                            httpArg = "city=" + URLEncoder.encode(mCityName.getText().toString(), "UTF-8");//中文需要编码
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.i("zhangbz", "网络查询并insert到数据库");
                        new MyAsyncTask().execute(httpArg);
                    }


                    SharedPreferences sp = getSharedPreferences("last", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("cityname", mCityName.getText().toString());
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, AutoUpdateService.class);
                    startService(intent);
                }
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mCityName.getText().toString())) {
                    shouldUpdate = true;
                    try {
                        httpArg = "city=" + URLEncoder.encode(mCityName.getText().toString(), "UTF-8");//中文需要编码
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    new MyAsyncTask().execute(httpArg);
                }
            }
        });
    }

    private void updateUIfromDatabase(Cursor cursor) {
        mUpateTime.setText(cursor.getString(cursor.getColumnIndex("updatetime")));
        mWeatherState.setText(cursor.getString(cursor.getColumnIndex("weatherstate")));
        mTemperature.setText(cursor.getString(cursor.getColumnIndex("temperature")));
        mWeatherLevel.setText(cursor.getString(cursor.getColumnIndex("level")));
        mWeatherEvaluagte.setText(cursor.getString(cursor.getColumnIndex("evaluate")));

        mDate0.setText(cursor.getString(cursor.getColumnIndex("date0")));
        mState0.setText(cursor.getString(cursor.getColumnIndex("state0")));
        mMax0.setText(cursor.getString(cursor.getColumnIndex("max0")));
        mMin0.setText(cursor.getString(cursor.getColumnIndex("min0")));

        mDate1.setText(cursor.getString(cursor.getColumnIndex("date1")));
        mState1.setText(cursor.getString(cursor.getColumnIndex("state1")));
        mMax1.setText(cursor.getString(cursor.getColumnIndex("max1")));
        mMin1.setText(cursor.getString(cursor.getColumnIndex("min1")));

        mDate2.setText(cursor.getString(cursor.getColumnIndex("date2")));
        mState2.setText(cursor.getString(cursor.getColumnIndex("state2")));
        mMax2.setText(cursor.getString(cursor.getColumnIndex("max2")));
        mMin2.setText(cursor.getString(cursor.getColumnIndex("min2")));
    }

    private void findViewById() {
        mCityName = (EditText) findViewById(R.id.city_name);
        mQueryButton = (Button) findViewById(R.id.query_button);
        mUpateTime = (TextView) findViewById(R.id.update_time);
        mUpdateButton = (Button) findViewById(R.id.update_button);
        mWeatherState = (TextView) findViewById(R.id.tv_weather_state);
        mTemperature = (TextView) findViewById(R.id.tv_temperature);
        mWeatherLevel = (TextView) findViewById(R.id.tv_weather_level);
        mWeatherEvaluagte = (TextView) findViewById(R.id.tv_weather_evaluate);
        mDate0 = (TextView) findViewById(R.id.date0);
        mState0 = (TextView) findViewById(R.id.state0);
        mMax0 = (TextView) findViewById(R.id.max0);
        mMin0 = (TextView) findViewById(R.id.min0);
        mDate1 = (TextView) findViewById(R.id.date1);
        mState1 = (TextView) findViewById(R.id.state1);
        mMax1 = (TextView) findViewById(R.id.max1);
        mMin1 = (TextView) findViewById(R.id.min1);
        mDate2 = (TextView) findViewById(R.id.date2);
        mState2 = (TextView) findViewById(R.id.state2);
        mMax2 = (TextView) findViewById(R.id.max2);
        mMin2 = (TextView) findViewById(R.id.min2);

    }

    class MyAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i("zhangbz", "doInBackground");
            String str = null;
            str = request(HTTPURL, params[0]);
            Log.i("zhangbz", str.toString());

            return str.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            WeatherDataStatus weatherDataStatus = gson.fromJson(s, WeatherDataStatus.class);
            WeatherData weatherData = weatherDataStatus.getWeatherData().get(0);

            upUIData(weatherData);

            saveData(weatherData);
        }
    }

    private void saveData(WeatherData weatherData) {
        ContentValues contentValues = new ContentValues();
        //这里数据的获取和upUIData的重复了,是可以优化的地方
        //另外这里的字符串硬编码太多了,容易出问题,最好的办法是在MyDbHelper中提取出来
        contentValues.put("cityname", mCityName.getText().toString());
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
        if(shouldUpdate) {
            getContentResolver().update(uri,contentValues,"cityname = ?" , new String[] {mCityName.getText().toString()});
        } else {
            getContentResolver().insert(uri, contentValues);
        }
    }

    //更新UI
    private void upUIData(WeatherData weatherData) {
        mUpateTime.setText(weatherData.getBasic().getUpdate().getLoc());
        mWeatherState.setText(weatherData.getNow().getCond().getTxt());
        mTemperature.setText(weatherData.getNow().getFl());
        mWeatherLevel.setText(weatherData.getAqi().getCity().getAqi());
        mWeatherEvaluagte.setText(weatherData.getAqi().getCity().getQlty());

        mDate0.setText(weatherData.getDaily_forecast().get(0).getDate());
        mState0.setText(weatherData.getDaily_forecast().get(0).getCond().getTxt_d());
        mMax0.setText(weatherData.getDaily_forecast().get(0).getTmp().getMax());
        mMin0.setText(weatherData.getDaily_forecast().get(0).getTmp().getMin());

        mDate1.setText(weatherData.getDaily_forecast().get(1).getDate());
        mState1.setText(weatherData.getDaily_forecast().get(1).getCond().getTxt_d());
        mMax1.setText(weatherData.getDaily_forecast().get(1).getTmp().getMax());
        mMin1.setText(weatherData.getDaily_forecast().get(1).getTmp().getMin());

        mDate2.setText(weatherData.getDaily_forecast().get(2).getDate());
        mState2.setText(weatherData.getDaily_forecast().get(2).getCond().getTxt_d());
        mMax2.setText(weatherData.getDaily_forecast().get(2).getTmp().getMax());
        mMin2.setText(weatherData.getDaily_forecast().get(2).getTmp().getMin());
    }


    /**
     *
     * 网络查询
     * @param httpUrl
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "37c592bfd2e09cd7059dbd5a25c1b0c1");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
