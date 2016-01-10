package com.varun.gbu_timetables.data;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class MyReciever extends BroadcastReceiver {

    private static final long REPEAT_TIME = 1000 * 30;

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager service = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);


        Intent myIntent = new Intent(context, UpdateService.class);
        // context.startService(myIntent);

        PendingIntent pending = PendingIntent.getBroadcast(context, 0, myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        // start 30 seconds after boot completed
        cal.add(Calendar.SECOND, 30);
        // fetch every 30 seconds
        // InexactRepeating allows Android to optimize the energy consumption
        service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(), REPEAT_TIME, pending);


    }
}