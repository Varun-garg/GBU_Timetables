package com.varun.gbu_timetables;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.varun.gbu_timetables.data.CSF;
import com.varun.gbu_timetables.data.FetchDbTask;
import com.varun.gbu_timetables.data.TimetableDbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimetableFragmentPager extends Fragment {

    static String LOG_TAG = "TimeTableActivityFragmentPager";
    String[] day_names = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    String title;
    String type;
    TableLayout tableLayout;
    public TimetableFragmentPager() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        title = getActivity().getIntent().getExtras().getString("Timetable_title");
        getActivity().setTitle(title);

        View rootView = inflater.inflate(R.layout.fragment_timetable_pager, container, false);
        HashMap<Long, CSF> CSF_Details = new HashMap();

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.timetable_pager);
        TimetablePagerAdapter timetablePagerAdapter = new TimetablePagerAdapter(getContext());
        if(viewPager == null)
            Log.d("view pager","null");
        else
            Log.d("view pager", viewPager.toString());

        if(timetablePagerAdapter == null)
            Log.d("adapter","null");
        else
            Log.d("adapter", timetablePagerAdapter.toString());


        viewPager.setAdapter(timetablePagerAdapter);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }


    public class TimetablePagerAdapter extends PagerAdapter
    {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<Integer> days;
        ArrayList<Integer> periods;
        Long id;
        TimetableAdapter timetableAdapter;
        public TimetablePagerAdapter(Context context)
        {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            days = new ArrayList<>();
            periods = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                days.add(i);
            }
            for (int i = 1; i <= 9; i++) {
                periods.add(i);
            }

            type = getActivity().getIntent().getExtras().getString("Type");
            id = null;
            if(type.equals("Section"))
            {
                id = getActivity().getIntent().getExtras().getLong("Section_id");
            }
            else if (type.equals("Faculty"))
            {
                id = getActivity().getIntent().getExtras().getLong("Faculty_id");
            }
            timetableAdapter = new TimetableAdapter(getContext(), days, id,type,periods,title);

        }

        public int getCount()
        {
            return day_names.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View parent_view = mLayoutInflater.inflate(R.layout.timetable_page_day, container, false);
            LinearLayout linearLayout = (LinearLayout)parent_view.findViewById(R.id.linear_layout);
            int beg_hr = 8;
            int beg_min = 30;

            for(int j = 0; j<periods.size();j++)
            {
                LinearLayout item_view = (LinearLayout) mLayoutInflater.inflate(R.layout.pager_item_row, container, false);
                LinearLayout item = (LinearLayout) timetableAdapter.getView(position,j);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                item_view.addView(item, layoutParams);
              
                TextView textView = (TextView) item_view.findViewById(R.id.pager_item_row);
                textView.setText(Integer.toString(beg_hr)+":"+ Integer.toString(beg_min) + " - ");
                beg_hr++;
                if(beg_hr == 13) beg_hr = 1;
                textView.append(Integer.toString(beg_hr) + ":" + Integer.toString(beg_min));
                textView.setBackgroundResource(R.drawable.back);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setPadding(10,0,0,0);

                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20,20,20,0);
                linearLayout.addView(item_view,layoutParams);


            }
            container.addView(parent_view);
            return parent_view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            ((ViewPager) container).removeView((View) view);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return day_names[position];
        }

    }


}
