package com.varun.gbu_timetables;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int saved_theme = Utility.getThemeId(getApplicationContext());
        int set_theme = R.style.AppTheme;
        if (set_theme != saved_theme)
            setTheme(saved_theme);

        setTitle("About GBU Timetables");
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(Html.fromHtml("<h2><b>GBU Timetables</b></h2>"));
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            int build = info.versionCode;
            String name = info.versionName;
            textView.append("Version " + name + "\n Build number " + Integer.toString(build) + "\n\n");
        } catch (Exception e) {
            Log.d("error", e.toString());
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.append("Official Timetables Android Client for students and faculty of GBU\n\n");
        textView.append("Source code:\n");

        String url = "<a href=\"https://github.com/Varun-garg/gbu-timetable_sql\">https://github.com/Varun-garg/gbu-timetable_sql</a>";
        textView.append(Html.fromHtml(url));
        textView.append("\n\n");
        textView.append("Developed under guidance of Dr. Amit K. Awasthi\n\n");
        textView.append("Developed by Varun Garg <");
        textView.append(Html.fromHtml("<a href=\"mailto:varun.10@live.com\">varun.10@live.com</a>><br />"));
        textView.append("\nThanks to CyanogenMod for MD5 library\n\n");
        textView.append("Released Under GPLv3 Licence");
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