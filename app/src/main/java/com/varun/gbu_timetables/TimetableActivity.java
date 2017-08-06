package com.varun.gbu_timetables;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varun.gbu_timetables.data.Model.TimeTableBasic;

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
        String Title = getIntent().getExtras().getString("Timetable_title");
        String Type = getIntent().getExtras().getString("Type");
        Long Id = null;
        if (Type.equals("Section")) {
            Id = getIntent().getExtras().getLong("Section_id");
        } else if (Type.equals("Faculty")) {
            Id = getIntent().getExtras().getLong("Faculty_id");
        }

        TimeTableBasic info = new TimeTableBasic();
        info.setTitle(Title);
        info.setId(Id);
        info.setType(Type);
        return info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        setTheme(saved_theme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        String stored_mode = prefs.getString(getString(R.string.pref_tt_display_type_key), "0");

        if (savedInstanceState == null) {

            if (stored_mode.equals("0"))
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new TimetableFragmentPager())
                        .commit();
            else //1
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new TimetableFragmentSinglePage())
                        .commit();
        }


        TimeTableBasic info = getCurrentBasic();
        reload();

        final Drawable fav_yes = Utility.ThemeTools.FavouriteIcon.getFavYes(getApplicationContext());
        final Drawable fav_no = Utility.ThemeTools.FavouriteIcon.getFavNo(getApplicationContext());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (existing_data.contains(info)) {
            fab.setImageDrawable(fav_yes);
        } else {
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
                editor.apply();

                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
