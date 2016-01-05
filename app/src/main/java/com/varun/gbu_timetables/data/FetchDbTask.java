package com.varun.gbu_timetables.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.varun.gbu_timetables.MD5;
import com.varun.gbu_timetables.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class FetchDbTask extends AsyncTask<Void,String,Integer>{

    private final Context mContext;

    public FetchDbTask(Context context) {
        mContext = context;
    }

    @Override
    protected Integer doInBackground(Void... params)
    {
        String url_str = "http://gbuonline.in/timetable_md5/md5.php";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String server_str = null;
        String server_md5;
        publishProgress("Checking for timetable updates");
        try {
            URL url = new URL(url_str);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            Log.d("got response",Integer.toString(status));

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null)
            {
                publishProgress("An error occurred");
                return -1;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return -1;
            }
            server_str = buffer.toString();
            JSONObject jsonObject = new JSONObject(server_str);
            server_md5 = jsonObject.getString("md5_hash");
            Log.d("got hash", server_md5);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String db_md5 = preferences.getString(TimetableDbHelper.DB_MD5_PATH,null);
            Log.d("update status",Boolean.toString(server_md5.equals(db_md5)));

            if(!server_md5.equals(db_md5))
            {
                publishProgress("Newer timetables found, downloading");
                Log.d("updater","downloading file");
                URL download_url = new URL("http://gbuonline.in/timetable/varun.db");
                URLConnection dl_url_connection = (HttpURLConnection) download_url.openConnection();
                dl_url_connection.connect();
                String downloaded_file_path = "download.db";
                FileOutputStream saved = mContext.openFileOutput(downloaded_file_path, Context.MODE_PRIVATE);

                publishProgress("Application will restart after update");
                InputStream download_stream = dl_url_connection.getInputStream();
                if(dl_url_connection == null)
                    return -1;

                byte mbuffer[] = new byte[1024];

                int length;
                while ((length = download_stream.read(mbuffer)) > 0) {
                    saved.write(mbuffer, 0, length);
                }
                Log.d("updater", "upgrading db");

                TimetableDbHelper timetableDbHelper = new TimetableDbHelper(mContext);
                timetableDbHelper.copy_db(mContext, 1, downloaded_file_path);
                Log.d("updater", "db updated");
                File f = new File(downloaded_file_path);
                f.delete();
                boolean new_update_status = MD5.checkMD5(server_md5,TimetableDbHelper.get_dest_db(mContext));
                Log.d("new update status", Boolean.toString(new_update_status));
                publishProgress("Timetables Updates Successfully!");
                return 1;
            }
            else
            {
                publishProgress("Already upto date!");
                return 0;
            }
        }
        catch (Exception e)
        {
            publishProgress("An error occurred!");
            Log.d("error",e.toString());
        }
        return 0;
    }

    @Override
    protected void onProgressUpdate(String... params)
    {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext,params[0],duration);
        toast.show();
    }

    @Override
    protected void onPostExecute(Integer status)
    {
        if(status == 1)
        {
            Intent mStartActivity = new Intent(mContext, MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        }
    }
}
