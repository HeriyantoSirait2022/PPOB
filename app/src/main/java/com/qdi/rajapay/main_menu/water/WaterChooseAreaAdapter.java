package com.qdi.rajapay.main_menu.water;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WaterChooseAreaAdapter extends RecyclerView.Adapter<WaterChooseAreaAdapter.MyViewHolder> {
    public ArrayList<JSONObject> arr;
    ClickListener clickListener;
    Context context;

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text,trouble;
        public View view;
        // <code>
        public LinearLayout rowItem;
        // </code>

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            trouble = view.findViewById(R.id.trouble);
            // <code>
            rowItem = view.findViewById(R.id.item_row);
            // </code>
            this.view = view;
        }
    }

    public WaterChooseAreaAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    public void updateList(ArrayList<JSONObject> list){
        this.arr = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.water_choose_area_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));

            /**
             * @author : Jesslyn
             * @note : change ui if product have problem
             */
            // <code>
            if(data.getBoolean("is_trouble")) {
                myViewHolder.trouble.setVisibility(View.VISIBLE);
                myViewHolder.rowItem.setBackgroundColor(context.getResources().getColor(R.color.disabled));
                myViewHolder.text.setTextColor(context.getResources().getColor(R.color.pdam_disabled));
            }else {
                myViewHolder.trouble.setVisibility(View.GONE);
                myViewHolder.rowItem.setBackgroundColor(context.getResources().getColor(R.color.white));
                myViewHolder.text.setTextColor(context.getResources().getColor(R.color.black));
            }
            // </code>
            myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(!data.getBoolean("is_trouble"))
                            clickListener.onClick(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}