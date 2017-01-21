package com.varun.gbu_timetables.asyncTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.widget.Toast;

import com.varun.gbu_timetables.MainActivity;
import com.varun.gbu_timetables.R;
import com.varun.gbu_timetables.data.database.TimetableContract;
import com.varun.gbu_timetables.data.database.TimetableDbHelper;
import com.varun.gbu_timetables.data.database.TimetableProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UpdateDatabaseOnlineTask extends AsyncTask<Void, String, Integer> {

    private final Context mContext;
    private boolean silent = false;
    String checksumUrlLocation = "http://gbuonline.in/timetable_md5/md5.php";
    String downloadUrlLocation = "http://gbuonline.in/timetable/varun.db";

    public UpdateDatabaseOnlineTask(Context context, boolean silent) {
        mContext = context;
        this.silent = silent;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String server_str;
        String server_md5;
        publishProgress("Checking for timetable updates");
        try {
            URL checksumUrl = new URL(checksumUrlLocation);
            urlConnection = (HttpURLConnection) checksumUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                publishProgress("Update Failed: An internet error occurred");
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String db_md5 = preferences.getString(TimetableDbHelper.DB_MD5_PATH, null);

            if (!server_md5.equals(db_md5)) {
                URL downloadUrl = new URL(downloadUrlLocation);
                publishProgress("Newer timetables found, downloading");
                URLConnection dl_url_connection = downloadUrl.openConnection();
                dl_url_connection.connect();
                String downloaded_file_path = "download.db";
                FileOutputStream saved = mContext.openFileOutput(downloaded_file_path, Context.MODE_PRIVATE);

                publishProgress("Application will restart after update");
                InputStream download_stream = dl_url_connection.getInputStream();
                if (dl_url_connection == null)
                    return -1;

                byte mbuffer[] = new byte[1024];

                int length;
                while ((length = download_stream.read(mbuffer)) > 0) {
                    saved.write(mbuffer, 0, length);
                }

                TimetableDbHelper timetableDbHelper = new TimetableDbHelper(mContext);
                timetableDbHelper.copy_db(mContext, 1, downloaded_file_path);
                File f = new File(downloaded_file_path);
                f.delete();

                /*Uri reloadDBUri = TimetableContract.RELOAD_DB_URI;
                mContext.getContentResolver().query(reloadDBUri,null,null,null,null);
                */

                ContentResolver resolver = mContext.getContentResolver(); //no need of non working uri method - better way
                ContentProviderClient client = resolver.acquireContentProviderClient(TimetableContract.CONTENT_AUTHORITY);
                TimetableProvider provider = (TimetableProvider) client.getLocalContentProvider();
                provider.reloadDb();
                client.release();

                publishProgress("Timetables Updated Successfully!");
                return 1;
            } else {
                publishProgress("Already upto date!");
                return 0;
            }
        } catch (Exception e) {
            publishProgress("Update Failed: An internet error occurred");
            Log.d("error", e.toString());
        }
        return 0;
    }

    @Override
    protected void onProgressUpdate(String... params) {
        if (silent) return;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, params[0], duration);
        toast.show();
    }

    @Override
    protected void onPostExecute(Integer status) {
        if (status == 1) {
            if (!silent) {
                /* Old code that restarted app
                Intent mStartActivity = new Intent(mContext, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
                */

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            } else {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(mContext)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("GBU Timetables")
                                .setContentText("Newer timetables have been downloaded");

                Intent resultIntent = new Intent(mContext, MainActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());

            }
        }
    }
}
