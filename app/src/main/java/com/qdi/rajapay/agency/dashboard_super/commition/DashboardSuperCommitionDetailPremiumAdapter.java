package com.qdi.rajapay.agency.dashboard_super.commition;

import android.content.Context;
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

public class DashboardSuperCommitionDetailPremiumAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<JSONObject> listDataHeader;
    HashMap<String, ArrayList<JSONObject>> listChildData;
    ClickListener clickListener;

    public DashboardSuperCommitionDetailPremiumAdapter(Context context, ArrayList<JSONObject> listDataHeader, HashMap<String, ArrayList<JSONObject>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listChildData = listChildData;
    }

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return listChildData.get(listDataHeader.get(groupPosition).getString("text")).size();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        try {
            return listChildData.get(listDataHeader.get(groupPosition).getString("text")).get(childPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.text_with_price_dashboard_super_item, null);
        }
        TextView text = convertView.findViewById(R.id.text);
        TextView price = convertView.findViewById(R.id.price);
        ImageView arrow = convertView.findViewById(R.id.arrow);

        try {
            text.setText(data.getString("text"));
            price.setText(data.getString("price_str"));
            price.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arrow.setImageResource(!isExpanded ? R.drawable.ic_keyboard_arrow_down_black_24 :
                R.drawable.ic_keyboard_arrow_up_black_24);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        JSONObject data = (JSONObject) getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.text_with_price_item, null);
        }
        TextView text = convertView.findViewById(R.id.text);
        TextView price = convertView.findViewById(R.id.price);

        try {
            text.setText(data.getString("text"));
            price.setText(data.getString("price_str"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
