package varun.timetables_sql.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by varun on 12/28/15.
 */
public class CSF {

    public Long CSF_Id;
    public int Fac_id; //click and call
    public String Fac_abbr, Fac_name;
    public String Sub_Code, Sub_name;
    public String Room_no;

    public CSF() {

    }

    public CSF(Long CSF_Id, Long Room_id, Context context) {
        this.CSF_Id = CSF_Id;
        Uri fac_uri = TimetableContract.BuildFacultyWithCSFid(CSF_Id);
        Cursor fac_cursor = context.getContentResolver().query(fac_uri, null, null, null, null);

        Uri sec_uri = TimetableContract.BuildSectionWithCSFid(CSF_Id);
        Cursor sec_cursor = context.getContentResolver().query(sec_uri, null, null, null, null);

        Uri room_uri = TimetableContract.BuildRoomWithId(Room_id);
        Cursor room_cursor = context.getContentResolver().query(room_uri, null, null, null, null);

        sec_cursor.moveToNext();
        room_cursor.moveToNext();
        fac_cursor.moveToNext();

        Fac_abbr = fac_cursor.getString(fac_cursor.getColumnIndex("abbr")).trim();
        Fac_id = fac_cursor.getInt(fac_cursor.getColumnIndex("_id"));
        Fac_name = fac_cursor.getString(fac_cursor.getColumnIndex("abbr")).trim();
        Sub_Code = sec_cursor.getString(sec_cursor.getColumnIndex("code")).trim();
        Sub_name = sec_cursor.getString(sec_cursor.getColumnIndex("name")).trim();
        Room_no = room_cursor.getString(room_cursor.getColumnIndex("Name")).trim();

        fac_cursor.close();
        sec_cursor.close();
        room_cursor.close();
    }
}
