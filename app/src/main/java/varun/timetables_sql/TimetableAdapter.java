package varun.timetables_sql;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import varun.timetables_sql.data.TimetableContract;

/**
 * Created by varun on 12/23/15.
 */
public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {


    int Day_no;
    int Section;
    Context context;
    ArrayList<Integer> Periods;
    HashMap cache = new HashMap();
    public TimetableAdapter(Context context, ArrayList<Integer> Periods, int Section, int Day_no) {
        this.Day_no = Day_no;
        this.Section = Section;
        this.context = context;
        this.Periods = Periods;

        for(int i = 0;i<this.Periods.size();i++)
            this.BuildTimeString(i);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TimetableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_item_single, parent, false);
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.linear_layout);

        ViewHolder vh = new ViewHolder(linearLayout);
        return vh;
    }

    public void BuildTimeString(int Position)
    {
        int Period_no = Periods.get(Position);
        Uri uri = TimetableContract.BuildTTCellWithSectionDaySlot(Section, Day_no, Period_no);

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        String time_string = "";
        while (cursor.moveToNext()) {

            Uri fac_uri = TimetableContract.BuildFacultyWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
            Cursor fac_cursor = context.getContentResolver().query(fac_uri, null, null, null, null);

            Uri sec_uri = TimetableContract.BuildSectionWithCSFid(cursor.getLong(cursor.getColumnIndex("CSF_Id")));
            Cursor sec_cursor = context.getContentResolver().query(sec_uri, null, null, null, null);

            Uri room_uri = TimetableContract.BuildRoomWithId(cursor.getLong(cursor.getColumnIndex("Room_Id")));
            Cursor room_cursor = context.getContentResolver().query(room_uri, null, null, null, null);

            sec_cursor.moveToNext();
            room_cursor.moveToNext();
            fac_cursor.moveToNext();
            time_string += sec_cursor.getString(sec_cursor.getColumnIndex("Subject_Code")) + " \n";
            time_string += "(" + fac_cursor.getString(fac_cursor.getColumnIndex("abbr")) + ") ";
            time_string += room_cursor.getString(room_cursor.getColumnIndex("Name")) + " \n";

            fac_cursor.close();
            sec_cursor.close();
            room_cursor.close();

        }
        cache.put(Position,time_string);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int Position) {
        int Period_no = Periods.get(Position);
        String time_string = (String) cache.get(Position);

        Log.d("time_string", time_string);

        TextView textView = (TextView) holder.linearLayout.findViewById(R.id.timetable_item_text);
        textView.setText(time_string.trim());
        }

    @Override
    public int getItemCount() {
        return Periods.size();
    }

}
