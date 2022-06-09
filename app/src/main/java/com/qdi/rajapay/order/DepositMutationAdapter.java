package com.qdi.rajapay.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.model.DepositMutationData;
import com.qdi.rajapay.utils.NumberUtils;

public class DepositMutationAdapter extends RecyclerView.Adapter<DepositMutationAdapter.MyViewHolder> {
    public DepositMutationData data;
    ClickListener clickListener;
    Context context;

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,price;

        public MyViewHolder(View view, int i) {
            super(view);

            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
        }
    }

    public DepositMutationAdapter(Context context) {
        this.context = context;
    }

    public void setData(DepositMutationData data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_mutation_deposit, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        switch(i){
            case 0: {
                myViewHolder.name.setText(context.getResources().getString(R.string.order_deposit_mutation_beginning));
                myViewHolder.price.setText(NumberUtils.format(data.getInitDeposit()));
                break;
            }
            case 1: {
                myViewHolder.name.setText(context.getResources().getString(R.string.order_deposit_mutation_in));
                myViewHolder.price.setText(NumberUtils.format(data.getDepositIn()));
                break;
            }
            case 2: {
                myViewHolder.name.setText(context.getResources().getString(R.string.order_deposit_mutation_out));
                myViewHolder.price.setText(NumberUtils.format(data.getDepositOut()));
                break;
            }
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        return data==null ? 0 : 3;
    }
}