package com.varun.gbu_timetables;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class CourseStructure extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        setTheme(saved_theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_structure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
