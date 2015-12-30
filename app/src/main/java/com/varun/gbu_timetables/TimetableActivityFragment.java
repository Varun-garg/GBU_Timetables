package com.varun.gbu_timetables;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

import com.varun.gbu_timetables.data.CSF;

import java.util.ArrayList;
import java.util.HashMap;

import com.varun.gbu_timetables.R;

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

        Long Section_id = getActivity().getIntent().getExtras().getLong("Section_id");
        getActivity().setTitle(getActivity().getIntent().getExtras().getString("Section_Name"));

        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        HashMap<Long, CSF> CSF_Details = new HashMap();

        final ArrayList<Integer> days = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            days.add(i);
        }

        final ListView lv = (ListView) rootView.findViewById(R.id.timetable_row_item);
        final DayAdapter dayAdapter = new DayAdapter(getContext(), days, Section_id);
        lv.setAdapter(dayAdapter);

        CSF_Details = dayAdapter.getCSFDetails();

        ArrayList<CSF> CSF_Array = new ArrayList<CSF>(CSF_Details.values());
        final ListView lv2 = (ListView) rootView.findViewById(R.id.timetable_faculty_data);
        final DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), CSF_Array);
        lv2.setAdapter(detailsAdapter);


        dayAdapter.setRecycleListener(new DayAdapter.RecycleListener() {
            int busy = 0;

            @Override
            public void onScroll(int scroll_x, int adapter_position) {
                if (busy == 1) return;
                busy = 1;
                for (int i = 0; i < days.size(); i++) {
                    View view = lv.getChildAt(i);
                    if (view == null) continue;
                    RecyclerView r = (RecyclerView) view.findViewById(R.id.timetable_row_recycler);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) r.getLayoutManager();
                    layoutManager.scrollToPositionWithOffset(0, 0 - scroll_x);
                }
                busy = 0;
            }
        });
        return rootView;
    }

}
