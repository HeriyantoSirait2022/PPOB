package com.qdi.rajapay.main_menu.phone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qdi.rajapay.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PhonePriceListAdapter extends BaseExpandableListAdapter {
    Context context;
    com.qdi.rajapay.main_menu.phone.PhonePriceListAdapter.ClickListener clickListener;
    ArrayList<JSONObject> listDataHeader;
    HashMap<JSONObject, ArrayList<JSONObject>> listChildData;

    public PhonePriceListAdapter(Context context, ArrayList<JSONObject> listDataHeader, HashMap<JSONObject, ArrayList<JSONObject>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listChildData = listChildData;
    }

    public interface ClickListener{
        void onClick(int group_position, int child_position);
    }

    public void setOnItemClickListener(com.qdi.rajapay.main_menu.phone.PhonePriceListAdapter.ClickListener clickListener){
        this.clickListener= clickListener;
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
            convertView = infalInflater.inflate(R.layout.text_with_image_list_item, null);
        }
        TextView title = convertView.findViewById(R.id.text);
        ImageView image = convertView.findViewById(R.id.image);
        ImageView arrow_right = convertView.findViewById(R.id.arrow_right);

        try {
            title.setText(data.getString("text"));
            if(data.has("image"))
                image.setImageDrawable(context.getResources().getDrawable(data.getInt("image")));
            else if(data.has("image_url"))
                Picasso.get()
                        .load(data.getString("image_url"))
                        .into(image);
            arrow_right.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        JSONObject data = (JSONObject) getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.postpaid_data_price_list_child, null);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView price = convertView.findViewById(R.id.price);

        try {
            title.setText(data.getString("text"));
            /**
             * @author : Jesslyn
             * @note : if product is problem, than the information "gangguan" will appears
             */
            // <code>
            if(data.getString("detail").equalsIgnoreCase("-")){
                price.setTextColor(context.getResources().getColor(R.color.black));
                price.setText(data.getString("price_str"));
            }else{
                price.setText(data.getString("detail"));
                if(data.getBoolean("is_important")) {
                    price.setTextColor(context.getResources().getColor(R.color.danger));
                }
            }
            // </code>
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(groupPosition,childPosition);
                }
            });
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
