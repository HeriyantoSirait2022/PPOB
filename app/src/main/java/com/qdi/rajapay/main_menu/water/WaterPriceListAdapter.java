package com.qdi.rajapay.main_menu.water;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WaterPriceListAdapter extends RecyclerView.Adapter<WaterPriceListAdapter.MyViewHolder> {
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
        public TextView text,price;
        public View view;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            price = view.findViewById(R.id.price);
            this.view = view;
        }
    }

    public WaterPriceListAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_water_price_list_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));
            /**
             * @author : Jesslyn
             * @note :
             */
            if(data.getBoolean("is_trouble")) {
                myViewHolder.view.setBackgroundColor(context.getResources().getColor(R.color.disabled));
                myViewHolder.price.setText(context.getResources().getString(R.string.trouble_label));
                myViewHolder.price.setTextColor(context.getResources().getColor(R.color.danger));
            }
            else {
                myViewHolder.view.setBackgroundColor(context.getResources().getColor(R.color.white));
                myViewHolder.price.setText(data.getString("price_str"));
                myViewHolder.price.setTextColor(context.getResources().getColor(R.color.black));
            }

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