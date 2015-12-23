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
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.GridLayout.LayoutParams;

import java.util.ArrayList;

import varun.timetables_sql.R;
import varun.timetables_sql.data.TimetableContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimetableActivityFragment extends Fragment {

    static String LOG_TAG = "TimeTableActivityFragment";

    public TimetableActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        ArrayList<Integer> days = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            days.add(i);
        }

        ListView lv = (ListView) rootView.findViewById(R.id.timetable_row_item);
        DayAdapter dayAdapter = new DayAdapter(getContext(),days,19);
        lv.setAdapter(dayAdapter);

        return rootView;
    }
}
