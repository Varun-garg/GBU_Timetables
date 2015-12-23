package varun.timetables_sql;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IntegerRes;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import varun.timetables_sql.R;

/**
 * Created by varun on 12/23/15.
 */
public class DayAdapter extends ArrayAdapter<Integer> {

    int Section;
    Context context;

    public DayAdapter(Context context, ArrayList<Integer> Days,int Section) {
        super(context, 0, Days);
        this.Section = Section;
        this.context = context;
        Log.d("days ",Days.toString());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
            int day_no = getItem(position);
            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_row, parent, false);
            }
            ArrayList<Integer> periods = new ArrayList<>();
            for (int i = 1; i <= 9; i++) {
                periods.add(i);
            }

            RecyclerView mRecyclerView = (RecyclerView) convertView.findViewById(R.id.timetable_row_recycler);

        //    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManager = new CustomLinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);

            mRecyclerView.setLayoutManager(layoutManager);

            TimetableAdapter timetableAdapter = new TimetableAdapter(context,periods,Section,day_no);
            mRecyclerView.setAdapter(timetableAdapter);

        return convertView;
     }
}
