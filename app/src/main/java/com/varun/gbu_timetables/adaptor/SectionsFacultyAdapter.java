package com.varun.gbu_timetables.adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.varun.gbu_timetables.R;
import com.varun.gbu_timetables.Utility;

import java.util.HashMap;
import java.util.List;

/*
Our ExpandableListAdapter is based on http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
*/

public class SectionsFacultyAdapter extends BaseExpandableListAdapter {


    Drawable GroupIconDrawable;
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<Common_type>> _listDataChild;

    public SectionsFacultyAdapter(Context context, List<String> listDataHeader,
                                  HashMap<String, List<Common_type>> listChildData) {
        this._context = context;

        java.util.Collections.sort(listDataHeader); //sort schools alphabetically

        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        GroupIconDrawable = Utility.ThemeTools.getListGroupIconInverseDrawable(context);
    }

    @Override
    public Common_type getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Common_type childSection = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_children, null);
        }

        TextView txtListChild = convertView
                .findViewById(R.id.list_child_tv);


        txtListChild.setText(childSection.Name);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = Utility.getSchool((String) getGroup(groupPosition));
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_item, null);

            ImageView GroupIcon = convertView.findViewById(R.id.image_view);
            GroupIcon.setImageDrawable(GroupIconDrawable);
        }

        TextView lblListHeader = convertView
                .findViewById(R.id.list_group_tv);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public static class Common_type {
        public Long id;
        public String Name;
    }
}