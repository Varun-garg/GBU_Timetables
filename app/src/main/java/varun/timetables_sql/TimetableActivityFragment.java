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
            int currently_working = 0;
            @Override
            public void onScroll(int scroll_x,int adapter_position)
            {
                if(currently_working == 1) return;
                currently_working = 1;
                Log.d("Listener","onScroll, scroll_x = "+Integer.toString(scroll_x) + ", adapter_position = " + Integer.toString(adapter_position));
                dayAdapter.scroll_position = scroll_x;
                dayAdapter.scroll_override = 1;
                dayAdapter.ignore_position = adapter_position;

                /*
                for(int i = 0;i<days.size();i++)
                {
                    View view = lv.getChildAt(i);

                    if(view != null && adapter_position != i) {
                        Log.d("Listener","Now updating scroll position of element position "+Integer.toString(i));
                        RecyclerView r = (RecyclerView) view.getTag();
                        RecyclerView.LayoutManager layoutManager = r.getLayoutManager();
                        ( (LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0,scroll_x);
                    }
                }
                */

                dayAdapter.notifyDataSetChanged();
                dayAdapter.scroll_override = 0;
                dayAdapter.ignore_position = -1;
                currently_working = 0;
            }
        });
        lv.setOnTouchListener(new ListView.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    Log.d("anyway","on touch listner is working fine");
                }
                return true;
            }
        });
        return rootView;
    }

}
