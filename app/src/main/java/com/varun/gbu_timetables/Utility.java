package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.database.DatabaseUtilsCompat;
import android.util.Log;

import com.varun.gbu_timetables.data.Database.TimetableContract;

/**
 * com.varun.gbu_timetables (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 1/9/2016 11:24 AM.
 */
public class Utility {

    public static int getThemeId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String theme = sharedPreferences.getString(context.getString(R.string.pref_theme_key), "0");
        if (theme.equals("0"))
            return R.style.LightTheme;
        else
            return R.style.DarkTheme;
    }


    public static int getThemeId(String theme) {
        if (theme.equals("0"))
            return R.style.LightTheme;
        else
            return R.style.DarkTheme;
    }

    public static Drawable getFavYes(Context context) {
        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_white_24dp);
        else
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_black_24dp);
    }

    public static Drawable getFavNo(Context context) {
        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white_24dp);
        else
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_black_24dp);
    }

    public static int getBackDrawable(Context context) {
        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return R.drawable.bg_box_default_light;
        else
            return R.drawable.bg_box_default_dark;
    }

    public static int getPinkDrawable(Context context) {

        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return R.drawable.bg_box_pink_light;
        else
            return R.drawable.bg_box_pink_dark;
    }


    public static int getGreenDrawable(Context context) {

        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return R.drawable.bg_box_green_light;
        else
            return R.drawable.bg_box_green_dark;
    }

    public static int getMarginDrawable(Context context) {

        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return R.drawable.margin_light;
        else
            return R.drawable.margin_dark;
    }

    public static int getDialogThemeId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String theme = sharedPreferences.getString(context.getString(R.string.pref_theme_key), "0");
        if (theme.equals("0")) {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                return android.R.style.Theme_Material_Light_Dialog;
            } else {
                return ProgressDialog.THEME_HOLO_LIGHT;
            }
        } else if (android.os.Build.VERSION.SDK_INT >= 21) {
            return android.R.style.Theme_Material_Dialog;
        } else {
            return ProgressDialog.THEME_HOLO_DARK;
        }
    }


    public static String getSchool(String src) {
        if (src.equalsIgnoreCase("SOICT"))
            return "Information and Communication  Technology";
        else if (src.equalsIgnoreCase("SOVSAS"))
            return "Vocational Studies And Applied Sciences";
        else if (src.equalsIgnoreCase("SOBT"))
            return "Biotechnology";
        else if (src.equalsIgnoreCase("SOE"))
            return "Engineering";
        else if (src.equalsIgnoreCase("SOM"))
            return "Management";
        else if (src.equalsIgnoreCase("SOLJ"))
            return "Law, Justice and Governance";
        else if (src.equalsIgnoreCase("SOBSC"))
            return "Buddhist Studies And Civilization";
        else if (src.equalsIgnoreCase("SOHSS"))
            return "Humanities and Social Sciences";
        else
            return src;
    }

    public static int getPeriodTitleNo(int Period_no)
    {
        //for period 1, we have 8 (8:30)
        Period_no += 7;
        if(Period_no > 12) //12 hr clock
            Period_no -= 12;
        return Period_no;
    }

    public static String getFullSectionName(String SectionCode, Context context)
    {
        String splitted[] = SectionCode.split("-");
        String Year = null;
        if (splitted.length >= 2)
            Year = " (" + splitted[1] + ")";

        Uri uri = TimetableContract.BuildFullSectionName(splitted[0]);
        Cursor cursor = context.getContentResolver().query(uri,null, null, null, null);

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            cursor.close();
            return SectionCode;
        }

        try {

            cursor.moveToFirst();
            String SectionFullName = cursor.getString(cursor.getColumnIndex("Name"));
            cursor.close();
            return SectionFullName + Year;
        }
        catch (Exception e)
        {
            Log.d(Utility.class.getSimpleName(),e.toString());
            Log.d(Utility.class.getSimpleName(),"Dump :" + DatabaseUtils.dumpCursorToString(cursor));
            cursor.close();
            return SectionCode;
        }
    }
}
