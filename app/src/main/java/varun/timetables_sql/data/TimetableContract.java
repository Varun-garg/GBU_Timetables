package varun.timetables_sql.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import varun.timetables_sql.MainActivity;

/**
 * varun.timetables_sql.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 12/14/2015 7:55 PM.
 */
public class TimetableContract {
    public static String CONTENT_AUTHORITY = "varun.timetables_sql";

    public static String PATH_TIMETABLE = "tt";
    public static String PATH_SECTION = "section";
    public static String PATH_FACULTY = "faculty";
    public static String PATH_CSF = "CSF";
    public static String PATH_ROOM = "room";

    public static String PARAM_SLOT = "slot";
    public static String PARAM_DAY = "day";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final Uri TT_CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMETABLE).build();

    public static String TT_CELL_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_TIMETABLE;

    public static Uri BuildTTCellWithFacultyDaySlot(long fac_id, long day,long slot)
    {
        return TT_CONTENT_URI.buildUpon().appendPath(PATH_FACULTY).appendPath(Long.toString(fac_id))
                .appendQueryParameter(PARAM_DAY, Long.toString(day))
                .appendQueryParameter(PARAM_SLOT, Long.toString(slot)).build();
    }
    public static Uri BuildTTCellWithSectionDaySlot(long section_id, long day,long slot)
    {
        return TT_CONTENT_URI.buildUpon().appendPath(PATH_SECTION).appendPath(Long.toString(section_id))
                .appendQueryParameter(PARAM_DAY, Long.toString(day))
                .appendQueryParameter(PARAM_SLOT, Long.toString(slot)).build();
    }
    public static long getFacultyFromUri(Uri uri)
    {
        return Long.parseLong(uri.getPathSegments().get(2));
    }
    public static long getSectionFromUri(Uri uri)
    {
        return Long.parseLong(uri.getPathSegments().get(2));
    }
    public static long getSlotFromUri(Uri uri)
    {
        return Long.parseLong(uri.getQueryParameter(PARAM_SLOT));
    }

    public static long getDayFromUri(Uri uri)
    {
        return Long.parseLong(uri.getQueryParameter(PARAM_DAY));
    }

    public static final Uri FACULTY_CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_FACULTY).build();

    public static final Uri SECTION_CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECTION).build();

    public static final Uri ROOM_CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROOM).build();

    public static Uri BuildFacultyWithCSFid(long csf_id)
    {
        return FACULTY_CONTENT_URI.buildUpon().appendPath(PATH_CSF).appendPath(Long.toString(csf_id)).build();
    }
    public static Uri BuildSectionWithCSFid(long csf_id)
    {
        return SECTION_CONTENT_URI.buildUpon().appendPath(PATH_CSF).appendPath(Long.toString(csf_id)).build();
    }
    public static Uri BuildRoomWithId(long room_id)
    {
        return ROOM_CONTENT_URI.buildUpon().appendPath(Long.toString(room_id)).build();
    }

    public static Long getCSFfromUri(Uri uri)
    {
        return Long.parseLong(uri.getPathSegments().get(2));
    }

    public static Long getRoomFromUri(Uri uri)
    {
        return Long.parseLong(uri.getPathSegments().get(1));
    }
}
