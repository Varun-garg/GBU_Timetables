package com.varun.gbu_timetables;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.varun.gbu_timetables.R;

import com.varun.gbu_timetables.data.CSF;
import com.varun.gbu_timetables.data.Key;
import com.varun.gbu_timetables.data.TimetableContract;

/**
 * Created by varun on 12/23/15.
 */
public class TimetableAdapter {

    Context context;
    String timetable_type;
    Long timetable_id;
    ArrayList<Integer> day_nos;
    int current_scroll_pos = 0;

    HashMap<Long, CSF> CSF_Details = new HashMap();

    ArrayList<Integer> periods;

    HashMap<Key, String> cache = new HashMap();

    public TimetableAdapter(Context context, ArrayList<Integer> day_nos, Long timetable_id, String timetable_type, ArrayList<Integer> periods) {
        this.day_nos = day_nos;
        this.timetable_type = timetable_type;
        this.timetable_id = timetable_id;
        this.context = context;
        this.periods = periods;

        for (int i = 0; i < day_nos.size(); i++)
            for (int j = 0; j < periods.size(); j++)
                BuildTimeString(i, j);
    }

    public HashMap<Long, CSF> getCSFDetails() {
        return CSF_Details;
    }
    public View getView(final int row_no, final int column_no) {

        LinearLayout linearLayout = new LinearLayout(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, context.getResources().getDisplayMetrics());
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(width, TableRow.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams itemParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);

        linearLayout.setLayoutParams(cellParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Key key = new Key(row_no,column_no);
        String day_str = cache.get(key);
        String lines[] = day_str.split("\\r?\\n");

        for(int i = 0;i<lines.length;i++) {
            TextView textView = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
            textView.setLayoutParams(itemParams);
            textView.setText(lines[i]);
            linearLayout.addView(textView);

            if(lines.length >=2) {
                if(i%2 == 0)
                    textView.setBackgroundResource(R.drawable.pink);
                else
                    textView.setBackgroundResource(R.drawable.green);
            }
            else
                textView.setBackgroundResource(R.drawable.back);
        }
        linearLayout.setTag(day_str);
        return linearLayout;
    }

    public void BuildTimeString(int Day_Pos, int Period_Pos) {

        int Period_no = periods.get(Period_Pos);
        int Day_no = day_nos.get(Day_Pos);

        Uri uri = null;
        if(timetable_type.equals("Section"))
            uri = TimetableContract.BuildTTCellWithSectionDaySlot(timetable_id, Day_no, Period_no);
        else if (timetable_type.equals("Faculty"))
            uri = TimetableContract.BuildTTCellWithFacultyDaySlot(timetable_id,Day_no,Period_no);

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        String time_string = "";
        int lines = 0;
        while (cursor.moveToNext()) {
            if(!time_string.equals("")) time_string += "\n";
            Long CSF_Id = cursor.getLong(cursor.getColumnIndex("CSF_Id"));
            Long Room_Id = cursor.getLong(cursor.getColumnIndex("Room_Id"));

            try {
                CSF mCSF = CSF_Details.get(CSF_Id);
                if (mCSF == null) {
                    mCSF = new CSF(CSF_Id, context);
                    mCSF.CSF_Id = CSF_Id;
                    CSF_Details.put(mCSF.CSF_Id, mCSF);
                }

                Uri room_uri = TimetableContract.BuildRoomWithId(Room_Id);
                Cursor room_cursor = context.getContentResolver().query(room_uri, null, null, null, null);
                room_cursor.moveToNext();
                String Room_no = room_cursor.getString(room_cursor.getColumnIndex("Name")).trim();
                room_cursor.close();


                time_string += mCSF.Sub_Code + " ";
                time_string += "(" + mCSF.Fac_abbr + ") ";
                time_string += Room_no;
                lines += 2;
            }
            catch (Exception e)
            {
                Log.d("TimetableAdapter","caught error in CSF_id" + CSF_Id.toString());
                Log.d("TimetableAdapter",e.toString());
            }
        }
        cursor.close();
        cache.put(new Key(Day_Pos, Period_Pos), time_string);
    }

}
