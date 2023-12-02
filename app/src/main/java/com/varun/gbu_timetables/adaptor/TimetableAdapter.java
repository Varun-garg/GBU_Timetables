package com.varun.gbu_timetables.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.varun.gbu_timetables.R;
import com.varun.gbu_timetables.Utility;
import com.varun.gbu_timetables.data.database.TimetableContract;
import com.varun.gbu_timetables.data.model.CSF;
import com.varun.gbu_timetables.data.model.CSF_FAC_MAP_KEY;
import com.varun.gbu_timetables.data.model.PairKey;

import java.io.PrintWriter;
import java.io.StringWriter;
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
    HashMap<CSF_FAC_MAP_KEY, CSF> CSF_Details = new HashMap();
    HashMap<PairKey, String> cache = new HashMap();
    HashMap<PairKey, HashSet> keymap = new HashMap<>();
    int BgBoxDefault;
    int BgBoxPink;
    int BgBoxGreen;
    long max_period = 0;
    long min_period = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("Range")
    public TimetableAdapter(Context context, ArrayList<Integer> day_nos, Long timetable_id, String timetable_type, String title) {
        this.title = title;
        this.day_nos = day_nos;
        this.timetable_type = timetable_type;
        this.timetable_id = timetable_id;
        this.context = context;

        //some analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "TimetableOpen");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
        bundle.putString("Title", title);
        bundle.putString("Timetable_Id", timetable_id.toString());
        bundle.putString("Timetable_Type", timetable_type.replaceAll(" ", "_"));
        mFirebaseAnalytics.logEvent("TimetableOpen", bundle);

        Uri maxMinUri;
        if (this.timetable_type.equals(TimetableContract.PATH_SECTION)) {
            maxMinUri = TimetableContract.BuildMaxPeriodBySection(this.timetable_id);
        } else {
            maxMinUri = TimetableContract.BuildMaxPeriodByFaculty(this.timetable_id);
        }
        //  Log.d("max_uri",max_uri.toString());
        Cursor max_c = context.getContentResolver().query(maxMinUri, null, null, null, null);
        max_c.moveToNext();
        max_period = max_c.getLong(max_c.getColumnIndex("max(TT_Period)"));
        min_period = max_c.getLong(max_c.getColumnIndex("min(TT_Period)"));
        max_c.close();

        BgBoxDefault = Utility.ThemeTools.BackgroundIcons.getBgBoxDefaultDrawable(context);
        BgBoxPink = Utility.ThemeTools.BackgroundIcons.getBgBoxPinkDrawable(context);
        BgBoxGreen = Utility.ThemeTools.BackgroundIcons.getBgBoxGreenDrawable(context);

        for (int i = 0; i < day_nos.size(); i++)
            for (int j = (int) min_period; j <= max_period; j++)
                BuildTimeString(i, j);
    }

    public HashMap<CSF_FAC_MAP_KEY, CSF> getCSFDetails() {
        return CSF_Details;
    }

    public long getMaxPeriods() {
        return max_period;
    }


    public long getMinPeriods() {
        return min_period;
    }

    public View getView(final int row_no, final int period_no) {

        LinearLayout linearLayout = new LinearLayout(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, context.getResources().getDisplayMetrics());
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(width, TableRow.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams itemParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        linearLayout.setLayoutParams(cellParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        PairKey key = new PairKey(row_no, period_no);
        Set<CSF_FAC_MAP_KEY> current_csf_fac_key_list = keymap.get(key);
        String day_str = cache.get(key);
        if (day_str == null) day_str = "";
        String[] lines = day_str.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            TextView textView = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
            textView.setLayoutParams(itemParams);
            textView.setText(lines[i]);
            linearLayout.addView(textView);

            if (lines.length >= 2) {
                if (i % 2 == 0)
                    textView.setBackgroundResource(BgBoxPink);
                else
                    textView.setBackgroundResource(BgBoxGreen);
            } else
                textView.setBackgroundResource(BgBoxDefault);
        }
        linearLayout.setTag(R.string.current_csf_fac_key_list, current_csf_fac_key_list);
        linearLayout.setTag(R.string.time_string, day_str);

        return linearLayout;
    }

    @SuppressLint("Range")
    public void BuildTimeString(int Day_Pos, int Period_Pos) {

        int Day_no = day_nos.get(Day_Pos);
        final PairKey key = new PairKey(Day_Pos, Period_Pos);

        HashSet<CSF_FAC_MAP_KEY> current_key_list = keymap.get(key);
        if (current_key_list == null) {
            current_key_list = new HashSet<>();
            keymap.put(key, current_key_list);
        }

        Uri uri = null;
        if (timetable_type.equals("Section"))
            uri = TimetableContract.BuildTTCellWithSectionDaySlot(timetable_id, Day_no, Period_Pos);
        else if (timetable_type.equals("Faculty"))
            uri = TimetableContract.BuildTTCellWithFacultyDaySlot(timetable_id, Day_no, Period_Pos);

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        String time_string = "";

        while (cursor.moveToNext()) {
            time_string = time_string.trim();
            if (!time_string.equals("")) time_string += "\n";
            Long CSF_Id = cursor.getLong(cursor.getColumnIndex("CSF_Id"));
            @SuppressLint("Range") Long Room_Id = cursor.getLong(cursor.getColumnIndex("Room_Id"));
            @SuppressLint("Range") Long Batch_id = cursor.getLong(cursor.getColumnIndex("Batch_Id"));
            @SuppressLint("Range") String ActivityTag = cursor.getString(cursor.getColumnIndex("ActivityTag"));
            Uri fac_uri = TimetableContract.BuildFacultyWithCSFid(CSF_Id);
            Cursor fac_cursor = context.getContentResolver().query(fac_uri, null, null, null, null);
            try {
                //room
                Uri room_uri = TimetableContract.BuildRoomWithId(Room_Id);
                Cursor room_cursor = context.getContentResolver().query(room_uri, null, null, null, null);
                room_cursor.moveToNext();
                @SuppressLint("Range") String Room_no = room_cursor.getString(room_cursor.getColumnIndex("RoomName")).trim();
                room_cursor.close();


                //subject
                Uri sub_uri = TimetableContract.BuildSubjectWithCSFid(CSF_Id);
                Cursor sub_cursor = context.getContentResolver().query(sub_uri, null, null, null, null);
                sub_cursor.moveToNext();
                @SuppressLint("Range") String Sub_Code = sub_cursor.getString(sub_cursor.getColumnIndex("code")).trim();
                @SuppressLint("Range") String Sub_name = sub_cursor.getString(sub_cursor.getColumnIndex("name")).trim();
                sub_cursor.close();

                ArrayList<CSF> myArr = new ArrayList<>();
                while (fac_cursor.moveToNext()) {
                    @SuppressLint("Range") Long Fac_id = fac_cursor.getLong(fac_cursor.getColumnIndex("faculty_id"));
                    CSF_FAC_MAP_KEY csf_fac_key = new CSF_FAC_MAP_KEY(CSF_Id, Fac_id);

                    CSF mCSF = CSF_Details.get(csf_fac_key);
                    if (mCSF == null) {
                        mCSF = new CSF(CSF_Id, context);
                        mCSF.CSF_Id = CSF_Id;
                        mCSF.Fac_abbr = fac_cursor.getString(fac_cursor.getColumnIndex("abbr")).trim();
                        mCSF.Fac_name = fac_cursor.getString(fac_cursor.getColumnIndex("TeacherName")).trim();
                        mCSF.Fac_name = fac_cursor.getString(fac_cursor.getColumnIndex("TeacherName")).trim();
                        mCSF.Fac_id = fac_cursor.getLong(fac_cursor.getColumnIndex("faculty_id"));
                        mCSF.Sub_Code = Sub_Code;
                        mCSF.Sub_name = Sub_name;

                        if (mCSF.Fac_abbr.equals("SS"))
                            mCSF.Fac_abbr += " ADB";

                        if (timetable_type.equals(TimetableContract.PATH_SECTION)) {
                            mCSF.Section_id = timetable_id;
                            mCSF.Section_name = title;
                        } else if (timetable_type.equals(TimetableContract.PATH_FACULTY)) {
                            mCSF.Section_id = cursor.getLong(cursor.getColumnIndex("Section_Id"));
                            Uri section_uri = TimetableContract.BuildSectionWithId(mCSF.Section_id);
                            Cursor section_cursor = context.getContentResolver().query(section_uri, null, null, null, null);
                            section_cursor.moveToNext();
                            mCSF.Section_name = section_cursor.getString(section_cursor.getColumnIndex("SectionName")).trim();
                            mCSF.Section_name = Utility.getFullSectionName(mCSF.Section_name, context);
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
                    time_string += " G" + Batch_id;

                if (ActivityTag.equalsIgnoreCase("lab"))
                    time_string += " LAB";

            } catch (Exception e) {
                Log.d("TimetableAdapter", "day_no " + Day_no);
                Log.d("TimetableAdapter", "period_no " + Period_Pos);
                Log.d("TimetableAdapter", "CSF_id " + CSF_Id);
                Log.d("TimetableAdapter", e.toString(), e);
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "CSF ERROR");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
                bundle.putString("day_no", Integer.toString(Day_no));
                bundle.putString("period_no", Integer.toString(Period_Pos));
                bundle.putString("CSF_id", CSF_Id.toString());
                bundle.putString("exception", e.toString());

                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                bundle.putString("StackTrace", errors.toString());
                mFirebaseAnalytics.logEvent("Error", bundle);
            }
        }
        cursor.close();
        cache.put(key, time_string.trim());
        //    Log.d("string", Integer.toString(Day_no) + "," + Integer.toString(Period_Pos) + " " + time_string.trim());
    }

}
