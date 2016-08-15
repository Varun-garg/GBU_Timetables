package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.varun.gbu_timetables.data.SectionsFacultyAdapter;
import com.varun.gbu_timetables.data.Database.TimetableContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class FacultyFragment extends Fragment {


    List<String> Header_data;
    HashMap<String, List<SectionsFacultyAdapter.Common_type>> Children_data;
    ProgressDialog dialog;
    SectionsFacultyAdapter schoolsAdapter;


    public FacultyFragment() {
        Header_data = new ArrayList<>();
        Children_data = new HashMap<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getContext(), Utility.ThemeTools.getDialogThemeId(getContext()));
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        Uri Faculty_uri = TimetableContract.BuildFaculty();
        Cursor faculty_cursor = getContext().getContentResolver().query(Faculty_uri, null, null, null, null);


        while (faculty_cursor.moveToNext()) {
            String school = faculty_cursor.getString(faculty_cursor.getColumnIndex("school"));
            SectionsFacultyAdapter.Common_type ct = new SectionsFacultyAdapter.Common_type();
            ct.id = faculty_cursor.getLong(faculty_cursor.getColumnIndex("faculty_id"));
            ct.Name = faculty_cursor.getString(faculty_cursor.getColumnIndex("name"));

            Header_data.add(school);

            List<SectionsFacultyAdapter.Common_type> facultyList = Children_data.get(school);
            if (facultyList == null) facultyList = new ArrayList<>();
            facultyList.add(ct);
            Children_data.put(school, facultyList);
        }
        faculty_cursor.close();

        Set<String> hs = new LinkedHashSet<>(Header_data); // now we remove duplicates
        Header_data.clear();
        Header_data.addAll(hs);

        schoolsAdapter = new SectionsFacultyAdapter(getContext(), Header_data, Children_data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.timetable_expandable_lv, container, false);
        ExpandableListView schools_lv = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

        schools_lv.setAdapter(schoolsAdapter);

        schools_lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String program = Header_data.get(groupPosition);
                SectionsFacultyAdapter.Common_type s = Children_data.get(program).get(childPosition);

                dialog.setMessage("Loading " + s.Name);
                dialog.show();

                Intent intent = new Intent(getActivity(), TimetableActivity.class);
                intent.putExtra("Type", "Faculty");
                intent.putExtra("Faculty_id", s.id);
                intent.putExtra("Timetable_title", s.Name);
                startActivity(intent);
                dialog.dismiss();
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog.hide();
    }
}
