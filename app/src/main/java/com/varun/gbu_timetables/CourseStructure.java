package com.varun.gbu_timetables;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CourseStructure extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        setTheme(saved_theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_structure);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
