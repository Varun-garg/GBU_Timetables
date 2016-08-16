package com.varun.gbu_timetables;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.varun.gbu_timetables.data.model.TimeTableBasic;

import java.util.ArrayList;

/**
 * Created by varun on 12/28/15.
 */
public class FavouritesAdapter extends ArrayAdapter<TimeTableBasic> {

    Drawable ItemIconDrawable;


    public FavouritesAdapter(Context context, ArrayList<TimeTableBasic> values) {
        super(context, 0, values);
        ItemIconDrawable = Utility.ThemeTools.FavouriteIcon.getFavYesInverse(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.favourites_item, null);

            ImageView GroupIcon = (ImageView) convertView.findViewById(R.id.image_view);
            GroupIcon.setImageDrawable(ItemIconDrawable);
        }

        TimeTableBasic info = getItem(position);

        TextView textView = (TextView) convertView.findViewById(R.id.textview);
        textView.setText(info.getTitle());

        convertView.setTag(info);
        return convertView;
    }

}