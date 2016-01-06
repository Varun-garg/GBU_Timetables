package com.varun.gbu_timetables;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varun.gbu_timetables.data.TimeTableBasic;

import java.lang.reflect.Type;
import java.util.HashSet;

public class TimetableActivity extends AppCompatActivity {

    HashSet<TimeTableBasic> existing_data;
    SharedPreferences prefs;
    Gson gson = new Gson();
    String existing_TAG = "favourites";
    String json;
    Type favourites_type = new TypeToken<HashSet<TimeTableBasic>>() {
    }.getType();
    SharedPreferences.Editor editor;
    String message = "";

    private void reload() {
        json = prefs.getString(existing_TAG, null);
        existing_data = gson.fromJson(json, favourites_type);
        if (existing_data == null)
            existing_data = new HashSet<>();
    }

    public TimeTableBasic getCurrentBasic() {
        String title = getIntent().getExtras().getString("Timetable_title");
        String type = getIntent().getExtras().getString("Type");
        Long id = null;
        if (type.equals("Section")) {
            id = getIntent().getExtras().getLong("Section_id");
        } else if (type.equals("Faculty")) {
            id = getIntent().getExtras().getLong("Faculty_id");
        }

        TimeTableBasic info = new TimeTableBasic();
        info.Title = title;
        info.Id = id;
        info.Type = type;
        return info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        TimeTableBasic info = getCurrentBasic();
        reload();
        final Drawable fav_yes = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white_24dp);
        final Drawable fav_no = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_favorite_border_white_24dp);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(existing_data.contains(info))
        {
            fab.setImageDrawable(fav_yes);
        }
        else
        {
            fab.setImageDrawable(fav_no);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeTableBasic info = getCurrentBasic();
                reload();

                if (existing_data.contains(info)) {
                    existing_data.remove(info);
                    message = "Removed from Favourites";
                    fab.setImageDrawable(fav_no);
                } else {
                    existing_data.add(info);
                    message = "Added to Favourites";
                    fab.setImageDrawable(fav_yes);
                }
                json = gson.toJson(existing_data);

                editor.putString(existing_TAG, json);
                editor.commit();

                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            }
        });
    }


}
