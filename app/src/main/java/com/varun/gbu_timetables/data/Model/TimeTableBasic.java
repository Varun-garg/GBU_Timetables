package com.varun.gbu_timetables.data.Model;

import android.util.Log;

/**
 * com.varun.gbu_timetables.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 1/3/2016 5:11 PM.
 */
public class TimeTableBasic {

    public Long Id;
    public String Type;
    public String Title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeTableBasic)) return false;
        TimeTableBasic timeTableBasic = (TimeTableBasic) o;
        if (!Id.equals(timeTableBasic.Id)) return false;
        if (!Type.equals(timeTableBasic.Type)) return false;
        if (!Title.equals(timeTableBasic.Title)) return false;
        Log.d("this", "equals 0");
        return true;
    }


    @Override
    public int hashCode() {
        return Type.hashCode() + Id.hashCode();
    }
}
