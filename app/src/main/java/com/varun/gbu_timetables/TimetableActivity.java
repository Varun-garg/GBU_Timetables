package com.varun.gbu_timetables;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
    private TimeTableBasic myclass;
    SharedPreferences prefs;
    Gson gson = new Gson();
    String existing_TAG = "favourites";
    private String myclass_TAG = "myclass";
    String json;
    Type favourites_type = new TypeToken<HashSet<TimeTableBasic>>() {
    }.getType();
    SharedPreferences.Editor editor;
    String message = "";
    private Boolean isFABOpen=false;
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;


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
        Toolbar toolbar = findViewById(R.id.toolbar);
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


        final TimeTableBasic info = getCurrentBasic();
        reload();

        final Drawable fav_yes = Utility.ThemeTools.FavouriteIcon.getFavYes(getApplicationContext());
        final Drawable fav_no = Utility.ThemeTools.FavouriteIcon.getFavNo(getApplicationContext());

        final Drawable myclass_yes = Utility.ThemeTools.MyClassIcon.getMyClassYes(getApplicationContext());
        final Drawable myclass_no = Utility.ThemeTools.MyClassIcon.getMyClassNo(getApplicationContext());

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);

        fab.setImageDrawable(Utility.ThemeTools.FabIcon.getfabup(getApplicationContext()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        if (existing_data.contains(info)) {
            fab1.setImageDrawable(fav_yes);
        } else {
            fab1.setImageDrawable(fav_no);
        }

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeTableBasic info = getCurrentBasic();
                reload();

                if (existing_data.contains(info)) {
                    existing_data.remove(info);
                    message = "Removed from Favourites";
                    fab1.setImageDrawable(fav_no);
                } else {
                    existing_data.add(info);
                    message = "Added to Favourites";
                    fab1.setImageDrawable(fav_yes);
                }

                json = gson.toJson(existing_data);

                editor.putString(existing_TAG, json);
                editor.apply();

                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            }
        });


        if(getCurrentBasic().equals(gson.fromJson(prefs.getString(myclass_TAG,""),TimeTableBasic.class)))
            fab2.setImageDrawable(myclass_yes);
        else
            fab2.setImageDrawable(myclass_no);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final TimeTableBasic setmyclass;
                final TimeTableBasic currentBasic = getCurrentBasic();
                reload();
                String myclass_json=prefs.getString(myclass_TAG,"");
                if(myclass_json.length()>1){
                  setmyclass=gson.fromJson(myclass_json,TimeTableBasic.class);
                }
                else {
                    setmyclass=null;
                }


                if (setmyclass==null) {
                    myclass=currentBasic;
                    message = "MyClass set";
                    fab2.setImageDrawable(myclass_yes);

                    json = gson.toJson(myclass);
                    editor.putString(myclass_TAG, json);
                    editor.apply();
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();

                } else {
                    if(currentBasic.equals(setmyclass)){
                        myclass=null;
                        message = "Current MyClass Removed";
                        fab2.setImageDrawable(myclass_no);




                        json = gson.toJson(myclass);
                        editor.putString(myclass_TAG, json);
                        editor.apply();
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        //Replace existing class with current class,Prompt user to confirm the action
                        AlertDialog.Builder builder;
                       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                          //  builder = new AlertDialog.Builder(TimetableActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        //} else {
                            builder = new AlertDialog.Builder(TimetableActivity.this);
                        //}
                        builder.setTitle("Replace Current MyClass")
                                .setMessage("There is an existing MyClass, This will replace current Myclass")
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        myclass=currentBasic;
                                        json = gson.toJson(myclass);
                                        message = "MyClass Replaced";
                                        fab2.setImageDrawable(myclass_yes);
                                        editor.putString(myclass_TAG, json);
                                        editor.apply();
                                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                       // no need to do anything here
                                    }
                                })
                                //.setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }

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
    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.fab_margin_1));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.fab_margin_2));
        fab.animate().rotation(180);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab.animate().rotation(0);
    }

}
