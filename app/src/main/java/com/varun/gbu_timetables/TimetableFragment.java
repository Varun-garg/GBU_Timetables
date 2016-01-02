package com.varun.gbu_timetables;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class TimetableFragment extends Fragment {

    static String LOG_TAG = "TimeTableActivityFragment";
    HorizontalScrollView horizontalScrollView;
    String[] day_names = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    String title;
    String type;
    public TimetableFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        title = getActivity().getIntent().getExtras().getString("Timetable_title");
        getActivity().setTitle(title);

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


        type = getActivity().getIntent().getExtras().getString("Type");
        Long id = null;
        if(type.equals("Section"))
        {
            id = getActivity().getIntent().getExtras().getLong("Section_id");
        }
        else if (type.equals("Faculty"))
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

        final TimetableAdapter timetableAdapter = new TimetableAdapter(getContext(), days, id,type,periods,title);
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
        final DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), CSF_Array,type);
        lv2.setAdapter(detailsAdapter);

        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                CSF csf = (CSF) view.getTag();

                Intent intent = new Intent(getActivity(),TimetableActivity.class);

                if(type.equals("Faculty"))
                {
                    intent.putExtra("Section_id",csf.Section_id);
                    intent.putExtra("Timetable_title",csf.Section_name);
                    intent.putExtra("Type","Section");
                }
                else  if(type.equals("Section"))
                {
                    intent.putExtra("Faculty_id",csf.Fac_id);
                    intent.putExtra("Timetable_title",csf.Fac_name);
                    intent.putExtra("Type","Faculty");
                }

                startActivity(intent);

            }
        });

        return rootView;
    }

}
