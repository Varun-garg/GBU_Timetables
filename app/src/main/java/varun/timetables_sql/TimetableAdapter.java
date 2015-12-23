package varun.timetables_sql;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import varun.timetables_sql.data.TimetableContract;

/**
 * Created by varun on 12/23/15.
 */
public class TimetableAdapter extends ArrayAdapter<Integer> {

    int Day_no;
    int Section;
    Context context;
    public TimetableAdapter(Context context, ArrayList<Integer> Periods,int Section,int Day_no) {
        super(context, 0, Periods);
        this.Day_no = Day_no;
        this.Section = Section;
        this.context = context;
        Log.d("periods ",Periods.toString());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int Period_no = getItem(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_item_single, parent, false);
        }
        Log.d("section day period",Integer.toString(Section) +" "+ Integer.toString(Day_no) +" " + Integer.toString(Period_no) +" ");
        Uri uri = TimetableContract.BuildTTCellWithSectionDaySlot(Section, Day_no, Period_no);
        Log.d("uri ",uri.toString());

        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);

        TextView textView = (TextView) convertView.findViewById(R.id.timetable_item_text);

        String time_string = "";
        while ( cursor.moveToNext())
        {
            Log.d("varun.timetables_sql",cursor.getString(cursor.getColumnIndex("CSF_Id")));

            Uri fac_uri = TimetableContract.BuildFacultyWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
            Cursor fac_cursor = context.getContentResolver().query(fac_uri, null, null, null, null);
            Log.d("fac_cursor ", DatabaseUtils.dumpCursorToString(fac_cursor));

            Uri sec_uri = TimetableContract.BuildSectionWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
            Cursor sec_cursor = context.getContentResolver().query(sec_uri, null, null, null, null);
            Log.d("sec_cursor ", DatabaseUtils.dumpCursorToString(sec_cursor));

            Uri room_uri = TimetableContract.BuildRoomWithId(cursor.getLong(cursor.getColumnIndex("Room_Id")));
            Cursor room_cursor = context.getContentResolver().query(room_uri, null, null, null, null);
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
 //       textView.setText(time_string);
        textView.setText(Integer.toString(Section) +" "+ Integer.toString(Day_no) +" " + Integer.toString(Period_no) +" ");
        return convertView;
    }
}
