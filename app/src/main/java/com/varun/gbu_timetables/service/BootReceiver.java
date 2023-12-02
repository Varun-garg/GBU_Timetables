package com.varun.gbu_timetables.service;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.varun.gbu_timetables.BuildConfig;
import com.varun.gbu_timetables.R;

import java.util.Calendar;
import java.util.Random;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("GBU-BootRec", "Class Called");
        try {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                if (BuildConfig.DEBUG) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_notification_statue_buddha)
                                    .setColor(context.getResources().getColor(R.color.app_bg_dark))
                                    .setContentTitle("BootReceiver Service initiated");
                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify((new Random()).nextInt(9999 - 1000) + 1000, mBuilder.build());
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 13); // For 1 PM or 2 PM
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                PendingIntent pi = PendingIntent.getService(context.getApplicationContext(), 0,
                        new Intent(context.getApplicationContext(), UpdateDatabaseService.class), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);

            }
        } catch (java.lang.NullPointerException e) {
            Log.e("BootReceiver", e.getMessage());
        }
    }
}