package varun.timetables_sql;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
    HorizontalScrollView horizontalScrollView;

    public TimetableActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        final ArrayList<Integer> days = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            days.add(i);
        }

        final ListView lv = (ListView) rootView.findViewById(R.id.timetable_row_item);
        final DayAdapter dayAdapter = new DayAdapter(getContext(), days, 19);
        lv.setAdapter(dayAdapter);

        dayAdapter.setRecycleListener(new DayAdapter.RecycleListener() {
            @Override
            public void onScroll(int scroll_x,int adapter_position)
            {

                 for(int i = 0;i<days.size();i++)
                {

                    if( adapter_position != i) {
                        View view = lv.getChildAt(i);
                        if(view == null) continue;
                        RecyclerView r = (RecyclerView) view.findViewById(R.id.timetable_row_recycler);
                        LinearLayoutManager  layoutManager =  ( LinearLayoutManager) r.getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(0,0-scroll_x);
                    }
                }

            }
        });
        return rootView;
    }

}
