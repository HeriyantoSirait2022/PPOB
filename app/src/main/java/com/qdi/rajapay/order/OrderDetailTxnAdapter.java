package com.qdi.rajapay.order;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import java.util.List;

public class OrderDetailTxnAdapter extends RecyclerView.Adapter<OrderDetailTxnAdapter.MyViewHolder> {
    public List<Pair<String, String>> list;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView label,value, seperator;

        public MyViewHolder(View view, int i) {
            super(view);

            label = view.findViewById(R.id.tv_label);
            value = view.findViewById(R.id.tv_value);
            seperator = view.findViewById(R.id.tv_seperator);
        }
    }

    public OrderDetailTxnAdapter(List<Pair<String, String>> list) {
        this.list = list;
    }

    public void setList() {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_detail_row, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Pair<String, String> data = list.get(i);
        myViewHolder.label.setText(data.first);
        if(data.second == null || data.second.isEmpty())
            myViewHolder.seperator.setText("");
        else
            myViewHolder.seperator.setText(":");
        myViewHolder.value.setText(data.second);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
