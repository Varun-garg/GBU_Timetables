package com.varun.gbu_timetable_sql;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestDatabaseActivity extends ListActivity {
	private CommentsDataSource datasource;
	private static int pos = 0;
	int section_id = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_database);

		datasource = new CommentsDataSource(this);
		datasource.open();

		List<Comment> values = datasource.getAllComments();

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		position++;
		// Toast.makeText(this, "message "+position+" "+program_name,
		// Toast.LENGTH_LONG).show();
		// Toast.makeText(this, "done", Toast.LENGTH_LONG).show();;

		if (pos == 0) {
			datasource.updatestuff("section", "name", "_id", " program = "
					+ position);
			onCreate(null);
			pos++;
		} else if (pos == 1) {
			section_id = position;
			datasource.updatestuff("M_Time_table", " TT_Day", "CSF_id",
					" section_id = " + position + " group by tt_day ");
			onCreate(null);
			pos++;
		} else if (pos == 2) {
			// we now have section_id && day.... n0w we have to find csf for
			// each period and display details
			pos++;
		}
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
}
