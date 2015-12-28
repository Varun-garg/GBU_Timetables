package varun.timetables_sql;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import varun.timetables_sql.data.Key;

/**
 * Created by varun on 12/23/15.
 */
public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {


    int Day_pos;
    int Section;
    Context context;
    ArrayList<Integer> Periods;
    HashMap<Key, String> cache;

    int max_lines = 2;

    public TimetableAdapter(Context context, ArrayList<Integer> Periods, int Section, int Day_pos, HashMap<Key, String> cache, int max_lines) {
        this.Day_pos = Day_pos;
        this.Section = Section;
        this.context = context;
        this.Periods = Periods;
        this.cache = cache;
        this.max_lines = max_lines;
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int Position) {
        Key key = new Key(Day_pos, Position);
        String time_string = (String) cache.get(key);
        TextView textView = (TextView) holder.linearLayout.findViewById(R.id.timetable_item_text);
        textView.setLines(max_lines);
        textView.setText(time_string.trim());
    }

    @Override
    public int getItemCount() {
        return Periods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
        }
    }

}
