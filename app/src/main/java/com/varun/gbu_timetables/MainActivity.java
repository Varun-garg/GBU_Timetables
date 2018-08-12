package com.varun.gbu_timetables;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varun.gbu_timetables.asyncTask.UpdateDatabaseOnlineTask;
import com.varun.gbu_timetables.data.Model.TimeTableBasic;
import com.varun.gbu_timetables.service.UpdateDatabaseService;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashSet;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static String ContentType_KEY = "NotificationContentType";
    public static String Content_KEY = "NotificationContent";
    UpdateDatabaseOnlineTask updateDatabaseOnlineTask;
    int set_theme;
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentPagerAdapter fragmentPagerAdapter;

    public static boolean checkFavouritesExist(Context context) {
        Gson gson = new Gson();
        String existing_TAG = "favourites";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(existing_TAG, null);

        Type favourites_type = new TypeToken<HashSet<TimeTableBasic>>() {
        }.getType(); //simply checking  string size doesn't work

        return json != null && json.length() > 0 &&
                ((HashSet<TimeTableBasic>) gson.fromJson(json, favourites_type)).size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        // Crashlytics.getInstance().crash();

        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        set_theme = R.style.AppTheme;
        if (set_theme != saved_theme)
            setTheme(saved_theme);
        set_theme = saved_theme;

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager());

        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(2); // Cache all pages = N-1

        if (checkFavouritesExist(getApplicationContext()) == true)
            viewPager.setCurrentItem(2); // open favourites
        else
            viewPager.setCurrentItem(0); // open sections

        tabLayout.setupWithViewPager(viewPager);

        updateDatabaseOnlineTask = new UpdateDatabaseOnlineTask(getApplicationContext(), false);
        updateDatabaseOnlineTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        if (set_theme != saved_theme) {
            setTheme(saved_theme);
            set_theme = saved_theme;
            recreate();
        }

        Bundle Extras = getIntent().getExtras();

        if (Extras != null) { //explore notifications

            String ContentType = Extras.getString(ContentType_KEY);
            String Content = Extras.getString(Content_KEY);

            if (ContentType != null && Content != null) {
                if (ContentType.equalsIgnoreCase("PlayStorePackage")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Content)));
                    } catch (android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Content)));
                    }
                } else if (ContentType.equalsIgnoreCase("BrowserUrl"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Content)));
                else if (ContentType.equalsIgnoreCase("MessageDialog")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage(Html.fromHtml(Content));
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            "Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = builder1.create();
                    alertDialog.show();
                }

                //Our Work is Done, Now let's clear our bundle to prevent onResume to print same
                // notifications each time app is opened

                getIntent().removeExtra(ContentType_KEY);
                getIntent().removeExtra(Content_KEY);

            }


        }

    }

    // Backwards compatible recreate().
    @Override
    public void recreate() {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            super.recreate();
        } else {
            startActivity(getIntent());
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if (updateDatabaseOnlineTask.getStatus() == AsyncTask.Status.RUNNING) {
                Toast toast = Toast.makeText(getApplicationContext(), "Refresh is already going on", Toast.LENGTH_LONG);
                toast.show();
            } else if (updateDatabaseOnlineTask.getStatus() == AsyncTask.Status.FINISHED) {
                updateDatabaseOnlineTask = new UpdateDatabaseOnlineTask(getApplicationContext(), false);
                updateDatabaseOnlineTask.execute();
            } else {
                updateDatabaseOnlineTask.execute();
                updateDatabaseOnlineTask = null;
            }
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_info) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_slack) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("Setup Slack");
            builder1.setMessage(Html.fromHtml("1. Go to play store and download slack. <br /><br />" +
            "2. Open slack -> sign in -> man -> Workspace url is <b>gbuhq</b> or <a>gbuhq.slack.com</a><br /><br />"+
            "3. Click <b>Launch</b>"));
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Launch",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("slack://channel?id=CC6J2UGF3&team=TBW575JA2"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder1.create();
            alertDialog.show();


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        // startService();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 13); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0,
                new Intent(getApplicationContext(), UpdateDatabaseService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        //just in case there is an existing pending intent
        am.cancel(pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
        super.onDestroy();
    }

    // Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), UpdateDatabaseService.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), UpdateDatabaseService.class));
    }

    public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[]{"Sections", "Faculty", "Favourites"};

        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new SectionsFragment();
            else if (position == 1)
                return new FacultyFragment();
            else return new FavouritesFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
