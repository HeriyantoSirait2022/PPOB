package com.qdi.rajapay.cashier;

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

public class CashierIndexAdapter extends RecyclerView.Adapter<CashierIndexAdapter.MyViewHolder> {
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
        public TextView text;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public CashierIndexAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_only_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}