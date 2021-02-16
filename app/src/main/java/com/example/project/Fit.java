package com.example.project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static com.example.project.R.color.colorPrimary;
import static com.example.project.R.color.designFifth;
import static com.example.project.R.color.designSecond;
import static com.example.project.R.color.green;
import static com.example.project.R.color.lightGreen;
import static com.example.project.R.color.littlePurple;
import static com.example.project.R.color.pastel;
import static com.example.project.R.color.peach;
import static com.example.project.R.color.whitePaper;

public class Fit extends AppCompatActivity {

    int id_of_focused = 0;
    int id_of_doing = 0;
    private static SQLiteDatabase DB;
    TextView about;
    ActivityLoader loader;
    //нужно для завершения таймера
    boolean isFit = true;
    //минуты и секунды
    Thread th;
    Pair<Integer, Integer> timer;
    //для упражнений на время
    int complexity = 0;
boolean now_to_time = false;
boolean canLook = true;
    //для смены упражнения
    private void SetActive(final int id) {
        SharedPreferences sp;
        sp = getSharedPreferences("settingsPreferences", MODE_PRIVATE);
        final TextView to_do = findViewById(R.id.To_Do);
        about.setText(loader.list.get(id).description);
        if(!loader.list.get(id).on_time) {
            int quantity = (int) Math.round(loader.list.get(id).to_do + sp.getInt("ComplexityKey", 0) * loader.list.get(id).modifier *loader.list.get(id).to_do );
            to_do.setText(String.valueOf(quantity % 2 == 0 ? quantity : quantity + 1));
            now_to_time = false;
            TextView  tV = findViewById(R.id.adviceText);
            tV.setText("");
        }
        else
        {
            if(loader.list.get(id).mode == 0)
                switch (sp.getInt("ComplexityKey", 0) / 25) {
                    case 0:
                        complexity = 15;
                        break;
                    case 1:
                        complexity = 25;
                        break;
                    case 2:
                        complexity = 30;
                        break;
                    case 3:
                        complexity = 40;
                        break;
                    case 4:
                        complexity = 60;
                        break;
                }
                else if(loader.list.get(id).mode ==1)
                    complexity = loader.list.get(id).to_do;

                to_do.setText(String.valueOf(complexity));
                if (id_of_focused == id_of_doing) {
                    Button btn = findViewById(R.id.NextOrSkipBtn);
                    btn.setText("пропустить");
                    now_to_time = true;
                    TextView  tV = findViewById(R.id.adviceText);
                    tV.setText("На время \nНажмите, чтобы запустить таймер");

                }
                else
                {
                    now_to_time = false;
                    TextView  tV = findViewById(R.id.adviceText);
                    tV.setText("");
                }

        }

    }

