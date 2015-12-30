package com.varun.gbu_timetables;

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

import com.varun.gbu_timetables.data.TimetableContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.varun.gbu_timetables.R;

import com.varun.gbu_timetables.data.SchoolsAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    List<String> Header_data;
    HashMap<String,List<Section>> Children_data;

    public MainActivityFragment() {
        Header_data = new ArrayList<String>();
        Children_data = new HashMap<String,List<Section>>();

    }

    public class Section
    {
        public Long id;
        public String Name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ExpandableListView schools_lv = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        Uri Schools_uri = TimetableContract.BuildSchool();
        Cursor schools_c = getContext().getContentResolver().query(Schools_uri,null,null,null,null);

        while (schools_c.moveToNext())
        {
            String school = schools_c.getString(schools_c.getColumnIndex("school"));
            Header_data.add(school);
            Long Program_id = schools_c.getLong(schools_c.getColumnIndex("program_id"));
            Uri Program_uri = TimetableContract.BuildSectionWithProgramId(Program_id);
            Cursor program_cursor = getContext().getContentResolver().query(Program_uri,null,null,null,null);
            List<Section> Sections = Children_data.get(school);
            if(Sections == null) Sections = new ArrayList<Section>();
            while (program_cursor.moveToNext())
            {
                Section s = new Section();
                s.id = program_cursor.getLong(program_cursor.getColumnIndex("section_id"));
                s.Name = program_cursor.getString(program_cursor.getColumnIndex("Name")).trim();
                Sections.add(s);
            }
            Children_data.put(school,Sections);
        }

        Set<String> hs = new LinkedHashSet<>(Header_data); // now we remove duplicates
        Header_data.clear();
        Header_data.addAll(hs);

        SchoolsAdapter schoolsAdapter = new SchoolsAdapter(getContext(),Header_data,Children_data);
        schools_lv.setAdapter(schoolsAdapter);

        schools_lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String program = Header_data.get(groupPosition);
                Section s = Children_data.get(program).get(childPosition);
                Intent intent = new Intent(getActivity(),TimetableActivity.class);
                intent.putExtra("Section_id",s.id);
                intent.putExtra("Section_Name",s.Name);
                startActivity(intent);
                return false;
            }
        });

        return rootView;
    }
}
