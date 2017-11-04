package com.varun.gbu_timetables.data.Database;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

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
    static final int SECTIONS_BY_PROGRAM_ID = 106;
    static final int FACULTY = 107;
    static final int SECTION_BY_ID = 108;
    static final int MAX_PERIOD_BY_SECTION = 109;
    static final int MAX_PERIOD_BY_FACULTY = 110;
    static final int FULL_SECTION_NAME = 111;

    static final UriMatcher sUriMatcher = buildUriMatcher();
    private TimetableDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TimetableContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_SECTION + "/" + TimetableContract.PATH_MAX + "/*", MAX_PERIOD_BY_SECTION);
        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_FACULTY + "/" + TimetableContract.PATH_MAX + "/*", MAX_PERIOD_BY_FACULTY);
        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_SECTION + "/*", CELL_BY_SECTION_DAY_SLOT);
        matcher.addURI(authority, TimetableContract.PATH_TIMETABLE + "/" + TimetableContract.PATH_FACULTY + "/*", CELL_BY_FACULTY_DAY_SLOT);
        matcher.addURI(authority, TimetableContract.PATH_FACULTY + "/" + TimetableContract.PATH_CSF + "/*", FACULTY_BY_CSF);
        matcher.addURI(authority, TimetableContract.PATH_SUBJECT + "/" + TimetableContract.PATH_CSF + "/*", SUBJECT_BY_CSF);
        matcher.addURI(authority, TimetableContract.PATH_ROOM + "/*", ROOM_BY_ID);
        matcher.addURI(authority, TimetableContract.PATH_SCHOOL, SCHOOLS);
        matcher.addURI(authority, TimetableContract.PATH_SECTION + "/" + TimetableContract.PATH_PROGRAM + "/*", SECTIONS_BY_PROGRAM_ID);
        matcher.addURI(authority, TimetableContract.PATH_FACULTY, FACULTY);
        matcher.addURI(authority, TimetableContract.PATH_SECTION + "/*", SECTION_BY_ID);
        matcher.addURI(authority, TimetableContract.PATH_FULL_SECTION_NAME, FULL_SECTION_NAME);

        return matcher;
    }

    public boolean onCreate() {
        mOpenHelper = new TimetableDbHelper(getContext());
        return true;
    }

    public void reloadDb() {
        mOpenHelper = new TimetableDbHelper(getContext());
        Log.d(this.getClass().getSimpleName(), "DB Reloaded");
    }

    @Override
    public String getType(Uri uri) { //TODO Improve return type
        return TimetableContract.TT_CELL_TYPE;
    }

    private Cursor getTTCellBySectionDaySlot(Uri uri) {
        Long section_id = TimetableContract.getSectionFromUri(uri);
        Long day = TimetableContract.getDayFromUri(uri);
        Long slot = TimetableContract.getSlotFromUri(uri);

        String query = "SELECT _ROWID_ as _id, ContGroupCode, CSF_Id,Room_Id, Batch_Id,ActivityTag FROM M_Time_Table Where Section_Id=" + section_id.toString() + " AND  TT_Day=" + day.toString() + " AND TT_Period=" + slot.toString();

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getMaxPeriodFromSection(Uri uri) {
        Long section_id = TimetableContract.getSectionFromMaxPeriodUri(uri);

        String query = "SELECT _ROWID_ as _id, max(TT_Period),min(TT_Period) from M_Time_Table where Section_Id = " + section_id.toString();

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getSchools() {

        String query = "SELECT _ROWID_ as _id,id as program_id, school from Program";

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }


    private Cursor getFaculty() {

        String query = "SELECT distinct Teacher._ROWID_ as _id,Teacher.id as faculty_id, Teacher.name,Teacher.school from Teacher,School,M_Time_Table,CSF_Faculty where Teacher.school = School.name " +
                " and M_Time_Table.CSF_Id=CSF_Faculty.csf_id and CSF_Faculty.faculty_Id = Teacher.id order by Teacher.name";

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getSectionsByProgramID(Uri uri) {
        Long program_id = TimetableContract.getProgramFromUri(uri);
        String query = "SELECT distinct Section._ROWID_ as _id,id as section_id,name from Section,M_Time_Table" +
                " where ShowTimetable = 1 and program = " + program_id + " and Section.id = M_Time_Table.Section_Id"
                + " order by Section.name ";

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }


    private Cursor getSectionById(Uri uri) {
        Long section_id = TimetableContract.getSectionFromUri(uri);
        String query = "SELECT _ROWID_ as _id,id as section_id,Name from Section where id = " + section_id;
        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }


    private Cursor getTTCellByFacultyDaySlot(Uri uri) {
        Long faculty_id = TimetableContract.getFacultyFromUri(uri);
        Long day = TimetableContract.getDayFromUri(uri);
        Long slot = TimetableContract.getSlotFromUri(uri);

        String query = "SELECT M_Time_Table._ROWID_ as _id, Section_Id, ContGroupCode, M_Time_Table.CSF_Id,Room_Id, Batch_Id,ActivityTag FROM M_Time_Table,CSF_Faculty " +
                " where M_Time_Table.CSF_Id=CSF_Faculty.csf_id and faculty_Id=" + faculty_id + " AND  TT_Day=" + day + " AND TT_Period=" + slot;

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getMaxPeriodFromFaculty(Uri uri) {
        Long faculty_id = TimetableContract.getFacultyFromMaxPeriodUri(uri);
        String query = "SELECT M_Time_Table._ROWID_ as _id, max(TT_Period),min(TT_Period) from M_Time_Table,CSF_Faculty where " +
                " M_Time_Table.CSF_Id=CSF_Faculty.csf_id and " +
                "faculty_Id = " + faculty_id.toString();
        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getFacultyByCSF(Uri uri) {
        Long CSF = TimetableContract.getCSFfromUri(uri);
        String query = "SELECT Teacher._ROWID_ as _id,Teacher.id as faculty_id,* FROM Teacher,CSF_Faculty where faculty_id=Teacher.id and csf_id=" + CSF;
        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getSubjectByCSF(Uri uri) {
        Long CSF = TimetableContract.getCSFfromUri(uri);
        String query = "SELECT _ROWID_ as _id,Subject_Code FROM CSF Where [CSF_Id]=" + CSF;
        Cursor cursor = mOpenHelper.getReadableDatabase().rawQuery(query, null);
        cursor.moveToNext();
        String code = cursor.getString(cursor.getColumnIndex("Subject_Code")).trim();
        cursor.close();
        String sub_query = "SELECT _ROWID_ as _id,code,name FROM Subject Where code like '%" + code + "%'";
        return mOpenHelper.getReadableDatabase().rawQuery(sub_query, null);
    }

    private Cursor getRoomById(Uri uri) {
        Long room_id = TimetableContract.getRoomFromUri(uri);
        String query = "SELECT _ROWID_ as _id, name FROM M_Room where room_id=" + room_id;
        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    private Cursor getFullSectionName(Uri uri) {
        String SectionCode = TimetableContract.getSectionCodeFromUri(uri);
        String query = "Select _ROWID_ as _id, Name FROM Program where code = '" + SectionCode + "'";
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
                retCursor = getSchools();
                break;
            case SECTIONS_BY_PROGRAM_ID:
                retCursor = getSectionsByProgramID(uri);
                break;
            case FACULTY:
                retCursor = getFaculty();
                break;
            case SECTION_BY_ID:
                retCursor = getSectionById(uri);
                break;
            case MAX_PERIOD_BY_FACULTY:
                retCursor = getMaxPeriodFromFaculty(uri);
                break;
            case MAX_PERIOD_BY_SECTION:
                retCursor = getMaxPeriodFromSection(uri);
                break;
            case FULL_SECTION_NAME:
                retCursor = getFullSectionName(uri);
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