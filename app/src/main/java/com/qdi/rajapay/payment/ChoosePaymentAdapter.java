package com.qdi.rajapay.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChoosePaymentAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<JSONObject> listDataHeader;
    HashMap<String, ArrayList<JSONObject>> listChildData;
    ClickListener clickListener;

    public ChoosePaymentAdapter(Context context, ArrayList<JSONObject> listDataHeader, HashMap<String, ArrayList<JSONObject>> listChildData) {
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
            return listChildData.get(listDataHeader.get(groupPosition).getString("title")).size();
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
            return listChildData.get(listDataHeader.get(groupPosition).getString("title")).get(childPosition);
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
            convertView = inflater.inflate(R.layout.choose_payment_list_item, null);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        RadioButton radio = convertView.findViewById(R.id.radio);

        try {
            title.setText(data.getString("title"));
            if(data.has("detail")) {
                description.setVisibility(View.VISIBLE);
                description.setText(data.getString("detail"));
            }
            else
                description.setVisibility(View.GONE);
            radio.setChecked(data.getBoolean("selected"));

            /**
             * @author Jesslyn
             * @note disable if the child item only one and isProblem = 1, if isInactive had sorted by Web Service
             */
            // <code>
            JSONArray arr = data.getJSONArray("arr");
            if(arr.length() == 1 && arr.getJSONObject(0).getInt("isProblem") == 1){
                convertView.setBackground(context.getResources().getDrawable(R.color.disabled));
            }else{
                convertView.setBackground(context.getResources().getDrawable(R.color.white));
            }
            // </code>
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        JSONObject data = (JSONObject) getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.choose_payment_sublist_item, null);
        }
        try {
            LinearLayout layout = convertView.findViewById(R.id.layout);
            RecyclerView list = convertView.findViewById(R.id.list);

            ChoosePaymentBankAdapter adapter;
            RecyclerView.LayoutManager layout_manager;
            ArrayList<JSONObject> array = new ArrayList<>();

            JSONArray jsonArray = listDataHeader.get(groupPosition).getJSONArray("arr");
            for(int x=0;x<jsonArray.length();x++)
                array.add(jsonArray.getJSONObject(x));

            adapter = new ChoosePaymentBankAdapter(array, context);
            layout_manager = new LinearLayoutManager(context);
            list.setAdapter(adapter);
            list.setLayoutManager(layout_manager);

            adapter.setOnItemClickListener(new ChoosePaymentBankAdapter.ClickListener() {
                @Override
                public void onClick(int position) {
                    clickListener.onClick(position);
                }
            });
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
