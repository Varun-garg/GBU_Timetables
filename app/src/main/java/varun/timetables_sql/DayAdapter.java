package varun.timetables_sql;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import varun.timetables_sql.data.CSF;
import varun.timetables_sql.data.Key;
import varun.timetables_sql.data.TimetableContract;

/**
 * Created by varun on 12/23/15.
 */
public class DayAdapter extends ArrayAdapter<Integer> {

    Context context;
    int Section;
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    int current_scroll_pos = 0;

    HashMap<Long, CSF> CSF_Details = new HashMap();

    ArrayList<Integer> periods = new ArrayList<>();
    int max_lines[];

    HashMap<Key, String> cache = new HashMap();
    private RecycleListener listener;


    public DayAdapter(Context context, ArrayList<Integer> Days, int Section) {
        super(context, 0, Days);
        this.listener = null;

        this.Section = Section;
        this.context = context;
        for (int i = 1; i <= 9; i++) {
            periods.add(i);
        }
        max_lines = new int[Days.size()];
        Arrays.fill(max_lines, 2);

        for (int i = 0; i < Days.size(); i++)
            for (int j = 0; j < periods.size(); j++)
                BuildTimeString(i, j);
    }

    public HashMap<Long, CSF> getCSFDetails() {
        return CSF_Details;
    }

    public void setRecycleListener(RecycleListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int day_no = getItem(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_row, parent, false);
        }


        TextView header_tv = (TextView) convertView.findViewById(R.id.timetable_row_header);
        header_tv.setText(days[position]);

        final RecyclerView mRecyclerView = (RecyclerView) convertView.findViewById(R.id.timetable_row_recycler);

        final RecyclerView.LayoutManager layoutManager = new CustomLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0, 0 - current_scroll_pos);

        TimetableAdapter timetableAdapter = new TimetableAdapter(context, periods, Section, position, cache, max_lines[position]);
        mRecyclerView.setAdapter(timetableAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                current_scroll_pos = recyclerView.computeHorizontalScrollOffset();
                listener.onScroll(recyclerView.computeHorizontalScrollOffset(), position);

            }
        });
        return convertView;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    public void BuildTimeString(int Day_Pos, int Period_Pos) {

        int Period_no = periods.get(Period_Pos);
        int Day_no = getItem(Day_Pos);

        Uri uri = TimetableContract.BuildTTCellWithSectionDaySlot(Section, Day_no, Period_no);

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        String time_string = "";
        int lines = 0;
        while (cursor.moveToNext()) {
            Long CSF_Id = cursor.getLong(cursor.getColumnIndex("CSF_Id"));
            Long Room_Id = cursor.getLong(cursor.getColumnIndex("Room_Id"));

            CSF mCSF = CSF_Details.get(CSF_Id);
            if (mCSF == null) {
                mCSF = new CSF(CSF_Id, Room_Id, context);
                mCSF.CSF_Id = CSF_Id;
                CSF_Details.put(mCSF.CSF_Id, mCSF);
            }

            time_string += mCSF.Sub_Code + "\n";
            time_string += "(" + mCSF.Fac_name + ") ";
            time_string += mCSF.Room_no + "\n";

            lines += 2;
        }
        cursor.close();
        cache.put(new Key(Day_Pos, Period_Pos), time_string);
        if (lines > max_lines[Day_Pos]) max_lines[Day_Pos] = lines;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public interface RecycleListener {

        public void onScroll(int scroll_x, int adapter_position);
    }
}
