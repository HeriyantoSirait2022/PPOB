package com.qdi.rajapay.payment;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HowToPayAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<JSONObject> listDataHeader;
    HashMap<JSONObject, ArrayList<JSONObject>> listChildData;

    public HowToPayAdapter(Context context, ArrayList<JSONObject> listDataHeader, HashMap<JSONObject, ArrayList<JSONObject>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listChildData = listChildData;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChildData.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChildData.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        JSONObject data = (JSONObject) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.how_to_pay_parent, null);
        }
        TextView title = convertView.findViewById(R.id.title);
        ImageView arrow  = convertView.findViewById(R.id.arrow);

        try {
            title.setText(data.getString("title"));
            if(data.getBoolean("selected")) {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.blue_completed));
                title.setTextColor(context.getResources().getColor(R.color.white));
                arrow.setImageDrawable(context.getDrawable(R.drawable.ic_keyboard_arrow_down_white_24));
            }
            else {
                convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
                title.setTextColor(context.getResources().getColor(R.color.black));
                arrow.setImageDrawable(context.getDrawable(R.drawable.ic_keyboard_arrow_right_black_24));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        JSONObject data = (JSONObject) getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.how_to_pay_child, null);
        }
        TextView title = convertView.findViewById(R.id.title);

        try {
            title.setText(Html.fromHtml(data.getString("title")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
