package com.varun.gbu_timetables;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.varun.gbu_timetables.service.MyFirebaseInstanceIdService;

public class AboutActivity extends AppCompatActivity {

    //@SuppressLint({"TimberArgCount", "LogNotTimber"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        int set_theme = R.style.AppTheme;
        if (set_theme != saved_theme)
            setTheme(saved_theme);

        setTitle("About GBU Timetables");
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView textView = findViewById(R.id.textView);

        textView.setText(Html.fromHtml("<h2><b>GBU Timetables</b></h2>"));
        String BuildInfo = "";
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            int build = info.versionCode;
            String name = info.versionName;
            BuildInfo = "Version " + name + "\n Build number " + Integer.toString(build) + "\n\n";
        } catch (Exception e) {
            Log.d("error", e.toString());
        }
        final String FinalBuildInfo = BuildInfo;

        SpannableString ss = new SpannableString(BuildInfo);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Toast.makeText(getApplicationContext(), "Copied id to clipboard.", Toast.LENGTH_LONG).show();
                Utility.setClipboard(getApplicationContext(), Utility.getFirebaseInstanceId(getApplicationContext()));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, FinalBuildInfo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();
        Intent intent = new Intent(getApplicationContext(), myFirebaseInstanceIdService.getClass());
        //   Log.d(this.getClass().getSimpleName(),"Starting MyFirebaseInstanceIdService");
        startService(intent);

        textView.append(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.append("Official Timetables Android Client for students and faculty of GBU\n\n");
        textView.append("Source code:\n");

        String url = "<a href=\"https://github.com/Varun-garg/gbu-timetable_sql\">https://github.com/Varun-garg/gbu-timetable_sql</a>";
        textView.append(Html.fromHtml(url));
        textView.append("\n\n");
        textView.append("Developed under guidance of Dr. Amit K. Awasthi\n\n");
        textView.append("Developed by Varun Garg <");
        textView.append(Html.fromHtml("<a href=\"mailto:varun.10@live.com\">Email Varun</a>><br />"));
        textView.append("Share Timetable feature by Ritik Channa\n <");
        textView.append(Html.fromHtml("<a href=\"mailto:chnritik@gmail.com\">Email Ritik</a>><br />"));
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