   // @RequiresApi(api = Build.VERSION_CODES.O)
    void createActivitiesInList(int a) {
        //парамерты для отступа в layout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(20, 0, 0, 0);
        //лист с упражнениями
        final LinearLayout mainLayout = findViewById(R.id.list_of_activities);
        //иницилизация каждого упражнения в списке
        for (int i = 0; i < a; i++) {
            final LinearLayout ln = new LinearLayout(this);
            //...порядковый номер на рукаве...
            final int finalI = i;

            ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (canLook) {
                        //изменить цвет педыдущей кнопки
                        if (id_of_focused > id_of_doing)
                            mainLayout.getChildAt(id_of_focused).setBackgroundColor(0);
                        else if (id_of_focused < id_of_doing) {
                            mainLayout.getChildAt(id_of_focused).setBackgroundColor(getResources().getColor(lightGreen));
                        }
                        //SetActive(finalI);
                        id_of_focused = finalI;
                        //изменение текущей кнопки и прочее
                        if (id_of_focused > id_of_doing) {
                            ln.setBackgroundColor(getResources().getColor(pastel));
                            findViewById(R.id.parentOfParents).setBackgroundColor(getResources().getColor(pastel));
                            Button button = findViewById(R.id.NextOrSkipBtn);
                            button.setText(getResources().getText(R.string.TO_NOW));
                        } else if (id_of_focused < id_of_doing) {
                            ln.setBackgroundColor(getResources().getColor(green));
                            findViewById(R.id.parentOfParents).setBackgroundColor(getResources().getColor(lightGreen));
                            Button button = findViewById(R.id.NextOrSkipBtn);
                            button.setText(getResources().getText(R.string.TO_NOW));
                        } else {
                            findViewById(R.id.parentOfParents).setBackgroundColor(getResources().getColor(whitePaper));
                            Button button = findViewById(R.id.NextOrSkipBtn);
                            button.setText(getResources().getText(R.string.NEXT));
                        }
                        SetActive(finalI);
                    }
                }
            });
            ln.setOrientation(LinearLayout.VERTICAL);
            //иницилизания и добавление пикчи
            ImageView im = new ImageView(this);
            im.setImageResource(getResources().getIdentifier("exercise" + String.valueOf(loader.list.get(i).id_of_pic), "drawable", getPackageName()));

            ln.addView(im);

            //иницилизация и добавление названия
            TextView tx = new TextView(this);
            tx.setText(loader.list.get(i).name);
            tx.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tx.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }
            ln.addView(tx);
            //чтобы отступ был
            if (i > 0)
                mainLayout.addView(ln, layoutParams);
            else {
                ln.setBackgroundColor(getResources().getColor(littlePurple));
                mainLayout.addView(ln);
                about.setText(loader.list.get(0).description);
            }


        }
    }

   // @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        about = findViewById(R.id.about);
        //БД
        DataBaseOpen dataBaseOpen = new DataBaseOpen(this);
        try {
            dataBaseOpen.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            DB = dataBaseOpen.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        int scenario = getIntent().getExtras().getInt("scenario");
        loader = new ActivityLoader(scenario);
        createActivitiesInList(loader.GetLengthOfActivities());
        SetActive(0);

        final TextView uiTimer = findViewById(R.id.timer);
      th =  new Thread(new Runnable() {
            @Override
            public void run() {
                final Date date = new Date();
                while (true) {
                    final Date dateNow = new Date();

                   // uiTimer.setText(String.valueOf((dateNow.getTime() - date.getTime()) / 1000 / 60 + ":" + (dateNow.getTime() - date.getTime()) / 1000 % 60));
                    uiTimer.post(new Runnable() {
                        @Override
                        public void run() {
                            uiTimer.setText(String.valueOf((dateNow.getTime() - date.getTime()) / 1000 / 60 + ":" + (dateNow.getTime() - date.getTime()) / 1000 % 60));
                        }
                    });
                    if (!isFit) {
                        int min = (int) (dateNow.getTime() - date.getTime()) / 1000 / 60;
                        int sec = (int) (dateNow.getTime() - date.getTime()) / 1000 % 60;
                        timer = new Pair<>(min, sec);
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
      th.start();
    }

    public void OnClick(View view) {

        LinearLayout mainLayout = findViewById(R.id.list_of_activities);
        if (id_of_doing + 1 < mainLayout.getChildCount()) {

            if (id_of_doing == id_of_focused) {
                canLook = true;
                Button button = findViewById(R.id.NextOrSkipBtn);
                button.setText(getResources().getText(R.string.NEXT));
                mainLayout.getChildAt(id_of_doing).setBackgroundColor(getResources().getColor(lightGreen));
                id_of_doing++;
                mainLayout.getChildAt(id_of_doing).setBackgroundColor(getResources().getColor(littlePurple));
            }
            else {
            mainLayout.getChildAt(id_of_focused).setBackgroundColor(id_of_doing > id_of_focused ? getResources().getColor(lightGreen) : 0);
            Button button = findViewById(R.id.NextOrSkipBtn);
            button.setText(getResources().getText(R.string.NEXT));
            findViewById(R.id.parentOfParents).setBackgroundColor(getResources().getColor(whitePaper));
        }
        id_of_focused = id_of_doing;
            SetActive(id_of_doing);
    }
        else {
            isFit = false;
            Intent intent = new Intent(Fit.this, last.class);
            //джоиним поток, чтобы не крашилось
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            intent.putExtra("timer", new int[]{timer.first, timer.second});
            startActivity(intent);

        }
}
//для упражнений на время
        public  void OnClickTimer(final View view)
        {
            if(now_to_time)
            {
                canLook = false;
                now_to_time = false;
                MediaPlayer mpStart =  new MediaPlayer();
                MediaPlayer mpEnd = new MediaPlayer();
                mpEnd = MediaPlayer.create(this, R.raw.end);
                MediaPlayer.create(this, R.raw.start).start();
                final MediaPlayer finalMpEnd = mpEnd;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Date date = new Date();
                        final TextView number = findViewById(R.id.To_Do);

                        while (!canLook)
                        {

                             int sec;
                            final Date newDate = new Date();
                            sec = complexity - (int)(newDate.getTime() - date.getTime()) / 1000 % 61;
                             number.setText(String.valueOf(sec));
                             if(sec == 3)
                                 finalMpEnd.start();
                            if(sec <=0 )
                            {
                                final Button btn = findViewById(R.id.NextOrSkipBtn);
                                btn.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn.setText("далее");
                                    }
                                });
                                break;
                            }
                        }
                    }
                }).start();
            }


        }


    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
            //нужно заканчивать упражнение
        {
            isFit = false;
            //так же джоиним поток
            Intent intent = new Intent(Fit.this, last.class);
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            intent.putExtra("timer", new int[]{timer.first, timer.second});
            startActivity(intent);
        }
        else
            Toast.makeText(getBaseContext(), "Нажмите еще раз для выхода",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }


    static class ActivityLoader
    {

        private Cursor cursor;
        String test;
        //массив с упражнениями
        int[] arrayOfEx;
       public ArrayList<EX> list = new ArrayList<>();
        ActivityLoader(int scenario)
        {

            //че-то крашилось
            cursor = DB.rawQuery("SELECT * FROM ex", null);
            scenarioLoader(scenario);
            CreateActivities();
            cursor.close();
            DB.close();
        }

        //рандомные цифры для теста
        private void scenarioLoader(int scenario)
        {
            switch (scenario)
            {
                case 0:
                    arrayOfEx = new int[]{0, 12, 25, 5, 6, 14, 4, 21, 10, 9 };
                    break;
                case 1:
                    arrayOfEx = new int[]{9, 19, 20, 17, 16, 11, 24};
                    break;
                case 2:
                    arrayOfEx = new int[]{15, 22, 13, 2, 5, 7, 8, 4, 1, 23, 10 };
                    break;
                case 3:
                    arrayOfEx = new int[]{19,17, 20, 16, 15, 10, 23, 18, 24 };
                    break;
            }
        }

      //  public void CreateActivities()
     //   {
           //шоб я сдох
           // for(int a:arrayOfEx) {
               // cursor.moveToPosition(a);
              //  list.add(cursor.getInt(0), new EX(cursor.getInt(0),cursor.getInt(1) ,cursor.getString(2), cursor.getString(3), cursor.getInt(4) == 1, cursor.getInt(5), cursor.getInt(6), cursor.getFloat(7)));
           // }

        //}
        public void CreateActivities()
        {
            // создание сортированного массива упражнений для выгрузки упражнений в O(n)
            int[] a = new int[arrayOfEx.length];

            for(int i = 0; i < a.length; i++)
                a[i] = arrayOfEx[i];

            Arrays.sort(a);
            Map<Integer, EX> ExList = new HashMap<>(); //хэш для обращения в O(1)

            int i = 0;
            cursor.moveToNext();
            while (!cursor.isAfterLast() && i < a.length )
            {
                if(cursor.getInt(0) == a[i])
                {
                    ExList.put(cursor.getInt(0), new EX(cursor.getInt(0),cursor.getInt(1) ,cursor.getString(2), cursor.getString(3), cursor.getInt(4) == 1, cursor.getInt(5), cursor.getInt(6), cursor.getFloat(7)));
                    i++;
                }
                else
                {
                    cursor.moveToNext();
                }
            }

            for (int ofEx : arrayOfEx) {
                list.add(ExList.get(ofEx));
            }
        }
        public int GetLengthOfActivities() {
            return arrayOfEx.length;
        }

        public class EX
        {
            int id;
            int id_of_pic;
            String name;
            String description;
            boolean on_time;
            int mode;
            int to_do;
            float modifier;

            EX(int id, int id_of_pic, String name, String description, boolean on_time, int mode, int to_do, float modifier)
            {
                this.id = id;
                this.id_of_pic = id_of_pic;
                this.name = name;
                this.description = description;
                this.to_do = to_do;
                this.on_time = on_time;
                this.mode = mode;
                this.modifier = modifier;
            }

        }
    }
}