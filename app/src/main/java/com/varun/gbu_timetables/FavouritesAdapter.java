package com.varun.gbu_timetables;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varun.gbu_timetables.data.CSF;

import java.util.ArrayList;

import com.varun.gbu_timetables.R;
import com.varun.gbu_timetables.data.TimeTableBasic;

/**
 * Created by varun on 12/28/15.
 */
public class FavouritesAdapter extends ArrayAdapter<TimeTableBasic> {

    String type;
    public FavouritesAdapter(Context context, ArrayList<TimeTableBasic> values) {
        super(context, 0, values);
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.favourites_item, null);
        }

        TimeTableBasic info = getItem(position);

        TextView textView = (TextView) convertView.findViewById(R.id.textview);
        textView.setText(info.Title);

        convertView.setTag(info);
        return convertView;
    }

}