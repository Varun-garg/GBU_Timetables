package com.varun.gbu_timetables;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.varun.gbu_timetables.data.CSF;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimetableActivityFragment extends Fragment {

    static String LOG_TAG = "TimeTableActivityFragment";
    HorizontalScrollView horizontalScrollView;
    String[] day_names = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public TimetableActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(getActivity().getIntent().getExtras().getString("Timetable_title"));

        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        HashMap<Long, CSF> CSF_Details = new HashMap();

        final ArrayList<Integer> days = new ArrayList<>();
        ArrayList<Integer> periods = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            days.add(i);
        }
        for (int i = 1; i <= 9; i++) {
            periods.add(i);
        }


        final TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.timetable_table);


        String timetable_type = getActivity().getIntent().getExtras().getString("Type");
        Long id = null;
        if(timetable_type.equals("Section"))
        {
            id = getActivity().getIntent().getExtras().getLong("Section_id");
        }
        else if (timetable_type.equals("Faculty"))
        {
            id = getActivity().getIntent().getExtras().getLong("Faculty_id");
        }

        //We insert Periods
        TableRow header = (TableRow) inflater.inflate(R.layout.timetable_row,null);
        int beg_hr = 8;
        int beg_min = 30;
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(width, TableRow.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams halfparams = new TableRow.LayoutParams(width/2, TableRow.LayoutParams.MATCH_PARENT);


        TextView blank = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
        blank.setLayoutParams(halfparams);
        blank.setBackgroundResource(R.drawable.back);
        header.addView(blank);
        for(int i = 0; i <periods.size();i++)
        {
            TextView textView = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
            textView.setLayoutParams(cellParams);
            textView.setText(Integer.toString(beg_hr)+":"+ Integer.toString(beg_min) + " - ");
            beg_hr++;
            if(beg_hr == 13) beg_hr = 1;
            textView.append(Integer.toString(beg_hr)+":"+ Integer.toString(beg_min));
            textView.setBackgroundResource(R.drawable.back);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(5,5,0,0);
            header.addView(textView);
        }
        tableLayout.addView(header);

        final TimetableAdapter timetableAdapter = new TimetableAdapter(getContext(), days, id,timetable_type,periods);
        for(int i = 0;i<days.size();i++)
        { //we are only interested in position not what is inside days

            TableRow tableRow = (TableRow) inflater.inflate(R.layout.timetable_row,null);


            TextView textView = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
            textView.setLayoutParams(halfparams);
            textView.setText(day_names[i]);
            textView.setBackgroundResource(R.drawable.back);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(5,5,0,0);

            tableRow.addView(textView);
            String prev_time_string = "";
            int duplicates = 1;
            LinearLayout prev_item = null;
            for(int j = 0;j<periods.size();j++)
            {
                LinearLayout item = (LinearLayout) timetableAdapter.getView(i,j);
                String this_time_string = (String) item.getTag();

                if(prev_time_string.equals(this_time_string) && prev_time_string.length() > 0 && prev_item!=null)
                {
                    TableRow.LayoutParams old_params = (TableRow.LayoutParams) prev_item.getLayoutParams();
                    duplicates++;
                    old_params.span = duplicates;
                    prev_item.setLayoutParams(old_params);
                    continue;
                }
                duplicates = 1;
                tableRow.addView(item);
                prev_time_string = this_time_string;
                prev_item = item;
            }

            tableLayout.addView(tableRow);
        }


        CSF_Details = timetableAdapter.getCSFDetails();

        ArrayList<CSF> CSF_Array = new ArrayList<CSF>(CSF_Details.values());
        final ListView lv2 = (ListView) rootView.findViewById(R.id.timetable_faculty_data);
        final DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), CSF_Array);
        lv2.setAdapter(detailsAdapter);



        return rootView;
    }

}
