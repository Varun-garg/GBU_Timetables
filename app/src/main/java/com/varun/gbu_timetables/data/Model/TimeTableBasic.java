package com.varun.gbu_timetables.data.Model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * com.varun.gbu_timetables.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 1/3/2016 5:11 PM.
 */
public class TimeTableBasic {

    @SerializedName(value = "Id", alternate = {"a"})
    // alternates a,b,c to fix recent regression issues
    private Long Id;

    @SerializedName(value = "Type", alternate = {"b"})
    private String Type;

    @SerializedName(value = "Title", alternate = {"c"})
    private String Title;

    public TimeTableBasic(Long Id, String Type, String Title) {
        this.Id = Id;
        this.Type = Type;
        this.Title = Title;
    }

    public TimeTableBasic() {

    }

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

    public long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

}
