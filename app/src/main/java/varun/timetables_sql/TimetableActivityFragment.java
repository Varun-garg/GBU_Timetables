package varun.timetables_sql;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.GridLayout.LayoutParams;
import varun.timetables_sql.R;
import varun.timetables_sql.data.TimetableContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimetableActivityFragment extends Fragment {
    GridLayout gl;
    TextView[] text;
    static String LOG_TAG = "TimeTableActivityFragment";

    public TimetableActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.timetable_grid, container, false);


        gl = (GridLayout) rootView.findViewById(R.id.timetable_grid);
        gl.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        gl.setOrientation(0);
        gl.setColumnCount(9);
       // gl.setRowCount(3);

        text = new TextView[9];
/*
        for(int i=0;i<9;i++)
        {
            text[i] = new TextView(getContext());
     //       text[i].setLayoutParams(new LayoutParams
       //             (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            text[i].setText(String.valueOf(i));
            text[i].setTextSize(25);
            text[i].setPadding(50, 25, 10, 25);
            gl.addView(text[i]);
        }
        */

        int section = 19;
        for(int day = 0; day<7;day++)
        {
            for(int slot = 0;slot<9;slot++)
            {
                Uri uri = TimetableContract.BuildTTCellWithSectionDaySlot(section, day, slot);
                Log.d("uri ",uri.toString());


                Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
                TextView textView = new TextView(getContext());

                String time_string = "";
                while ( cursor.moveToNext())
                {
                    Log.d("varun.timetables_sql",cursor.getString(cursor.getColumnIndex("CSF_Id")));

                    Uri fac_uri = TimetableContract.BuildFacultyWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
                    Cursor fac_cursor = getActivity().getContentResolver().query(fac_uri, null, null, null, null);
                    Log.d("fac_cursor ", DatabaseUtils.dumpCursorToString(fac_cursor));

                    Uri sec_uri = TimetableContract.BuildSectionWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
                    Cursor sec_cursor = getActivity().getContentResolver().query(sec_uri, null, null, null, null);
                    Log.d("sec_cursor ", DatabaseUtils.dumpCursorToString(sec_cursor));

                    Uri room_uri = TimetableContract.BuildRoomWithId(cursor.getLong(cursor.getColumnIndex("Room_Id")));
                    Cursor room_cursor = getActivity().getContentResolver().query(room_uri, null, null, null, null);
                    Log.d("room_cursor ", DatabaseUtils.dumpCursorToString(room_cursor));

                    sec_cursor.moveToNext();
                    room_cursor.moveToNext();
                    fac_cursor.moveToNext();
                    time_string += sec_cursor.getString(sec_cursor.getColumnIndex("Subject_Code")) + " ";
                    time_string += "(" + fac_cursor.getString(fac_cursor.getColumnIndex("abbr")) + ") ";
                    time_string += room_cursor.getString(room_cursor.getColumnIndex("Name"))+ " ";

                    Log.d("time_string",time_string);
                    fac_cursor.close();
                    sec_cursor.close();
                    room_cursor.close();

                }
                textView.setText(time_string);
                gl.addView(textView);
                cursor.close();
            }
        }




        return rootView;
    }
}
