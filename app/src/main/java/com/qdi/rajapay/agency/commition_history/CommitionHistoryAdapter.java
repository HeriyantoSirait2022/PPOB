package com.qdi.rajapay.agency.commition_history;

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

public class CommitionHistoryAdapter extends RecyclerView.Adapter<CommitionHistoryAdapter.MyViewHolder> {
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
        public TextView title,time,price, title2;
        public LinearLayout data, header;

        public MyViewHolder(View view, int i) {
            super(view);

            time = view.findViewById(R.id.time);
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);

            title2 = view.findViewById(R.id.title2);

            data = view.findViewById(R.id.data);
            header = view.findViewById(R.id.header);
        }
    }

    public CommitionHistoryAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agency_commition_history_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            if(data.has("title")){
                myViewHolder.data.setVisibility(View.VISIBLE);
                myViewHolder.header.setVisibility(View.GONE);

                myViewHolder.time.setText(data.getString("time"));
                myViewHolder.title.setText(data.getString("title"));
                myViewHolder.price.setText(data.getString("price_str"));

                myViewHolder.title2.setText("");
            }else{
                myViewHolder.data.setVisibility(View.GONE);
                myViewHolder.header.setVisibility(View.VISIBLE);

                myViewHolder.time.setText("");
                myViewHolder.title.setText("");
                myViewHolder.price.setText("");

                myViewHolder.title2.setText(data.getString("day"));
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