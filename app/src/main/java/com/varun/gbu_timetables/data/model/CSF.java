package com.varun.gbu_timetables.data.model;

import android.content.Context;

/**
 * Created by varun on 12/28/15.
 */
public class CSF {

    public Long CSF_Id;
    public Long Fac_id; //click and call
    public String Fac_abbr, Fac_name;
    public String Sub_Code, Sub_name;
    public Long Section_id;
    public String Section_name;
    // public String Meet_link;

    public CSF() {
    }

    public CSF(Long CSF_Id, Context context) {
        this.CSF_Id = CSF_Id;
    }
}
