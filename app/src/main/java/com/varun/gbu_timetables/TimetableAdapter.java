package com.varun.gbu_timetables;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.varun.gbu_timetables.data.CSF;
import com.varun.gbu_timetables.data.CSF_FAC_KEY;
import com.varun.gbu_timetables.data.Key;
import com.varun.gbu_timetables.data.TimetableContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by varun on 12/23/15.
 */
public class TimetableAdapter {

    Context context;
    String timetable_type;
    Long timetable_id;
    ArrayList<Integer> day_nos;
    String title;
    HashMap<CSF_FAC_KEY, CSF> CSF_Details = new HashMap();

    ArrayList<Integer> periods;

    HashMap<Key, String> cache = new HashMap();
    HashMap<Key, HashSet> keymap = new HashMap<>();

    int back;
    int pink;
    int green;

    public TimetableAdapter(Context context, ArrayList<Integer> day_nos, Long timetable_id, String timetable_type, ArrayList<Integer> periods, String title) {
        this.title = title;
        this.day_nos = day_nos;
        this.timetable_type = timetable_type;
        this.timetable_id = timetable_id;
        this.context = context;
        this.periods = periods;

        back = Utility.getBackDrawable(context);
        pink = Utility.getPinkDrawable(context);
        green = Utility.getGreenDrawable(context);

        for (int i = 0; i < day_nos.size(); i++)
            for (int j = 0; j < periods.size(); j++)
                BuildTimeString(i, j);
    }

    public HashMap<CSF_FAC_KEY, CSF> getCSFDetails() {
        return CSF_Details;
    }

