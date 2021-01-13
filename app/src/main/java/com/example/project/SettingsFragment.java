package com.example.project;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class SettingsFragment extends Fragment {

    static TextInputEditText weightInput;
    static TextInputEditText heightInput;
    NotificationManager notificationManager;
    private  SQLiteDatabase database;
   static Cursor  cursor;

   static LinearLayout LINEAR_LAYOUT;
   static  TextView TV;
        ArrayList<Integer> ids;
   static Pair<Integer, Integer> timeOfNotif;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    //получение ключей с настройками
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int complexity;
    TextView complexityText;
    @Override
    public void onStart() {
        super.onStart();

        //Я УЖЕ ХОЧУ СДОХНУТЬ ОТ ЭТИХ ФРАГМЕНТОВ
        LINEAR_LAYOUT = getView().findViewById(R.id.notificationLayout);

        //датабазы агейн
        DataBaseOpen dataBaseOpen = new DataBaseOpen(getContext());
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

        database.execSQL("CREATE TABLE IF NOT EXISTS notifications (_id INTEGER PRIMARY KEY, " +
                "hours INTEGER, " + "minutes INTEGER, " +
                "isMonday INTEGER, isTuesday INTEGER, isWednsday INTEGER, isThursday INTEGER, isFriday INTEGER, isSaturday INTEGER, isSunday INTEGER )");

        cursor = database.rawQuery("SELECT * FROM notifications", null);
        //отгрузка
       // Statistic(database);

        //отгрузка из БД листа
        LoadNotificationList();

        notificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        final Button btn = getView().findViewById(R.id.newNotificationButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewNotification();
            }
        });

        //иницилизация ключей с настройками
        sharedPreferences = getActivity().getSharedPreferences("settingsPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        complexity =  sharedPreferences.getInt("ComplexityKey", 0);


        complexityText = getView().findViewById(R.id.complexityText);
            complexityText.setText("Вы будете делать " + (int)Math.round(6 + complexity*0.04*6) + " приседаний");

        weightInput = getView().findViewById(R.id.weightTextInput);
        heightInput = getView().findViewById(R.id.heightTextInput);
        if(sharedPreferences.contains("Weight"))
        {
            weightInput.setText(String.valueOf(sharedPreferences.getInt("Weight",0)));
        }

        if(sharedPreferences.contains("Height"))
        {
            heightInput.setText(String.valueOf(sharedPreferences.getInt("Height", 0)));
        }
        IMTcalc(sharedPreferences.getInt("Height",0), sharedPreferences.getInt("Weight",0));

        weightInput.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent)
            {
                if(i == keyEvent.KEYCODE_ENTER)
                {
                    if(weightInput.getText().toString().length() > 0 )
                    {
                        int num = Integer.parseInt(weightInput.getText().toString());

                        if (num > 150 || num < 40)
                        {
                            num = num > 150 ? 150 : 40;
                            weightInput.setText(String.valueOf(num));
                            weightInput.setError("введите корректный вес");

                        }
                        else
                        {

                        }
                        editor.putInt("Weight", Integer.parseInt(weightInput.getText().toString()));
                        editor.apply();
                        IMTcalc(sharedPreferences.getInt("Height",0), sharedPreferences.getInt("Weight",0));
                    }
                    else
                    {

                    }

                }

                return false;
            }
        });
        heightInput.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent)
            {
                if(i == keyEvent.KEYCODE_ENTER)
                {
                    if(heightInput.getText().toString().length() > 0 )
                    {
                        int num = Integer.parseInt(heightInput.getText().toString());

                        if (num > 200 || num < 110)
                        {
                            num = num > 200 ? 200 : 110;
                            heightInput.setText(String.valueOf(num));
                            heightInput.setError("введите корректный рост");

                        }
                        else
                        {

                        }
                        editor.putInt("Height", Integer.parseInt(heightInput.getText().toString()));
                        editor.apply();
                        IMTcalc(sharedPreferences.getInt("Height",0), sharedPreferences.getInt("Weight",0));
                    }
                    else
                    {

                    }

                }

                return false;
            }
        });


        SeekBar sK = getView().findViewById(R.id.complexity);
        sK.setProgress(complexity);
        sK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                int num = (int)Math.round(6 + i*0.04 * 6);
                complexityText.setText("Вы будете делать " + num + " приседаний");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                editor.putInt("ComplexityKey", seekBar.getProgress());
                editor.apply();

            }
        });
        }
        //оптимальная работа, лучше не трогать
        private void IMTcalc(float height, int weight)
        {
            height /= 100;
            if(height != 0 && weight != 0)
            {
                TextView imtState = getView().findViewById(R.id.imtState);
                TextView IMT = getView().findViewById(R.id.IMTnum);
                float num = (float) (Math.round(weight / (Math.pow(height, 2)) * 100.0) / 100.0);
                IMT.setText(String.valueOf(num));
                if(num < 16)
                {
                    imtState.setText("Выраженный дефицит массы тела\nнорма: 18,5 - 25");
                    IMT.setTextColor(getResources().getColor(R.color.lightRed));
                }
                else if(num > 16 && num < 18.5)
                {
                    imtState.setText("дефицит массы тела\nнорма: 18,5 - 25");
                    IMT.setTextColor(getResources().getColor(R.color.yellow));
                }
                else if(num > 18.5 && num < 25)
                {
                    imtState.setText("норма");
                    IMT.setTextColor(getResources().getColor(R.color.green));
                }

                else if(num > 25 && num < 30)
                {
                    imtState.setText("Предожирение\nнорма: 18,5 - 25");
                    IMT.setTextColor(getResources().getColor(R.color.yellow));
                }
                else if(num > 30 && num < 35)
                {
                    imtState.setText("Ожирение первой степени\nнорма: 18,5 - 25");
                    IMT.setTextColor(getResources().getColor(R.color.lightRed));
                }
                else if(num > 35 && num < 40)
                {
                    imtState.setText("Ожирение второй степени\nнорма: 18,5 - 25");
                    IMT.setTextColor(getResources().getColor(R.color.red));
                }
                else if(num > 40)
                {
                    imtState.setText("Ожирение третьей степени\nнорма: 18,5 - 25");
                    IMT.setTextColor(getResources().getColor(R.color.red));
                }
            }
        }


        public void CreateNewNotification()
        {
            final LinearLayout ln = new LinearLayout(getContext());
            final TextView tV = new TextView(getContext());
            ln.addView(tV);
          final LinearLayout parentView =  getView().findViewById(R.id.notificationLayout);
          parentView.addView(ln);
            ImageButton imageButton = new ImageButton(getContext());
            imageButton.setBackground(getResources().getDrawable(R.drawable.delete));


          //диалоговое окно с выбором времени

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hours, int minute)
                {

                    String hourStr = hours > 9?String.valueOf(hours) : "0" + hours;
                    String minuteStr =  minute> 9? String.valueOf(minute): "0" + minute;
                    timeOfNotif = new Pair<>(hours, minute);


                    tV.setText(  hourStr + " : " +  minuteStr);
                    TV = tV;



                    getActivity().startService(new Intent(getContext(), NotifyService.class));

                        dialogFragment dialog = new dialogFragment();
                        dialog.show(getFragmentManager(), "custom");


                }
            }, 0, 0, true);

            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    IsCancel(parentView, ln);
                }
            });

            timePickerDialog.show();


        }

        private void DeleteButton(int id)
        {

            database.delete("notifications", "_id = ?", new String[]{String.valueOf(id)});
            LoadNotificationList();
        }
        private void LoadNotificationList()
        {
            ids = new ArrayList<>();
            Cursor cursor;
            try {
                cursor = database.rawQuery("SELECT * FROM notifications", null);
                cursor.moveToNext();
            }
            catch (Exception e)
            {
                return;
            }
            final LinearLayout parentView =  getView().findViewById(R.id.notificationLayout);
            parentView.removeAllViews();
            while (!cursor.isAfterLast())
            {
                final LinearLayout ln = new LinearLayout(getContext());
                final TextView tV = new TextView(getContext());
                ln.addView(tV);
                parentView.addView(ln);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.weight = 1.0f;
                ImageButton imageButton = new ImageButton(getContext());
                imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageButton.setBackground(getResources().getDrawable(R.drawable.delete));
                ids.add(cursor.getInt(0));
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DeleteButton(ids.get(parentView.indexOfChild((View) view.getParent())));
                    }

                });
                ln.addView(imageButton, layoutParams);
                //информация о времени
                 StringBuilder notifText = new StringBuilder(cursor.getInt(1) + " : " + cursor.getInt(2) + "    ");
                //информация о днях недели
                 for(int i = 3; i < 10; i++)
                    {
                    if(cursor.getInt(i) == 1)
                        switch (i)
                        {
                            case 3:
                                notifText.append("ПН ");
                                break;
                            case 4:
                                notifText.append("ВТ ");
                                break;
                            case 5:
                                notifText.append("СР ");
                                break;
                            case 6:
                                notifText.append( "ЧТ ");
                                break;
                            case 7:
                                notifText.append( "ПТ ");
                                break;
                            case 8:
                                notifText.append( "СБ ");
                                break;
                            case 9:
                                notifText.append( "ВС ");
                                break;

                        }
                    }
                tV.setText(notifText);
                cursor.moveToNext();

            }
        }


        private void IsCancel(LinearLayout Mainln, LinearLayout to_remove)
        {
            Mainln.removeView(to_remove);
        }
    public static void IsCancel()
    {

        LINEAR_LAYOUT.removeViewAt(LINEAR_LAYOUT.getChildCount()-1);
    }

    public static  void isOk(boolean[] arr, SQLiteDatabase database)
    {
        ContentValues contentValues = new ContentValues();
        StringBuilder stringBuilder = new StringBuilder(TV.getText() + "    ");

      for(int i = 0; i< arr.length; i++)
      {
          if(arr[i])
              switch (i)
              {
                  case 0:
                      stringBuilder.append("ПН ");
                      break;
                  case 1:
                      stringBuilder.append("ВТ ");
                      break;
                  case 2:
                      stringBuilder.append("СР ");
                      break;
                  case 3:
                      stringBuilder.append( "ЧТ ");
                      break;
                  case 4:
                      stringBuilder.append( "ПТ ");
                      break;
                  case 5:
                      stringBuilder.append( "СБ ");
                      break;
                  case 6:
                      stringBuilder.append( "ВС ");
                      break;

              }
          }


      contentValues.put("isMonday", arr[0]);
      contentValues.put("isTuesday", arr[1]);
      contentValues.put("isWednsday", arr[2]);
      contentValues.put("isThursday", arr[3]);
      contentValues.put("isFriday", arr[4]);
      contentValues.put("isSaturday", arr[5]);
      contentValues.put("isSunday", arr[6]);
      contentValues.put("hours", timeOfNotif.first);
      contentValues.put("minutes", timeOfNotif.second);

        int sum = 0;
      for(boolean a: arr)
      {
          if(a)
              sum++;
      }
      if(sum == 0) {
          IsCancel();
          return;
      }
      int i = 0;
      while (true) {
          contentValues.put("_id", i);
          if (database.insert("notifications", null, contentValues) == -1)
          {
            i++;
          }
          else break;
      }
      TV.setText(stringBuilder);
    }

  /*  private void Statistic(SQLiteDatabase database)
    {
        Cursor cursor;
        try {
            cursor = database.rawQuery("SELECT * FROM statistic", null);
        }
        catch (SQLException e)
        {
            LinearLayout ln = Objects.requireNonNull(getActivity()).findViewById(R.id.MOTHEROFLAYOUTS);
            ln.removeView(Objects.requireNonNull(getView()).findViewById(R.id.statisticOfTrain));

            return;
        }

        cursor.moveToLast();
        TextView textView = getView().findViewById(R.id.timeDelta);
        textView.setText(String.valueOf(cursor.getInt(0))+" "+String.valueOf(cursor.getInt(1))+" "+String.valueOf(cursor.getInt(2)));
    } */

}
