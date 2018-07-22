package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.varun.gbu_timetables.adaptor.TimetableAdapter;
import com.varun.gbu_timetables.data.Model.CSF;
import com.varun.gbu_timetables.data.Model.CSF_FAC_MAP_KEY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimetableFragmentSinglePage extends Fragment {

    String[] day_names = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    String title;
    String type;
    TableLayout tableLayout;
    ProgressDialog dialog;
    int BgBoxDefault_id, max_period, min_period;

    public TimetableFragmentSinglePage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        title = getActivity().getIntent().getExtras().getString("Timetable_title");
        getActivity().setTitle(title);
        BgBoxDefault_id = Utility.ThemeTools.BackgroundIcons.getBgBoxDefaultDrawable(getContext());

        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        HashMap<CSF_FAC_MAP_KEY, CSF> CSF_Details;

        dialog = new ProgressDialog(getContext(), Utility.ThemeTools.getDialogThemeId(getContext()));
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        final ArrayList<Integer> days = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            days.add(i);
        }


        tableLayout = rootView.findViewById(R.id.timetable_table);


        type = getActivity().getIntent().getExtras().getString("Type");
        Long id = null;
        if (type.equals("Section")) {
            id = getActivity().getIntent().getExtras().getLong("Section_id");
        } else if (type.equals("Faculty")) {
            id = getActivity().getIntent().getExtras().getLong("Faculty_id");
        }

        //We insert Periods
        TableRow header = (TableRow) inflater.inflate(R.layout.timetable_row, null);
        int beg_min = 30;
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(width, TableRow.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams halfparams = new TableRow.LayoutParams(width / 2, TableRow.LayoutParams.MATCH_PARENT);


        TextView blank = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
        blank.setLayoutParams(halfparams);
        blank.setBackgroundResource(BgBoxDefault_id);
        header.addView(blank);
        final TimetableAdapter timetableAdapter = new TimetableAdapter(getContext(), days, id, type, title);
        max_period = (int) timetableAdapter.getMaxPeriods();
        min_period = (int) timetableAdapter.getMinPeriods();

        for (int i = min_period; i <= max_period; i++) {
            TextView textView = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
            textView.setLayoutParams(cellParams);
            textView.setText(Integer.toString(Utility.getPeriodTitleNo(i)) + ":" + Integer.toString(beg_min) + " - ");
            textView.append(Integer.toString(Utility.getPeriodTitleNo(i + 1)) + ":" + Integer.toString(beg_min));
            textView.setBackgroundResource(BgBoxDefault_id);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(5, 5, 0, 0);
            header.addView(textView);
        }
        tableLayout.addView(header);

        for (int i = 0; i < days.size(); i++) { //we are only interested in position not what is inside days

            TableRow tableRow = (TableRow) inflater.inflate(R.layout.timetable_row, null);

            TextView textView = (TextView) inflater.inflate(R.layout.timetable_item_single, null);
            textView.setLayoutParams(halfparams);
            textView.setText(day_names[i]);
            textView.setBackgroundResource(BgBoxDefault_id);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(5, 5, 0, 0);

            tableRow.addView(textView);
            String prev_time_string = "";
            int duplicates = 1;
            LinearLayout prev_item = null;
            for (int j = min_period; j <= max_period; j++) {
                LinearLayout item = (LinearLayout) timetableAdapter.getView(i, j);
                String this_time_string = (String) item.getTag(R.string.time_string);

                if (prev_time_string.equals(this_time_string) && prev_time_string.length() > 0 && prev_item != null) {
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
        tableLayout.addView(new LinearLayout(this.getContext()));

        CSF_Details = timetableAdapter.getCSFDetails();
        Set<CSF> csf_items = new HashSet<>(CSF_Details.values());

        ArrayList<CSF> CSF_Array = new ArrayList<>(csf_items);
        final DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), CSF_Array, type);

        for (int i = 0; i < CSF_Array.size(); i++) {
            final View detail_item = detailsAdapter.getView(i, null, null);
            detail_item.setPadding(20, 0, 20, 0);
            detail_item.setBackgroundResource(BgBoxDefault_id);
            detail_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CSF csf = (CSF) detail_item.getTag();
                    Intent intent = new Intent(getActivity(), TimetableActivity.class);

                    if (type.equals("Faculty")) {
                        dialog.setMessage("Loading " + csf.Section_name);
                        intent.putExtra("Section_id", csf.Section_id);
                        intent.putExtra("Timetable_title", csf.Section_name);
                        intent.putExtra("Type", "Section");
                    } else if (type.equals("Section")) {
                        dialog.setMessage("Loading " + csf.Fac_name);
                        intent.putExtra("Faculty_id", csf.Fac_id);
                        intent.putExtra("Timetable_title", csf.Fac_name);
                        intent.putExtra("Type", "Faculty");
                    }
                    dialog.show();
                    startActivity(intent);
                }
            });

            tableLayout.addView(detail_item);
        }

//        final ListView lv2 = rootView.findViewById(R.id.timetable_faculty_data);
//        lv2.setAdapter(detailsAdapter);
//        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
//                CSF csf = (CSF) view.getTag();
//
//
//                Intent intent = new Intent(getActivity(), TimetableActivity.class);
//
//                if (type.equals("Faculty")) {
//                    dialog.setMessage("Loading " + csf.Section_name);
//                    intent.putExtra("Section_id", csf.Section_id);
//                    intent.putExtra("Timetable_title", csf.Section_name);
//                    intent.putExtra("Type", "Section");
//                } else if (type.equals("Section")) {
//                    dialog.setMessage("Loading " + csf.Fac_name);
//                    intent.putExtra("Faculty_id", csf.Fac_id);
//                    intent.putExtra("Timetable_title", csf.Fac_name);
//                    intent.putExtra("Type", "Faculty");
//                }
//                dialog.show();
//                startActivity(intent);
//            }
//        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog.hide();
    }

}