    public View getView(final int row_no, final int column_no) {

        LinearLayout linearLayout = new LinearLayout(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, context.getResources().getDisplayMetrics());
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(width, TableRow.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams itemParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        linearLayout.setLayoutParams(cellParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Key key = new Key(row_no, column_no);
        Set<CSF_FAC_KEY> current_csf_fac_key_list = keymap.get(key);
        String day_str = cache.get(key);
        if (day_str == null) day_str = "";
        String lines[] = day_str.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            TextView textView = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
            textView.setLayoutParams(itemParams);
            textView.setText(lines[i]);
            linearLayout.addView(textView);

            if (lines.length >= 2) {
                if (i % 2 == 0)
                    textView.setBackgroundResource(pink);
                else
                    textView.setBackgroundResource(green);
            }
            else
                textView.setBackgroundResource(back);
        }
        linearLayout.setTag(R.string.current_csf_fac_key_list, current_csf_fac_key_list);
        linearLayout.setTag(R.string.time_string, day_str);

        return linearLayout;
    }

    public void BuildTimeString(int Day_Pos, int Period_Pos) {

        int Period_no = periods.get(Period_Pos);
        int Day_no = day_nos.get(Day_Pos);
        final Key key = new Key(Day_Pos, Period_Pos);

        HashSet<CSF_FAC_KEY> current_key_list = keymap.get(key);
        if (current_key_list == null) {
            current_key_list = new HashSet<>();
            keymap.put(key, current_key_list);
        }

        Uri uri = null;
        if (timetable_type.equals("Section"))
            uri = TimetableContract.BuildTTCellWithSectionDaySlot(timetable_id, Day_no, Period_no);
        else if (timetable_type.equals("Faculty"))
            uri = TimetableContract.BuildTTCellWithFacultyDaySlot(timetable_id, Day_no, Period_no);

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        String time_string = "";

        while (cursor.moveToNext()) {
            time_string = time_string.trim();
            if (!time_string.equals("")) time_string += "\n";
            Long CSF_Id = cursor.getLong(cursor.getColumnIndex("CSF_Id"));
            Long Room_Id = cursor.getLong(cursor.getColumnIndex("Room_Id"));
            Long Batch_id = cursor.getLong(cursor.getColumnIndex("Batch_Id"));
            String ActivityTag = cursor.getString(cursor.getColumnIndex("ActivityTag"));
            Uri fac_uri = TimetableContract.BuildFacultyWithCSFid(CSF_Id);
            Cursor fac_cursor = context.getContentResolver().query(fac_uri, null, null, null, null);
            try {
                //room
                Uri room_uri = TimetableContract.BuildRoomWithId(Room_Id);
                Cursor room_cursor = context.getContentResolver().query(room_uri, null, null, null, null);
                room_cursor.moveToNext();
                String Room_no = room_cursor.getString(room_cursor.getColumnIndex("Name")).trim();
                room_cursor.close();


                //subject
                Uri sec_uri = TimetableContract.BuildSubjectWithCSFid(CSF_Id);
                Cursor sec_cursor = context.getContentResolver().query(sec_uri, null, null, null, null);
                sec_cursor.moveToNext();
                String Sub_Code = sec_cursor.getString(sec_cursor.getColumnIndex("code")).trim();
                String Sub_name = sec_cursor.getString(sec_cursor.getColumnIndex("name")).trim();
                sec_cursor.close();

                ArrayList<CSF> myArr = new ArrayList<>();
                while (fac_cursor.moveToNext()) {
                    Long Fac_id = fac_cursor.getLong(fac_cursor.getColumnIndex("faculty_id"));
                    CSF_FAC_KEY csf_fac_key = new CSF_FAC_KEY(CSF_Id, Fac_id);

                    CSF mCSF = CSF_Details.get(csf_fac_key);
                    if (mCSF == null) {
                        mCSF = new CSF(CSF_Id, context);
                        mCSF.CSF_Id = CSF_Id;
                        mCSF.Fac_abbr = fac_cursor.getString(fac_cursor.getColumnIndex("abbr")).trim();
                        mCSF.Fac_name = fac_cursor.getString(fac_cursor.getColumnIndex("name")).trim();
                        mCSF.Fac_name = fac_cursor.getString(fac_cursor.getColumnIndex("name")).trim();
                        mCSF.Fac_id = fac_cursor.getLong(fac_cursor.getColumnIndex("faculty_id"));
                        mCSF.Sub_Code = Sub_Code;
                        mCSF.Sub_name = Sub_name;

                        if (timetable_type.equals("Section")) {
                            mCSF.Section_id = timetable_id;
                            mCSF.Section_name = title;
                        } else if (timetable_type.equals("Faculty")) {
                            mCSF.Section_id = cursor.getLong(cursor.getColumnIndex("Section_Id"));
                            Uri section_uri = TimetableContract.BuildSectionWithId(mCSF.Section_id);
                            Cursor section_cursor = context.getContentResolver().query(section_uri, null, null, null, null);
                            section_cursor.moveToNext();
                            mCSF.Section_name = section_cursor.getString(section_cursor.getColumnIndex("Name")).trim();
                            section_cursor.close();
                        }

                        CSF_Details.put(csf_fac_key, mCSF);
                    }
                    myArr.add(mCSF);
                    current_key_list.add(csf_fac_key);
                }
                fac_cursor.close();

                CSF mCSF = myArr.get(0);
                time_string += mCSF.Sub_Code;

                if (timetable_type.equals("Section")) {
                    time_string += " (";
                    for (int i = 0; i < myArr.size(); i++) {
                        CSF csf = myArr.get(i);
                        if (i != 0)
                            time_string += ", ";
                        time_string += csf.Fac_abbr;
                    }
                    time_string += ") ";
                }

                if (timetable_type.equals("Faculty"))
                    time_string += "(" + mCSF.Section_name + ") ";

                time_string += Room_no;

                if (Batch_id != 0)
                    time_string += " G" + Batch_id.toString();

                if (ActivityTag.equalsIgnoreCase("lab"))
                    time_string += " LAB";

            } catch (Exception e) {
                Log.d("day_no",Integer.toString(Day_no));
                Log.d("period_no",Integer.toString(Period_no));
                Log.d("TimetableAdapter", "caught error in CSF_id" + CSF_Id.toString());
                Log.d("TimetableAdapter", e.toString());
            }
        }
        cursor.close();
        cache.put(key, time_string.trim());
    }

}
