package com.qdi.rajapay.main_menu.gas;

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

public class GasPriceListAdapter extends RecyclerView.Adapter<GasPriceListAdapter.MyViewHolder> {
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
        public TextView text,detail,price;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            price = view.findViewById(R.id.price);
            detail = view.findViewById(R.id.detail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public GasPriceListAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_electrical_price_list_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));
            myViewHolder.price.setText(data.getString("price_str"));

            /**
             * @author : Jesslyn
             * @note : set visibility gone to detail if detail is "-"
             */
            // <code>
            if(data.getString("detail").equalsIgnoreCase("-")){
                myViewHolder.detail.setVisibility(View.GONE);
            }else{
                myViewHolder.detail.setVisibility(View.VISIBLE);
                myViewHolder.detail.setText(data.getString("detail"));
                if(data.getBoolean("is_important")) {
                    myViewHolder.detail.setTextColor(context.getResources().getColor(R.color.danger));
                }
            }
            // </code>
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}