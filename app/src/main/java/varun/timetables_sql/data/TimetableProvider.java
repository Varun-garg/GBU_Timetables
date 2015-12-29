package varun.timetables_sql.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * varun.timetables_sql.data (Timetables_sql)
 * Created by Varun garg <varun.10@live.com> on 12/14/2015 7:44 PM.
 */
public class TimetableProvider extends ContentProvider {
    static final int CELL_BY_SECTION_DAY_SLOT = 100;
    static final int CELL_BY_FACULTY_DAY_SLOT = 101;
    static final int SUBJECT_BY_CSF = 102;
    static final int FACULTY_BY_CSF = 103;
    static final int ROOM_BY_ID = 104;
    static final int SCHOOLS = 105;
    static final int SECTIONS = 106;
    static final UriMatcher sUriMatcher = buildUriMatcher();
    private TimetableDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TimetableContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_SECTION + "/*", CELL_BY_SECTION_DAY_SLOT);
        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_FACULTY + "/*", CELL_BY_FACULTY_DAY_SLOT);
        matcher.addURI(authority, TimetableContract.PATH_FACULTY + "/" + TimetableContract.PATH_CSF + "/*", FACULTY_BY_CSF);
        matcher.addURI(authority, TimetableContract.PATH_SUBJECT + "/" + TimetableContract.PATH_CSF + "/*", SUBJECT_BY_CSF);
        matcher.addURI(authority, TimetableContract.PATH_ROOM + "/*", ROOM_BY_ID);
        matcher.addURI(authority, TimetableContract.PATH_SCHOOL,SCHOOLS);
        matcher.addURI(authority, TimetableContract.PATH_SECTION + "/*",SECTIONS);

        return matcher;
    }

    public boolean onCreate() {
        mOpenHelper = new TimetableDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) { //TODO Improve return type
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CELL_BY_SECTION_DAY_SLOT:
                return TimetableContract.TT_CELL_TYPE;
            case CELL_BY_FACULTY_DAY_SLOT:
                return TimetableContract.TT_CELL_TYPE;
            case FACULTY_BY_CSF:
                return TimetableContract.TT_CELL_TYPE;
            case ROOM_BY_ID:
                return TimetableContract.TT_CELL_TYPE;
            case SUBJECT_BY_CSF:
                return TimetableContract.TT_CELL_TYPE;
            case SCHOOLS:
                return TimetableContract.TT_CELL_TYPE;
            case SECTIONS:
                return TimetableContract.TT_CELL_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getTTCellBySectionDaySlot(Uri uri) {
        Long section_id = TimetableContract.getSectionFromUri(uri);
        Long day = TimetableContract.getDayFromUri(uri);
        Long slot = TimetableContract.getSlotFromUri(uri);

        String query = "SELECT _ROWID_ as _id, ContGroupCode, CSF_Id,Room_Id, Batch_Id,ActivityTag FROM M_Time_Table Where Section_Id=" + section_id.toString() + " AND  TT_Day=" + day.toString() + " AND TT_Period=" + slot.toString();

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }


    private Cursor getSchools(Uri uri) {

        String query = "SELECT _ROWID_ as _id,id as program_id, school from Program";

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getSections(Uri uri)
    {
        Long program_id = TimetableContract.getProgramFromUri(uri);
        String query = "SELECT _ROWID_ as _id,id as section_id,Name from Section where ShowTimetable = 1 and  program = " + program_id;
        return mOpenHelper.getReadableDatabase().rawQuery(query, null);

    }

    private Cursor getTTCellByFacultyDaySlot(Uri uri) {
        Long faculty_id = TimetableContract.getFacultyFromUri(uri);
        Long day = TimetableContract.getDayFromUri(uri);
        Long slot = TimetableContract.getSlotFromUri(uri);

        String query = "SELECT _ROWID_ as _id, ContGroupCode, M_Time_Table.CSF_Id,Room_Id, Batch_Id,ActivityTag FROM M_Time_Table " +
                " JOIN CSF_Faculty on M_Time_Table.CSF_Id=CSF_Faculty.csf_id Where faculty_Id=" + faculty_id + " AND  TT_Day=" + day + " AND TT_Period=" + slot;

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getFacultyByCSF(Uri uri) {
        Long CSF = TimetableContract.getCSFfromUri(uri);

        String query = "SELECT Teacher._ROWID_ as _id,* FROM Teacher,CSF_Faculty where faculty_id=Teacher.id and csf_id=" + CSF;
        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getSubjectByCSF(Uri uri) {
        Long CSF = TimetableContract.getCSFfromUri(uri);
        String query = "SELECT _ROWID_ as _id,Subject_Code FROM CSF Where [CSF_Id]=" + CSF;
        Cursor cursor = mOpenHelper.getReadableDatabase().rawQuery(query, null);
        cursor.moveToNext();
        String code = cursor.getString(cursor.getColumnIndex("Subject_Code")).trim();
        String sub_query = "SELECT _ROWID_ as _id,code,name FROM Subject Where code= '" + code + "'";
        return mOpenHelper.getReadableDatabase().rawQuery(sub_query, null);

    }

    private Cursor getRoomById(Uri uri) {
        Long room_id = TimetableContract.getRoomFromUri(uri);
        String query = "SELECT _ROWID_ as _id, name FROM M_Room where room_id=" + room_id;
        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
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
            case FACULTY_BY_CSF:
                retCursor = getFacultyByCSF(uri);
                break;
            case SUBJECT_BY_CSF:
                retCursor = getSubjectByCSF(uri);
                break;
            case ROOM_BY_ID:
                retCursor = getRoomById(uri);
                break;
            case SCHOOLS:
                retCursor = getSchools(uri);
                break;
            case SECTIONS:
                retCursor = getSections(uri);
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

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}