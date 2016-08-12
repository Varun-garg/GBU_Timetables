package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.varun.gbu_timetables.data.SchoolsFacultyAdapter;
import com.varun.gbu_timetables.data.TimetableContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class SectionsFragment extends Fragment {

    List<String> Header_data;
    HashMap<String, List<SchoolsFacultyAdapter.Common_type>> Children_data;
    ProgressDialog dialog;
    SchoolsFacultyAdapter schoolsAdapter;

    public SectionsFragment() {
        Header_data = new ArrayList<>();
        Children_data = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(this.getClass().getSimpleName(),"onCreate Called");
        dialog = new ProgressDialog(getContext(), Utility.getDialogThemeId(getContext()));
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        Uri Schools_uri = TimetableContract.BuildSchool();
        Cursor schools_c = getContext().getContentResolver().query(Schools_uri, null, null, null, null);

        while (schools_c.moveToNext()) {
            String school = schools_c.getString(schools_c.getColumnIndex("school"));
            Header_data.add(school);
            Long Program_id = schools_c.getLong(schools_c.getColumnIndex("program_id"));
            Uri Program_uri = TimetableContract.BuildSectionWithProgramId(Program_id);
            Cursor program_cursor = getContext().getContentResolver().query(Program_uri, null, null, null, null);
            List<SchoolsFacultyAdapter.Common_type> Sections = Children_data.get(school);
            if (Sections == null) Sections = new ArrayList<>();
            while (program_cursor.moveToNext()) {
                SchoolsFacultyAdapter.Common_type s = new SchoolsFacultyAdapter.Common_type();
                s.id = program_cursor.getLong(program_cursor.getColumnIndex("section_id"));
                s.Name = program_cursor.getString(program_cursor.getColumnIndex("Name")).trim();
                s.Name = Utility.getFullSectionName(s.Name,getContext());
                Sections.add(s);
            }
            program_cursor.close();
            Children_data.put(school, Sections);
        }
        schools_c.close();

        Set<String> hs = new LinkedHashSet<>(Header_data); // now we remove duplicates
        Header_data.clear();
        Header_data.addAll(hs);

        schoolsAdapter = new SchoolsFacultyAdapter(getContext(), Header_data, Children_data);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(this.getClass().getSimpleName(),"onCreateView Called");

        View rootView = inflater.inflate(R.layout.timetable_expandable_lv, container, false);

        ExpandableListView schools_lv = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        schools_lv.setAdapter(schoolsAdapter);

        schools_lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String program = Header_data.get(groupPosition);
                SchoolsFacultyAdapter.Common_type s = Children_data.get(program).get(childPosition);

                dialog.setMessage("Loading " + s.Name);
                dialog.show();

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
        dialog.hide();
    }
}
