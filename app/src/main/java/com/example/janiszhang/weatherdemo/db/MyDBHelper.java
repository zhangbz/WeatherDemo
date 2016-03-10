package com.example.janiszhang.weatherdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by janiszhang on 2016/3/10.
 */
public class MyDBHelper extends SQLiteOpenHelper{

    public static final String CREATE_TABLE = "create table weatherdata(" +
            "id integer primary key autoincrement, " +
            "cityname text, " +
            "updatetime text, " +
            "weatherstate text, " +
            "temperature text, " +
            "level text, " +
            "evaluate text, " +
            "date0 text, " +
            "state0 text, " +
            "max0 text, " +
            "min0 text, " +
            "date1 text, " +
            "state1 text, " +
            "max1 text," +
            "min1 text," +
            "date2 text," +
            "state2 text," +
            "max2 text," +
            "min2 text," +
            "savetime text)";
    private Context mContext;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Toast.makeText(mContext, "Create Succeeded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
