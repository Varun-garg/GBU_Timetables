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


    List<String> HeaderListData;
    HashMap<String, List<SectionsFacultyAdapter.Common_type>> ChildrenListData;
    ProgressDialog progressDialog;
    SectionsFacultyAdapter schoolsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext(), Utility.ThemeTools.getDialogThemeId(getContext()));
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);

        HeaderListData = new ArrayList<>();
        ChildrenListData = new HashMap<>();

        Uri Faculty_uri = TimetableContract.BuildFaculty();
        Cursor faculty_cursor = getContext().getContentResolver().query(Faculty_uri, null, null, null, null);

        while (faculty_cursor.moveToNext()) {
            String school = faculty_cursor.getString(faculty_cursor.getColumnIndex("school"));
            SectionsFacultyAdapter.Common_type ct = new SectionsFacultyAdapter.Common_type();
            ct.id = faculty_cursor.getLong(faculty_cursor.getColumnIndex("faculty_id"));
            ct.Name = faculty_cursor.getString(faculty_cursor.getColumnIndex("name"));

            HeaderListData.add(school);

            List<SectionsFacultyAdapter.Common_type> facultyList = ChildrenListData.get(school);
            if (facultyList == null) facultyList = new ArrayList<>();
            facultyList.add(ct);
            ChildrenListData.put(school, facultyList);
        }
        faculty_cursor.close();

        Set<String> hs = new LinkedHashSet<>(HeaderListData); // now we remove duplicates
        HeaderListData.clear();
        HeaderListData.addAll(hs);

        schoolsAdapter = new SectionsFacultyAdapter(getContext(), HeaderListData, ChildrenListData);
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
                String program = HeaderListData.get(groupPosition);
                SectionsFacultyAdapter.Common_type s = ChildrenListData.get(program).get(childPosition);

                progressDialog.setMessage("Loading " + s.Name);
                progressDialog.show();

                Intent intent = new Intent(getActivity(), TimetableActivity.class);
                intent.putExtra("Type", "Faculty");
                intent.putExtra("Faculty_id", s.id);
                intent.putExtra("Timetable_title", s.Name);
                startActivity(intent);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }
}
