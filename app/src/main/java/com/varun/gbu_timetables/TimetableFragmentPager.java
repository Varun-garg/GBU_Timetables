package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varun.gbu_timetables.adaptor.TimetableAdapter;
import com.varun.gbu_timetables.data.Model.CSF;
import com.varun.gbu_timetables.data.Model.CSF_FAC_MAP_KEY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimetableFragmentPager extends Fragment {

    String[] day_names = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    String title;
    String type;
    HashMap<CSF_FAC_MAP_KEY, CSF> CSF_Details;
    ProgressDialog dialog;
    int max_period, min_period;

    public TimetableFragmentPager() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        title = getActivity().getIntent().getExtras().getString("Timetable_title");
        getActivity().setTitle(title);

        dialog = new ProgressDialog(getContext(), Utility.ThemeTools.getDialogThemeId(getContext()));
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        View rootView = inflater.inflate(R.layout.fragment_timetable_pager, container, false);

        AppBarLayout appBarLayout = getActivity().findViewById(R.id.appbar_layout);
        ViewPager viewPager = rootView.findViewById(R.id.timetable_pager);
        TimetablePagerAdapter timetablePagerAdapter = new TimetablePagerAdapter(getContext());

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1) day = 6; //bring sunday to last
        else day -= 2;

        viewPager.setAdapter(timetablePagerAdapter);

        TabLayout tabLayout = (TabLayout) inflater.inflate(R.layout.tab_layout, null);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(day);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        appBarLayout.addView(tabLayout, layoutParams);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog.hide();
    }

    public class TimetablePagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<Integer> days;
        ArrayList<Integer> periods;
        Long id;
        TimetableAdapter timetableAdapter;
        int BgBoxDefault_id;
        int margin_id;

        public TimetablePagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            days = new ArrayList<>();
            periods = new ArrayList<>();
            BgBoxDefault_id = Utility.ThemeTools.BackgroundIcons.getBgBoxDefaultDrawable(context);
            margin_id = Utility.ThemeTools.getMarginDrawable(context);
            for (int i = 1; i <= 7; i++) {
                days.add(i);
            }

            type = getActivity().getIntent().getExtras().getString("Type");
            id = null;
            if (type.equals("Section")) {
                id = getActivity().getIntent().getExtras().getLong("Section_id");
            } else if (type.equals("Faculty")) {
                id = getActivity().getIntent().getExtras().getLong("Faculty_id");
            }
            timetableAdapter = new TimetableAdapter(getContext(), days, id, type, title);
            CSF_Details = timetableAdapter.getCSFDetails();
            max_period = (int) timetableAdapter.getMaxPeriods();
            min_period = (int) timetableAdapter.getMinPeriods();
        }

        public int getCount() {
            return day_names.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View parent_view = mLayoutInflater.inflate(R.layout.timetable_page_day, container, false);
            final LinearLayout linearLayout = parent_view.findViewById(R.id.linear_layout);
            int beg_min = 30;
            final ArrayList<LinearLayout> items_list = new ArrayList<>();
            String prev_time_string = "";
            int item_pos = -1;
            int repeat = 1;
            for (int j = min_period; j <= max_period; j++) {
                LinearLayout item = (LinearLayout) timetableAdapter.getView(position, j);
                HashSet<CSF_FAC_MAP_KEY> key_hashmap = (HashSet) item.getTag(R.string.current_csf_fac_key_list);
                ArrayList<CSF_FAC_MAP_KEY> keys = new ArrayList<>(key_hashmap);
                String time_string = (String) item.getTag(R.string.time_string);
                if (time_string.length() < 1) {
                    repeat = 0;
                    prev_time_string = null;
                    continue;
                }
                if (time_string.equals(prev_time_string)) {
                    repeat++;
                    LinearLayout cur_item = items_list.get(item_pos);
                    TextView textView = cur_item.findViewById(R.id.pager_item_row);
                    textView.setText(Integer.toString(Utility.getPeriodTitleNo(j - repeat)) + ":" + Integer.toString(beg_min) + " - ");
                    textView.append(Integer.toString(Utility.getPeriodTitleNo(j + 1)) + ":" + Integer.toString(beg_min));
                    textView.append(" (" + Integer.toString(repeat + 1) + " Hrs)");
                    continue;
                }
                repeat = 0;
                prev_time_string = time_string;
                item_pos++;
                final int final_item_pos = item_pos;
                final LinearLayout item_view = (LinearLayout) mLayoutInflater.inflate(R.layout.pager_item_row, container, false);
                items_list.add(item_view);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                item_view.addView(item, layoutParams);

                TextView textView = item_view.findViewById(R.id.pager_item_row);
                textView.setText(Integer.toString(Utility.getPeriodTitleNo(j)) + ":" + Integer.toString(beg_min) + " - ");
                textView.append(Integer.toString(Utility.getPeriodTitleNo(j + 1)) + ":" + Integer.toString(beg_min));
                textView.setBackgroundResource(BgBoxDefault_id);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setPadding(0, 15, 0, 15);

                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 20, 20, 0);
                linearLayout.addView(item_view, layoutParams);

                final ArrayList<CSF> current_csf_list = new ArrayList<>();
                if (keys != null)
                    for (int i = 0; i < keys.size(); i++) {
                        current_csf_list.add(CSF_Details.get(keys.get(i)));
                    }

                final DetailsAdapter detailsAdapter = new DetailsAdapter(getContext(), current_csf_list, type);
                final LinearLayout footer = parent_view.findViewById(R.id.footer_ll);
                footer.setPadding(0, 20, 0, 0);

                item_view.setOnClickListener(new View.OnClickListener() {
                    LinearLayout.LayoutParams item_layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    @Override
                    public void onClick(View v) {
                        footer.removeAllViews();
                        for (int i = 0; i < current_csf_list.size(); i++) {
                            final View detail_item = detailsAdapter.getView(i, null, null);
                            detail_item.setPadding(20, 0, 20, 0);
                            detail_item.setBackgroundResource(BgBoxDefault_id);
                            detail_item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CSF csf = (CSF) detail_item.getTag();
                                    Intent intent = new Intent(getActivity(), TimetableActivity.class);

                                    if (type.equals("Faculty")) {
                                        dialog.setMessage("Loading " + csf.Section_name);
                                        intent.putExtra("Section_id", csf.Section_id);
                                        intent.putExtra("Timetable_title", csf.Section_name);
                                        intent.putExtra("Type", "Section");
                                    } else if (type.equals("Section")) {
                                        dialog.setMessage("Loading " + csf.Fac_name);
                                        intent.putExtra("Faculty_id", csf.Fac_id);
                                        intent.putExtra("Timetable_title", csf.Fac_name);
                                        intent.putExtra("Type", "Faculty");
                                    }
                                    dialog.show();
                                    startActivity(intent);
                                }
                            });
                            footer.addView(detail_item, item_layoutParams);
                        }
                        item_view.setBackgroundResource(margin_id);
                        item_view.setPadding(6, 6, 6, 6);

                        for (int i = 0; i < items_list.size(); i++) //remove border from other rows
                        {
                            if (i == final_item_pos) continue;
                            LinearLayout cur_item = items_list.get(i);
                            cur_item.setBackgroundResource(0);
                            cur_item.setPadding(0, 0, 0, 0);
                        }
                    }
                });

                if (item_pos == 0) //first item , initially item_pos = -1 but it is incremented before it is reaches here first time
                {
                    item_view.performClick();//select first item by default in UI by "clicking" it
                }
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
            container.removeView((View) view);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return day_names[position];
        }
    }
}
