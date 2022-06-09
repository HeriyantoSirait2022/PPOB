package com.qdi.rajapay.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.utils.NumberUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    public List<OrderData> arr;
    ClickListener clickListener;
    Context context;

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,detail,price,date,status;
        public ImageView image;

        public MyViewHolder(View view, int i) {
            super(view);

            title = view.findViewById(R.id.title);
            detail = view.findViewById(R.id.detail);
            date = view.findViewById(R.id.date);
            price = view.findViewById(R.id.price);
            status = view.findViewById(R.id.status);
            image = view.findViewById(R.id.image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public TransactionAdapter(List<OrderData> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    public void setArr(List<OrderData> arr) {
        this.arr = arr;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_transaction_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        OrderData data = arr.get(i);

        myViewHolder.title.setText(data.getNmTxn());
        myViewHolder.detail.setText(data.getDtlTxn());
        myViewHolder.date.setText(data.getFormattedDateOrder());
        myViewHolder.status.setText(data.getStatus().toDisplayString());
        myViewHolder.price.setText(NumberUtils.format(data.getTotBillAmount()));

        /**
         * @authors : Jacqueline
         * @notes : simplified code using base class function
         */
        // <code>
        int statusColor = BaseActivity.getStatusColor(data.getStatus());
        myViewHolder.status.setBackgroundColor(context.getResources().getColor(statusColor));
        // </code>

        /**
         * @author Jesslyn
         * @note if data not contains any string of image link then set default image
         */
        // <code>
        if(!BaseActivity.isNullOrEmpty(data.getImage()))
            Picasso.get().load(data.getImage()).into(myViewHolder.image);
        else
            myViewHolder.image.setImageDrawable(context.getDrawable(R.drawable.ic_icon_detail_transaksihdpi));
        // </code>
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}