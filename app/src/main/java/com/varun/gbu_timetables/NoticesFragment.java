package com.varun.gbu_timetables;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class NoticesFragment extends Fragment {
    ListView listview;
    int intCount = 0;
    ArrayList<String> tutorialList = new ArrayList<String>();
    String[] courseList = {"C-Programming", "Data Structure", "database", "Python",
            "Java", "Operating System", "Compiler Design", "Android Development"};
    String jsonURL = "https://manage.gbu.ac.in/api/listnotices";
    // private final static String URL = "http://manage.gbu.ac.in/api/listnotices";
    ListView lv;
    private ArrayList<String> my_notices;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        my_notices = new ArrayList<String>();
        my_notices.clear();
        my_notices.add(0, "Notice 1");
        my_notices.add(1, "Notice 2");
        //adapter = new ArrayAdapter<String> ( this, android.R.layout.simple_list_item_1, android.R.id.text1,my_notices);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notice, container, false);
        //listview = view.findViewById(R.id.listView);
        // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_notice, R.id.notice_title,courseList);
        //listview.setAdapter(arrayAdapter);
        lv = view.findViewById(R.id.listView);
      //  new JSONDownloader(getContext(), jsonURL, lv).execute();
        return view;
    }

}
