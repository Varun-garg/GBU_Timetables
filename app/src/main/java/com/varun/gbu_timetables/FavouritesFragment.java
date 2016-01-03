package com.varun.gbu_timetables;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varun.gbu_timetables.data.TimeTableBasic;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FavouritesFragment extends Fragment {

    ListView lv;
    FavouritesAdapter favouritesAdapter;
    private int no_elements = 0;

    TimeTableBasic empty;

    public FavouritesFragment() {
        empty =  new TimeTableBasic();
        empty.Title = "No Favourites Yet.";
        empty.Id = Long.valueOf(0);
        empty.Type = "";
    }

    public ArrayList<TimeTableBasic> getFavourites()
    {
        HashSet<TimeTableBasic> existing_data = new HashSet<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Gson gson = new Gson();
        String existing_TAG = "favourites";
        Type favourites_type = new TypeToken<HashSet<TimeTableBasic>>() {}.getType();

        String json = prefs.getString(existing_TAG, null);
        if(json!=null && json.length()>0) {
            existing_data = gson.fromJson(json, favourites_type);
            no_elements = existing_data.size();
        }
        if(no_elements == 0)
           existing_data.add(empty);

        ArrayList<TimeTableBasic> existing_data_list = new ArrayList<>(existing_data);
        return existing_data_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favourites, container, false);
        lv = (ListView) rootView.findViewById(R.id.listView);

        ArrayList<TimeTableBasic> existing_data_list = getFavourites();

        favouritesAdapter = new FavouritesAdapter(getContext(),existing_data_list);
        lv.setAdapter(favouritesAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if(no_elements == 0 )
                    return;
                TimeTableBasic item = (TimeTableBasic) view.getTag();
                Intent intent = new Intent(getActivity(), TimetableActivity.class);
                intent.putExtra("Type", item.Type);
                intent.putExtra("Timetable_title", item.Title);
                if (item.Type.equals("Section"))
                    intent.putExtra("Section_id", item.Id);
                else
                    intent.putExtra("Faculty_id",item.Id);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ArrayList<TimeTableBasic> existing_data_list = getFavourites();
        favouritesAdapter.clear();
        for(int i = 0; i<existing_data_list.size();i++)
        {
            TimeTableBasic item = existing_data_list.get(i);
            favouritesAdapter.add(item);
        }
        favouritesAdapter.notifyDataSetChanged();
    }

}
