package com.varun.gbu_timetables.adaptor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.varun.gbu_timetables.R;
import com.varun.gbu_timetables.Utility;
import com.varun.gbu_timetables.data.Model.TimeTableBasic;

import java.util.ArrayList;

/**
 * Created by varun on 12/28/15.
 */
public class FavouritesAdapter extends ArrayAdapter<TimeTableBasic> {

    Drawable ItemIconDrawable;

    private Context context;

    public FavouritesAdapter(Context context, ArrayList<TimeTableBasic> values) {
        super(context, 0, values);
        ItemIconDrawable = Utility.ThemeTools.FavouriteIcon.getFavYesInverse(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.favourites_item, null);

            //ImageView GroupIcon = convertView.findViewById(R.id.image_view);
            //GroupIcon.setImageDrawable(ItemIconDrawable);
            //GroupIcon.setImageDrawable(ItemIconDrawable);
        }

        TimeTableBasic info = getItem(position);
        ImageView icon = convertView.findViewById(R.id.image_view);
        if (position == 0)
            icon.setImageDrawable(Utility.ThemeTools.MyClassIcon.getMyClassYesInverse(context));
        else
            icon.setImageDrawable(ItemIconDrawable);


        TextView textView = convertView.findViewById(R.id.textview);
        textView.setText(info.getTitle());

        convertView.setTag(info);
        return convertView;
    }

}