package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varun.gbu_timetables.adaptor.FavouritesAdapter;
import com.varun.gbu_timetables.data.model.TimeTableBasic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

public class FavouritesFragment extends Fragment {

    ListView listView;
    FavouritesAdapter favouritesAdapter;
    ProgressDialog progressDialog;
    TimeTableBasic emptyTimeTableBasic;
    ArrayList<TimeTableBasic> FavouritesList;
    private TimeTableBasic myclass = null;
    private int ElementsCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emptyTimeTableBasic = new TimeTableBasic();
        emptyTimeTableBasic.setTitle("No Favourites Yet.");
        emptyTimeTableBasic.setId(Long.valueOf(0));
        emptyTimeTableBasic.setType("");
        FavouritesList = new ArrayList<>();
    }

    public ArrayList<TimeTableBasic> getFavourites() {

        HashSet<TimeTableBasic> existing_data = new HashSet<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Gson gson = new Gson();
        String existing_TAG = "favourites";
        String myclass_TAG = "myclass";

        Type favourites_type = new TypeToken<HashSet<TimeTableBasic>>() {
        }.getType();

        String json = prefs.getString(existing_TAG, null);
        String myclass_json = prefs.getString(myclass_TAG, "");
        try {
            if (!("null".equals(myclass_json)) && myclass_json.length() > 1) {

                myclass = gson.fromJson(myclass_json, TimeTableBasic.class);
            } else {

                myclass = new TimeTableBasic();
                myclass.setTitle("MyClass not set yet.");
                myclass.setId(Long.valueOf(0));
                myclass.setType("");
            }
            if (json != null && json.length() > 0) {
                existing_data = gson.fromJson(json, favourites_type);
                ElementsCount = existing_data.size();
            }

        } catch (Exception e) {
            existing_data = new HashSet<>();
            ElementsCount = 0;
            String Message = "Fatal error: plz screenshot next messages and mail them to varun.10@live.com. Thank you.";
            Toast.makeText(getContext(), Message, Toast.LENGTH_LONG).show();
            Message = "Old Data " + json;
            Toast.makeText(getContext(), Message, Toast.LENGTH_LONG).show();
            existing_data.add(emptyTimeTableBasic);
            json = gson.toJson(existing_data);
            Message = "New Format " + json;
            Toast.makeText(getContext(), Message, Toast.LENGTH_LONG).show();

        }

        if (ElementsCount == 0)
            existing_data.add(emptyTimeTableBasic);

        ArrayList<TimeTableBasic> existing_data_list = new ArrayList<>(existing_data);
        return existing_data_list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.favourites, container, false);
        listView = rootView.findViewById(R.id.listView);
        favouritesAdapter = new FavouritesAdapter(getContext(), FavouritesList);
        listView.setAdapter(favouritesAdapter);

        progressDialog = new ProgressDialog(getContext(), Utility.ThemeTools.getDialogThemeId(getContext()));
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);

        listView = rootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {


                TimeTableBasic item = (TimeTableBasic) view.getTag();

                if (item.getId() == 0)
                    return;

                progressDialog.setMessage("Loading " + item.getTitle());
                progressDialog.show();

                Intent intent = new Intent(getActivity(), TimetableActivity.class);
                intent.putExtra("Type", item.getType());
                intent.putExtra("Timetable_title", item.getTitle());
                if (item.getType().equals("Section"))
                    intent.putExtra("Section_id", item.getId());
                else
                    intent.putExtra("Faculty_id", item.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.dismiss();

        FavouritesList = getFavourites();
        favouritesAdapter.clear(); //clear previous data

        favouritesAdapter.add(myclass);
        for (int i = 0; i < FavouritesList.size(); i++) { //add all requires api 11
            TimeTableBasic item = FavouritesList.get(i);
            favouritesAdapter.add(item);
        }

        favouritesAdapter.notifyDataSetChanged();
    }

}
