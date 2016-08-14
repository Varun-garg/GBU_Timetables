package com.varun.gbu_timetables.data.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.varun.gbu_timetables.data.MD5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * varun.timetables_sql.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 12/12/2015 10:11 PM.
 */
public class TimetableDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "varun.db";
    static final String LOG_TAG = "TimetableDbHelper";
    public static String DB_VERSION_PATH = "App_db_version";
    public static String DB_MD5_PATH = "App_db_md5";
    Context context;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public TimetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        this.context = context;

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
            copy_db(context, 0, null);
        }

        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int saved_db_version = prefs.getInt(DB_VERSION_PATH, 0);
            int current_db_version = info.versionCode;
            if (current_db_version != saved_db_version) {
                copy_db(context, 0, null);
                editor.putInt(DB_VERSION_PATH, current_db_version);
                editor.commit();
                Log.d("updating", "due to version mismatch");
                Log.d("Difference", "new version = " + Integer.toString(current_db_version) + ", old db version = " + Integer.toString(saved_db_version));
            }
        } catch (Exception e) {
            Log.d("error", e.toString());
        }
    }

    public static File get_dest_db(Context context) {
        String dest_db = context.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
        return new File(dest_db);
    }

    public void copy_db(Context context, int mode, String location) {

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

            byte buffer[] = new byte[1024];

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
        String new_MD5 = MD5.calculateMD5(get_dest_db(context));
        editor.putString(DB_MD5_PATH, new_MD5);
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
