package varun.timetables_sql;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import varun.timetables_sql.data.TimetableContract;
import varun.timetables_sql.data.TimetableDbHelper;

public class MainActivity extends AppCompatActivity {
    public static String PACKAGE_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        int section = 19;
        for(int day = 0; day<7;day++)
        {
            for(int slot = 0;slot<9;slot++)
            {
                Uri uri = TimetableContract.BuildTTCellWithSectionDaySlot(section, day, slot);
                Log.d("uri ",uri.toString());

                Cursor cursor = getContentResolver().query(uri,null,null,null,null);
                while ( cursor.moveToNext())
                {
                    Log.d("varun.timetables_sql",cursor.getString(cursor.getColumnIndex("CSF_Id")));

                    Uri fac_uri = TimetableContract.BuildFacultyWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
                    Cursor fac_cursor = getContentResolver().query(fac_uri, null, null, null, null);
                    Log.d("fac_cursor ", DatabaseUtils.dumpCursorToString(fac_cursor));

                    Uri sec_uri = TimetableContract.BuildSectionWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
                    Cursor sec_cursor = getContentResolver().query(sec_uri, null, null, null, null);
                    Log.d("sec_cursor ", DatabaseUtils.dumpCursorToString(sec_cursor));

                    Log.d("varun.timetables_sql",cursor.getString(cursor.getColumnIndex("Room_Id")));

                    Uri room_uri = TimetableContract.BuildRoomWithId(cursor.getLong(cursor.getColumnIndex("Room_Id")));
                    Cursor room_cursor = getContentResolver().query(room_uri, null, null, null, null);
                    Log.d("room_cursor ", DatabaseUtils.dumpCursorToString(room_cursor));

                    fac_cursor.close();
                    sec_cursor.close();
                    room_cursor.close();

                }
                cursor.close();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
