package com.varun.gbu_timetables;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.varun.gbu_timetables.service.MyFirebaseMessagingService;

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

        textView.setText(HtmlCompat.fromHtml("<h2><b>GBU Timetables</b></h2>", 0));
        //PackageInfo info;

        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        String versionName = BuildConfig.VERSION_NAME;

        //String BuildInfo =

//        try {
//            info = getPackageManager().getPackageInfo(getPackageName(), 0);
//
//            //PackageInfoCompat.getLongVersionCode(info);
//
//            int build = 0; // info.versionCode;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                build = (int) info.getLongVersionCode();
//            } else {
//                build = info.versionCode;
//            }
//            String name = info.versionName;
//            BuildInfo = "Version " + name + "\n Build number " + build + "\n\n";
//        } catch (Exception e) {
//            Log.d("error", e.toString());
//        }
        final String FinalBuildInfo = "Version " + versionName + "\n Build number " + versionCode + "\n\n";
        //BuildInfo;

        SpannableString ss = new SpannableString(FinalBuildInfo);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Toast.makeText(getApplicationContext(), "Copied id to clipboard.", Toast.LENGTH_LONG).show();
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    System.out.println("Fetching FCM registration token failed");
                                    return;
                                }
                                // Get new FCM registration token
                                String token = task.getResult();
                                Log.i("Firebase Token", token);
                                Toast.makeText(getApplicationContext(), "Firebase Token: " + token, Toast.LENGTH_SHORT).show();
                                Utility.setClipboard(getApplicationContext(), token);
                            }
                        });

                // Utility.setClipboard(getApplicationContext(), Utility.getFirebaseInstanceId(getApplicationContext()));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, FinalBuildInfo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MyFirebaseMessagingService myFMS = new MyFirebaseMessagingService();
        //MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();
        Intent intent = new Intent(getApplicationContext(), myFMS.getClass());
        //Intent intent = new Intent(getApplicationContext(), myFirebaseInstanceIdService.getClass());
        //   Log.d(this.getClass().getSimpleName(),"Starting MyFirebaseInstanceIdService");
        startService(intent);

        textView.append(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.append("Official Timetables Android Client for students and faculty of GBU\n\n");
        textView.append("Source code:\n");

        String url = "<a href=\"https://github.com/Varun-garg/gbu-timetable_sql\">https://github.com/Varun-garg/gbu-timetable_sql</a>";
        textView.append(HtmlCompat.fromHtml(url, 0));
        textView.append("\n\n");
        textView.append("Developed under guidance of Dr. Amit K. Awasthi\n\n");
        textView.append("Developed by Varun Garg <");
        textView.append(HtmlCompat.fromHtml("<a href=\"mailto:varun.10@live.com\">Email Varun</a>><br />", 0));
        textView.append("Share Timetable feature by Ritik Channa\n <");
        textView.append(HtmlCompat.fromHtml("<a href=\"mailto:chnritik@gmail.com\">Email Ritik</a>><br />", 0));
        textView.append("\nThanks to CyanogenMod for MD5 library\n\n");
        textView.append("Released Under GPLv3 Licence");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}