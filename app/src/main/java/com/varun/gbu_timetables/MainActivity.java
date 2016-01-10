package com.varun.gbu_timetables;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.varun.gbu_timetables.data.FetchDbTask;
import com.varun.gbu_timetables.data.UpdateService;

public class MainActivity extends AppCompatActivity {
    public static String PACKAGE_NAME;
    FetchDbTask fetchDbTask;
    int set_theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int saved_theme = Utility.getThemeId(getApplicationContext());
        set_theme = R.style.AppTheme;
        if (set_theme != saved_theme)
            setTheme(saved_theme);
        set_theme = saved_theme;

        PACKAGE_NAME = getApplicationContext().getPackageName();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));
        viewPager.setCurrentItem(2);
        tabLayout.setupWithViewPager(viewPager);

        fetchDbTask = new FetchDbTask(getApplicationContext(), false);
        fetchDbTask.execute();

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

        int saved_theme = Utility.getThemeId(getApplicationContext());
        if (set_theme != saved_theme) {
            setTheme(saved_theme);
            set_theme = saved_theme;
            recreate();
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
            if (fetchDbTask.getStatus() == AsyncTask.Status.RUNNING) {
                Toast toast = Toast.makeText(getApplicationContext(), "Refresh is already going on", Toast.LENGTH_LONG);
                toast.show();
            } else if (fetchDbTask.getStatus() == AsyncTask.Status.FINISHED) {
                fetchDbTask = new FetchDbTask(getApplicationContext(), false);
                fetchDbTask.execute();
            } else {
                fetchDbTask.execute();
                fetchDbTask = null;
            }
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_info) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        startService();
        super.onDestroy();
    }

    // Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), UpdateService.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), UpdateService.class));
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[]{"Programs", "Faculty", "Favourites"};
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
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
