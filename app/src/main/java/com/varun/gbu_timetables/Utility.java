package com.varun.gbu_timetables;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
            return R.drawable.back_light;
        else
            return R.drawable.back_dark;
    }

    public static int getPinkDrawable(Context context) {

        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return R.drawable.pink_light;
        else
            return R.drawable.pink_dark;
    }


    public static int getGreenDrawable(Context context) {

        int theme_id = getThemeId(context);
        if (theme_id == R.style.LightTheme)
            return R.drawable.green_light;
        else
            return R.drawable.green_dark;
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

    public static String getFullSectionName(String SectionCode)
    {
        String splitted[] = SectionCode.split("-");
        String Year = null;
        if (splitted.length >= 2)
            Year = " (" + splitted[1] + ")";

        switch (splitted[0])
        {
            case "CS":
                return "B.Tech Computer Science" + Year;
            case "EC":
                return "B.Tech Electronics and Communication" + Year;
            case "CE":
                return "B.Tech Civil" + Year;
            case "ME":
                return "B.Tech Mechanical" + Year;
            case "EE":
                return "B.Tech Electrical" + Year;
            case "MAM":
                return "B.Tech M.Sc. (Applied Mathematics)" + Year;
            case "MAP":
                return "M.Sc. (Applied Physics)" + Year;
            case "MAC":
                return "M.Sc. (Applied Chemistry)" + Year;
            case "MFS":
                return "M.Sc. (Food Science)" + Year;
            case "MES":
                return "M.Sc. (Environmental Sciences)" + Year;
            case "FT":
                return "Food Tech and Proc" + Year;
            case "BT":
                return "Biotechnology (B.Tech)" + Year;
            case "MTF":
                return "M.Tech. (Food Technology)" + Year;
            case "MTV":
                return "M.Tech. (VLSI Design)" + Year;
            case "MTW":
                return "M.Tech. (Wireless Comm.)" + Year;
            case "MTS":
                return "M.Tech. (Software)" + Year;
            case "MTCS":
                return "M.Tech. (Computer Science)" + Year;
            case "MTI":
                return "M.Tech. (Intelligent ..?)" + Year;
            case "MABS":
                return "M.A. in Buddhist Studies and Civilization" + Year;
            case "MBS":
                return "M.Phil in Buddhist Studies and Civilization" + Year;
            case "PBSC":
                return "Ph.D. in Buddhist Studies and Civilization" + Year;
            case "Ph.D.":
                return "Ph.D. Applied Science" + Year;
            case "MTI.":
                return "M.Tech. Industrial Engineering and Mgmt" + Year;
            case "MTB2":
                return "M. Tech. (Biotechnology) (2 Years Program)" + Year;
            case "MTB3.":
                return "M. Tech. (Biotechnology) (3 Years Program)" + Year;
            case "MAE":
                return "M.A. (Economics Planning and Development)" + Year;
            case "BALLB":
                return "BA LLB" + Year;
            case "MSW":
                return "Masters in Social Works" + Year;
            case "PhDICT.":
                return "Ph.D. in ICT" + Year;
            case "MTP":
                return "M.Tech (Power System)" + Year;
            case "IntMBA":
                return "Integrated MBA" + Year;
            case "MBA":
                return "MBA" + Year;
            case "MTD":
                return "M.Tech (Design)" + Year;
            case "MTT":
                return "M.Tech (Thermal)" + Year;
            case "PhDBT":
                return "Ph.D. Biotechnology" + Year;
            case "MTE":
                return "M.Tech. (Embeded Systems)" + Year;
            case "PhDMBA":
                return "Ph.D. in Management" + Year;
            case "MAAP":
                return "M.A. (Applied Psychology)" + Year;
            case "MCP":
                return "M.Phil. (Clinical Psychology)" + Year;
            case "MTPED":
                return "M.Tech. (Power Electronics and Drives)" + Year;
            case "MTM":
                return "M.Tech. (Manufacturing)" + Year;
            case "MTST":
                return "M.Tech. (Structural)" + Year;
            case "MTEN":
                return "M.Tech. (Environmental)" + Year;
            case "BARC":
                return "B.Tech Architecture " + Year;
            case "MURP":
                return "Masters in Urban and Regional Planning" + Year;
            case "BBLB":
                return "BBA LLB" + Year;

            default:
                return SectionCode;
        }
    }
}
