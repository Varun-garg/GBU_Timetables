package com.varun.gbu_timetables;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.varun.gbu_timetables.service.SettingsFragment;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        int set_theme = R.style.AppTheme;
        if (set_theme != saved_theme)
            setTheme(saved_theme);

        setTitle("Settings");
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_frame, new SettingsFragment())
                .commit();
    }

}
