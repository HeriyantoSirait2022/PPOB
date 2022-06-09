package com.qdi.rajapay.main_menu.electrical_token;

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

public class ElectricalTokenPriceListAdapter extends RecyclerView.Adapter<ElectricalTokenPriceListAdapter.MyViewHolder> {
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
        public TextView name,detail,price;
        public View view;

        public MyViewHolder(View view, int i) {
            super(view);

            name = view.findViewById(R.id.name);
            detail = view.findViewById(R.id.detail);
            price = view.findViewById(R.id.price);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
            this.view = view;
        }
    }

    public ElectricalTokenPriceListAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_electrical_token_price_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.name.setText(data.getString("name"));
            /**
             * @author Jesslyn
             * @note if detail contains string "-" then set visibility gone
             */
            // <code>
            if(data.getString("detail").equalsIgnoreCase("-")){
                myViewHolder.detail.setVisibility(View.GONE);
            }else{
                myViewHolder.detail.setVisibility(View.VISIBLE);
            }
            // </code>
            myViewHolder.detail.setText(data.getString("detail"));
            myViewHolder.price.setText(data.getString("price_str"));

            if(data.getBoolean("is_important")) {
                myViewHolder.detail.setTextColor(context.getResources().getColor(R.color.danger));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}