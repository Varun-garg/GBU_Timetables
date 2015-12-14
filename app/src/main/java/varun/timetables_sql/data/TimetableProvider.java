package varun.timetables_sql.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * varun.timetables_sql.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 12/14/2015 7:44 PM.
 */
public class TimetableProvider extends ContentProvider
{
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TimetableDbHelper mOpenHeler;

    static final int CELL_BY_SECTION_DAY_SLOT = 100;
    static final int CELL_BY_FACULTY_DAY_SLOT = 101;

    static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TimetableContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_SECTION,CELL_BY_SECTION_DAY_SLOT);
        matcher.addURI(authority,TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_FACULTY,CELL_BY_FACULTY_DAY_SLOT);
    }

    public boolean onCreate()
    {
        mOpenHeler = new TimetableDbHelper(getContext());
        return true;
    }
}
