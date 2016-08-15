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
public class SectionsFragment extends Fragment {

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

        Uri Schools_uri = TimetableContract.BuildSchool();
        Cursor schools_c = getContext().getContentResolver().query(Schools_uri, null, null, null, null);

        while (schools_c.moveToNext()) {
            String school = schools_c.getString(schools_c.getColumnIndex("school"));
            HeaderListData.add(school);
            Long Program_id = schools_c.getLong(schools_c.getColumnIndex("program_id"));
            Uri Program_uri = TimetableContract.BuildSectionWithProgramId(Program_id);
            Cursor program_cursor = getContext().getContentResolver().query(Program_uri, null, null, null, null);
            List<SectionsFacultyAdapter.Common_type> Sections = ChildrenListData.get(school);
            if (Sections == null) Sections = new ArrayList<>();
            while (program_cursor.moveToNext()) {
                SectionsFacultyAdapter.Common_type s = new SectionsFacultyAdapter.Common_type();
                s.id = program_cursor.getLong(program_cursor.getColumnIndex("section_id"));
                s.Name = program_cursor.getString(program_cursor.getColumnIndex("Name")).trim();
                s.Name = Utility.getFullSectionName(s.Name,getContext());
                Sections.add(s);
            }
            program_cursor.close();
            ChildrenListData.put(school, Sections);
        }
        schools_c.close();

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
                intent.putExtra("Type", "Section");
                intent.putExtra("Section_id", s.id);
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
