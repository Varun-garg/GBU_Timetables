package com.varun.gbu_timetables.data.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.varun.gbu_timetables.data.MD5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * varun.timetables_sql.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 12/12/2015 10:11 PM.
 */
public class TimetableDbHelper extends SQLiteOpenHelper {

    public static final String DB_MD5_PATH = "App_db_md5";
    private static final String DATABASE_NAME = "varun.db";
    private static final String DB_VERSION_PATH = "App_db_version";
    private static final String DB_LOCK = "DB_LOCK";
    private static final String DB_LOCK_ON = "ON";
    private static final String DB_LOCK_OFF = "OFF";
    private final String LOG_TAG = "TimetableDbHelper";
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public TimetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();

        try {
            while (prefs.getString(DB_LOCK, "").equalsIgnoreCase(DB_LOCK_ON)) //already locked
                TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Log.d("error", e.toString());
            return; //no point of overwriting now.
        }

        if (!this.getReadableDatabase().isDatabaseIntegrityOk()) {
            Toast toast = Toast.makeText(context, "database is corrupted, please reinstall this application.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Cursor c = this.getWritableDatabase().rawQuery("SELECT name from sqlite_master where type = 'table'", null);
        ArrayList<String> list = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                //   Log.d(LOG_TAG,"Table Name => "+c.getString(0));
                list.add(c.getString(0));
                c.moveToNext();
            }
        }
        c.close();

        if (list.size() <= 1) // only sql_master or empty db
        {
            overwriteDB(context, 0, null);
        }

        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int saved_db_version = prefs.getInt(DB_VERSION_PATH, 0);
            int current_db_version = info.versionCode;
            if (current_db_version != saved_db_version) {
                overwriteDB(context, 0, null);
                editor.putInt(DB_VERSION_PATH, current_db_version);
                editor.apply();
                Log.d("updating", "due to version mismatch");
                Log.d("Difference", "new version = " + current_db_version + ", old db version = " + saved_db_version);
            }
        } catch (Exception e) {
            Log.d("error", e.toString());
        }

        if (!this.getReadableDatabase().isDatabaseIntegrityOk()) {
            Toast toast = Toast.makeText(context, "database is corrupted, please reinstall this application.", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private File getDBFile(Context context) {
        String dest_db = context.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
        return new File(dest_db);
    }

    public void overwriteDB(Context context, int mode, String location) {

        Log.d(LOG_TAG, "entering overwriteDB with lock status " + prefs.getString(DB_LOCK, ""));

        try {
            while (prefs.getString(DB_LOCK, "").equalsIgnoreCase(DB_LOCK_ON)) //already locked
                TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Log.d("error", e.toString());
            return; //no point of overwriting now.
        }

        editor.putString(DB_LOCK, DB_LOCK_ON);
        editor.commit();

        Log.d(LOG_TAG, "Ok, we are gonna copy some data");
        String dest_db = context.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
        Log.d(LOG_TAG, "output file location " + dest_db);

        this.close();
        context.deleteDatabase(DATABASE_NAME); //Delete existing db
        try {
            InputStream db_stream;
            if (mode == 0)
                db_stream = context.getAssets().open("varun.db");
            else {
                Log.d("using", location);
                db_stream = context.openFileInput(location);
            }
            OutputStream dest_db_stream = new FileOutputStream(dest_db);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = db_stream.read(buffer)) > 0) {
                dest_db_stream.write(buffer, 0, length);
            }

            dest_db_stream.flush();
            db_stream.close();
            dest_db_stream.close();

        } catch (IOException e) {
            Log.d(LOG_TAG, "Caught IO Exception " + e);
        }
        String new_MD5 = MD5.calculateMD5(getDBFile(context));
        editor.putString(DB_MD5_PATH, new_MD5);
        editor.commit();

        editor.putString(DB_LOCK, DB_LOCK_OFF); //release our lock.
        editor.commit();

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "Calling onCreate");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}
