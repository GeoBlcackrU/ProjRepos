package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Array;
import java.util.Calendar;


public class last extends AppCompatActivity {

    int offsetComplexity = 0;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);
        final TextView calories = findViewById(R.id.caloriesText);
        TextView timer = findViewById(R.id.timeText);
        //получение таймера с занятия
        int[] intTimer = getIntent().getExtras().getIntArray("timer");
        timer.setText(String.valueOf(intTimer[0]) + ":" + String.valueOf(intTimer[1]));
        sp = last.this.getSharedPreferences("settingsPreferences", Context.MODE_PRIVATE);
        float calorie = ((float )intTimer[0] +(float) intTimer[1] /60) / 60 * 4 * sp.getInt("Weight", 0);
        calories.setText(String.valueOf(calorie));
        SaveIntoDataBase(intTimer[0]* 60 + intTimer[1], Math.round(calorie));
        SeekBar complexityBar = findViewById(R.id.complexityBar);
        complexityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                switch (i)
                {
                    case 0:
                        offsetComplexity = -10;
                        break;
                    case 1:
                        offsetComplexity = -5;
                        break;
                    case 2:
                        offsetComplexity = 0;
                        break;
                    case 3:
                        offsetComplexity = 5;
                        break;
                    case 4:
                        offsetComplexity = 10;
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed()

    {
        int f = sp.getInt("ComplexityKey", 0) + offsetComplexity;
        if(f > 100)
            sp.edit().putInt("ComplexityKey", 100).apply();
        else if(f<0)
            sp.edit().putInt("ComplexityKey", 0).apply();
        else sp.edit().putInt("ComplexityKey", f).apply();
        startActivity(new Intent(last.this, MainActivity.class));
    }

    public void onClick(View view)
    {
        int f = sp.getInt("ComplexityKey", 0) + offsetComplexity;
        if(f > 100)
       sp.edit().putInt("ComplexityKey", 100).apply();
        else if(f<0)
            sp.edit().putInt("ComplexityKey", 0).apply();
        else sp.edit().putInt("ComplexityKey", f).apply();
        startActivity(new Intent(last.this, MainActivity.class));
    }


    void SaveIntoDataBase(int time, int calories)
    {
        SQLiteDatabase database;
        DataBaseOpen dataBaseOpen = new DataBaseOpen(this);
        try {
            dataBaseOpen.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            database = dataBaseOpen.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        Calendar calendar = Calendar.getInstance();
        ContentValues  contentValues = new ContentValues();
        contentValues.put("dayOfWeek", calendar.get(Calendar.DAY_OF_WEEK));
        contentValues.put("calories", calories);
        contentValues.put("time", time);
        database.execSQL("CREATE TABLE IF NOT EXISTS statistic(dayOfWeek INTEGER, calories INTEGER, time INTEGER)");
        database.insert("statistic", null, contentValues);
        database.close();
    }
}