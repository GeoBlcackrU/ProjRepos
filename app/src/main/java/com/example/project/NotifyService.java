package com.example.project;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.util.Pair;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotifyService extends Service {
    NotificationManager notificationManager;
    @Override
    public void onCreate() {
        super.onCreate();
      //  Toast.makeText(this, "Эта хрень включилась", Toast.LENGTH_SHORT).show();
    }
    int i;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  Toast.makeText(this, "Эта хрень запустила команду", Toast.LENGTH_SHORT).show();
        Calendar calendar;

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

        Cursor cursor = database.rawQuery("SELECT * FROM notifications", null);

                cursor.moveToNext();
            calendar = Calendar.getInstance();
            int a = 0;
            while (!cursor.isAfterLast()) {
                a++;
                if (cursor.getInt(1) == calendar.get(Calendar.HOUR_OF_DAY) && cursor.getInt(2) == calendar.get(Calendar.MINUTE) && cursor.getInt(calendar.get(Calendar.DAY_OF_WEEK) + 1)==1)
                {
                    CreateNotify(cursor);
                }
                cursor.moveToNext();
            }
            if(a == 0)
                i = 0;
            else  i = 1;
        stopSelf();
       return  Service.START_STICKY;
    }

 private  void  CreateNotify(Cursor cursor)
 {
     notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
     Intent notificationIntent = new Intent(this, MainActivity.class);
     PendingIntent contentIntent = PendingIntent.getActivity(this,
             0, notificationIntent,
             PendingIntent.FLAG_CANCEL_CURRENT);
     NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, "CHANNEL")
             .setAutoCancel(true)
             .setSmallIcon(R.drawable.exercise0)
             .setWhen(System.currentTimeMillis())
             .setContentTitle("Время заниматься")
             .setContentText("Вы запланировали занятие на " + cursor.getInt(1) + " : "+ cursor.getInt(2))
             .setContentIntent(contentIntent);

     if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
     {
         NotificationChannel notificationChannel = new NotificationChannel("CHANNEL", "CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
         notificationManager.createNotificationChannel(notificationChannel);
     }
     notificationManager.notify(1, nBuilder.build());

 }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
       // Toast.makeText(this, "Эта хрень удалила таск", Toast.LENGTH_SHORT).show();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
       // Toast.makeText(this, "Эта хрень закрылась", Toast.LENGTH_SHORT).show();
        if(i == 1) {
            AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, alarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= 23) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 45000, pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 45000, pi);
            }
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}