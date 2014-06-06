package com.varun.gbu_timetable_sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CommentsDataSource {

	// Database fields
	private static SQLiteDatabase database;
	private static MySQLiteHelper dbHelper;
	private static String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_COMMENT };
	private static String condition = "0 = 0";

	public CommentsDataSource(Context context) {
		try {
			dbHelper = new MySQLiteHelper(context);
		} catch (IOException e) {
			throw new Error("Error ");
		}
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Comment createComment(String comment) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
		long insertId = database.insert(MySQLiteHelper.TABLE_section, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_section,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Comment newComment = cursorToComment(cursor);
		cursor.close();
		return newComment;
	}

	public void deleteComment(Comment comment) {
		long id = comment.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_section, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Comment> getAllComments() {
		List<Comment> comments = new ArrayList<Comment>();

		Cursor cursor = database.query(true, MySQLiteHelper.TABLE_section,
				allColumns, condition, null, null, null, null, null);

		// database.query(false, MySQLiteHelper.TABLE_section, allColumns,
		// condition, null, null, null, null, null)

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Comment comment = cursorToComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}

	public void updatestuff(String tablename, String column, String id,
			String condi) {
		MySQLiteHelper.TABLE_section = tablename;
		MySQLiteHelper.COLUMN_COMMENT = column;
		MySQLiteHelper.COLUMN_ID = id;
		allColumns[0] = MySQLiteHelper.COLUMN_ID;
		allColumns[1] = MySQLiteHelper.COLUMN_COMMENT;
		condition = condi;
	}

	private Comment cursorToComment(Cursor cursor) {
		Comment comment = new Comment();
		comment.setId(cursor.getLong(0));
		comment.setComment(cursor.getString(1));
		return comment;
	}

	public String get_str(int id, String col) {
		String condi = "Select * from " + MySQLiteHelper.TABLE_section
				+ " WHERE id = " + String.valueOf(id) + ";";
		return condi;
		// Cursor varun_c = database.rawQuery(condi, null);
		// varun_c.moveToFirst();
		// return varun_c.getString(varun_c.getColumnIndex(col));

	}
}