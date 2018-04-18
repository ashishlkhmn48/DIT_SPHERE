package com.ashishlakhmani.dit_sphere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.FacultyData;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomExpandableAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<String> departmentList;
    HashMap<String, ArrayList<FacultyData>> map;

    public CustomExpandableAdapter(Context context, ArrayList<String> departmentList, HashMap<String, ArrayList<FacultyData>> map) {
        this.context = context;
        this.departmentList = departmentList;
        this.map = map;
    }

    @Override
    public FacultyData getChild(int groupPosition, int childPosititon) {
        return this.map.get(this.departmentList.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_child_items, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.name);

        txtListChild.setText(childText.toUpperCase());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.map.get(this.departmentList.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.departmentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.departmentList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.layout_group_items, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.department);
        lblListHeader.setText(headerTitle.toUpperCase());

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
}
