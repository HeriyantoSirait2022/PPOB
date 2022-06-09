package com.qdi.rajapay.cashier.manage_sell_price;

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
import com.qdi.rajapay.model.ProductData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ManageSellPriceChooseProviderAdapter extends RecyclerView.Adapter<ManageSellPriceChooseProviderAdapter.MyViewHolder> {
    public List<ProductData> arr;
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
        public ImageView image;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            image = view.findViewById(R.id.image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public ManageSellPriceChooseProviderAdapter(List<ProductData> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_sublist_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ProductData data = arr.get(i);
        myViewHolder.text.setText(data.getNameProduct());
        if(BaseActivity.isNullOrEmpty(data.getImage()))
            myViewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default_4));
        else
            Picasso.get().load(data.getImage()).into(myViewHolder.image);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}