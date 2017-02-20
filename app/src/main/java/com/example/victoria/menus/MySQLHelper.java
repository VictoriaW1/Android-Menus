package com.example.victoria.menus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weiying on 2017/2/16.
 */

public class MySQLHelper extends SQLiteOpenHelper {
    public static final String FOODCATEGORY = "FoodCategory";
    public static final String FOOD = "Food";

    private String CREATE_FOODCATEGORY = "create table FoodCategory ("
                                                    + "id integer primary key autoincrement, "
                                                    + "name text, "
                                                    + "foods text) ";

    private String CREATE_FOOD = "create table Food ("
                                                    + "id integer primary key autoincrement, "
                                                    + "name text, "
                                                    + "parent text,"
                                                    + "materials text)" ;
    private Context mContext;

    public MySQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_FOODCATEGORY);
        db.execSQL(CREATE_FOOD);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

}
