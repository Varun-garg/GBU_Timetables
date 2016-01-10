package com.varun.gbu_timetables.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

/**
 * com.varun.gbu_timetables.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 1/9/2016 7:43 PM.
 */
public class UpdateService extends Service {

    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode;

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind;
    FetchDbTask fetchDbTask = null;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {

    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (fetchDbTask != null)
            if (fetchDbTask.getStatus() == AsyncTask.Status.RUNNING)
                return mStartMode;

        fetchDbTask = new FetchDbTask(getApplicationContext(), true);
        fetchDbTask.execute();

        mStartMode = Service.START_NOT_STICKY;
        return mStartMode;
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(getApplicationContext(), UpdateService.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, 30000, pi);

    }

}
