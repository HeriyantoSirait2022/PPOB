package com.qdi.rajapay.agency.dashboard_super.commition;

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

public class DashboardSuperCommitionDetailAdapter extends RecyclerView.Adapter<DashboardSuperCommitionDetailAdapter.MyViewHolder> {
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

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            price = view.findViewById(R.id.price);
        }
    }

    public DashboardSuperCommitionDetailAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_with_price_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));
            myViewHolder.price.setText(data.getString("price_str"));
            myViewHolder.price.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}