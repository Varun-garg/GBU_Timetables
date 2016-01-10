package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

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

    public static Drawable getFavYes(Context context)
    {
        int theme_id = getThemeId(context);
        if(theme_id == R.style.LightTheme)
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_white_24dp);
        else
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_black_24dp);
    }

    public static Drawable getFavNo(Context context)
    {
        int theme_id = getThemeId(context);
        if(theme_id == R.style.LightTheme)
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white_24dp);
        else
            return ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_black_24dp);
    }

    public static int getBgColor(int ThemeId, Context context) {
        TypedArray a;
        if (ThemeId == R.style.LightTheme)
            a = context.getTheme().obtainStyledAttributes(R.style.LightTheme, new int[]{R.attr.app_bg});
        else
            a = context.getTheme().obtainStyledAttributes(R.style.LightTheme, new int[]{R.attr.app_bg});

        return a.getResourceId(0, 0);
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
}
