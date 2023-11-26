package com.varun.gbu_timetables;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.varun.gbu_timetables.data.model.CSF;

import java.util.ArrayList;

/**
 * Created by varun on 12/28/15.
 */
public class DetailsAdapter extends ArrayAdapter<CSF> {

    String type;

    public DetailsAdapter(Context context, ArrayList<CSF> values, String type) {
        super(context, 0, values);
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_fac_item, parent, false);
        }

        CSF mCSF = getItem(position);
        TextView sub_code = convertView.findViewById(R.id.sub_code);
        sub_code.setText(mCSF.Sub_Code);

        TextView sub_name = convertView.findViewById(R.id.sub_name);
        sub_name.setText(mCSF.Sub_name);

        TextView fac_code = convertView.findViewById(R.id.fac_code);
        fac_code.setText(mCSF.Fac_abbr);

        TextView fac_name = convertView.findViewById(R.id.fac_name);

        fac_name.setText(mCSF.Fac_name);


        if (type.equals("Faculty")) {
            fac_code.setText("Section");
            fac_name.setText(mCSF.Section_name);
        }

        convertView.setTag(mCSF);
        return convertView;
    }

}