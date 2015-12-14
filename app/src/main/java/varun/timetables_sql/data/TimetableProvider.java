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
public class TimetableProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TimetableDbHelper mOpenHeler;

    static final int CELL_BY_SECTION_DAY_SLOT = 100;
    static final int CELL_BY_FACULTY_DAY_SLOT = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TimetableContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_SECTION + "/*", CELL_BY_SECTION_DAY_SLOT);
        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_FACULTY + "/*", CELL_BY_FACULTY_DAY_SLOT);
        return matcher;
    }

    public boolean onCreate() {
        mOpenHeler = new TimetableDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CELL_BY_SECTION_DAY_SLOT:
                return TimetableContract.TT_CELL_TYPE;
            case CELL_BY_FACULTY_DAY_SLOT:
                return TimetableContract.TT_CELL_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getTTCellBySectionDaySlot(Uri uri) {
        Long section_id = TimetableContract.getSectionFromUri(uri);
        Long day = TimetableContract.getDayFromUri(uri);
        Long slot = TimetableContract.getSlotFromUri(uri);

        String query = "SELECT ContGroupCode, CSF_Id,Room_Id, Batch_Id,ActivityTag FROM M_Time_Table Where Section_Id=" + section_id.toString() + " AND  TT_Day=" + day.toString() + " AND TT_Period=" + slot.toString();

        return mOpenHeler.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getTTCellByFacultyDaySlot(Uri uri) {
        Long faculty_id = TimetableContract.getFacultyFromUri(uri);
        Long day = TimetableContract.getDayFromUri(uri);
        Long slot = TimetableContract.getSlotFromUri(uri);

        String query = "SELECT ContGroupCode, M_Time_Table.CSF_Id,Room_Id, Batch_Id,ActivityTag FROM M_Time_Table " +
                " JOIN CSF_Faculty on M_Time_Table.CSF_Id=CSF_Faculty.csf_id Where faculty_Id=" + faculty_id + " AND  TT_Day=" + day + " AND TT_Period=" + slot;

        return mOpenHeler.getReadableDatabase().rawQuery(query, null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] SelectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CELL_BY_FACULTY_DAY_SLOT:
                retCursor = getTTCellByFacultyDaySlot(uri);
                break;
            case CELL_BY_SECTION_DAY_SLOT:
                retCursor = getTTCellBySectionDaySlot(uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {//TODO
        return 1;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) //TODO
    {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) { //TODO
        return 1;
    }
}