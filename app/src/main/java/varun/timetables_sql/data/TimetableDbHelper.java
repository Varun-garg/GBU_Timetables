package varun.timetables_sql.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * varun.timetables_sql.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 12/12/2015 10:11 PM.
 */
public class TimetableDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "varun.db";
    static final String LOG_TAG = "TimetableDbHelper";


    public TimetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);


        /* Code for listing files in assets folder, varun.db should be present here
        String files[] = context.getAssets().list("");
        for(int i = 0; i<files.length;i++)
            Log.d(LOG_TAG," File found " + files[i]);
        */

        //list tables in our db
        Cursor c = this.getWritableDatabase().rawQuery("SELECT name from sqlite_master where type = 'table'", null);
        ArrayList<String> list = new ArrayList<String>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                //   Log.d(LOG_TAG,"Table Name => "+c.getString(0));
                list.add(c.getString(0));
                c.moveToNext();
            }
        }

        if (list.size() <= 1) // only sql_master or empty db
        {
            copy_db(context);
        }
    }

    private void copy_db(Context context){

        Log.d(LOG_TAG, "Ok, we are gonna copy some data");
        String dest_db = context.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
        Log.d(LOG_TAG, "output file location " + dest_db);

        context.deleteDatabase(DATABASE_NAME); //Delete existing db
        try {
            File f = new File(dest_db);
            if (!f.exists()) {
                Log.d(LOG_TAG, "File Not Created");
            }

            InputStream db_stream = context.getAssets().open("varun.db");
            OutputStream dest_db_stream = new FileOutputStream(dest_db);

            byte buffer[] = new byte[1024];

            int length;
            while ((length = db_stream.read(buffer)) > 0) {
                dest_db_stream.write(buffer, 0, length);
            }


            db_stream.close();
            dest_db_stream.close();

            /* Read new file for debugging only
            Log.d(LOG_TAG,"Now We we read new file");
            InputStream db_stream_2 = new FileInputStream(dest_db);

            InputStreamReader is = new InputStreamReader(db_stream_2);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while (read!=null)
            {
                sb.append(read);
                read = br.readLine();
            }

            Log.d(LOG_TAG,"New Database = "+ sb.toString());
            db_stream_2.close();
            */
        } catch (IOException e) {
            Log.d(LOG_TAG, "Caught IO Exception " + e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

}
