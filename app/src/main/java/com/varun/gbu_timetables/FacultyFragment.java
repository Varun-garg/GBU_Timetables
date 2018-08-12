package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;

import com.varun.gbu_timetables.adaptor.SectionsFacultyAdapter;
import com.varun.gbu_timetables.data.Database.TimetableContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FacultyFragment extends Fragment {


    List<String> HeaderListData;
    HashMap<String, List<SectionsFacultyAdapter.Common_type>> ChildrenListData;
    ProgressDialog progressDialog;
    SectionsFacultyAdapter schoolsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext(), Utility.ThemeTools.getDialogThemeId(getContext()));
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);

        HashSet<String> RedundantHeaderListData = new HashSet<>();
        ChildrenListData = new HashMap<>();

        Uri Faculty_uri = TimetableContract.BuildFaculty();
        Cursor faculty_cursor = getContext().getContentResolver().query(Faculty_uri, null, null, null, null);

        while (faculty_cursor.moveToNext()) {
            String school = faculty_cursor.getString(faculty_cursor.getColumnIndex("school"));
            SectionsFacultyAdapter.Common_type ct = new SectionsFacultyAdapter.Common_type();
            ct.id = faculty_cursor.getLong(faculty_cursor.getColumnIndex("faculty_id"));
            ct.Name = faculty_cursor.getString(faculty_cursor.getColumnIndex("name"));

            RedundantHeaderListData.add(school);

            List<SectionsFacultyAdapter.Common_type> facultyList = ChildrenListData.get(school);
            if (facultyList == null) facultyList = new ArrayList<>();
            facultyList.add(ct);
            ChildrenListData.put(school, facultyList);
        }
        faculty_cursor.close();

        HeaderListData = new ArrayList<>(RedundantHeaderListData);
        schoolsAdapter = new SectionsFacultyAdapter(getContext(), HeaderListData, ChildrenListData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.expandable_listview, container, false);
        final ExpandableListView schools_lv = rootView.findViewById(R.id.expandableListView);

        schools_lv.setAdapter(schoolsAdapter);

        Context context = getContext();
        ViewTreeObserver vto = schools_lv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    schools_lv.setIndicatorBounds(schools_lv.getRight() - Utility.convertDpToPixel(60, context), schools_lv.getWidth());
                } else {
                    schools_lv.setIndicatorBoundsRelative(schools_lv.getRight() - Utility.convertDpToPixel(60, context), schools_lv.getWidth());
                }
            }
        });

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
