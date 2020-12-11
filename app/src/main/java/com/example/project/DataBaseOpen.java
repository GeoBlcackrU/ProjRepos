package com.example.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseOpen extends SQLiteOpenHelper {

    static String DB_NAME = "DataBase.db";
    static String DB_PATH = "";
    final static int DB_VERSION = 1;

    final Context mContext;

    public DataBaseOpen(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
