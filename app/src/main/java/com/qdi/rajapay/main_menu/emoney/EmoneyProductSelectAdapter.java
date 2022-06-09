package com.qdi.rajapay.main_menu.emoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EmoneyProductSelectAdapter extends RecyclerView.Adapter<EmoneyProductSelectAdapter.MyViewHolder> {
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
        public TextView text,trouble;
        public View view;
        public LinearLayout rowItem;
        public ImageView productImage;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            trouble = view.findViewById(R.id.trouble);
            rowItem = view.findViewById(R.id.item_row);
            productImage = view.findViewById(R.id.product_image);
            this.view = view;
        }
    }

    public EmoneyProductSelectAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    public void updateList(ArrayList<JSONObject> list){
        this.arr = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emoney_product_select_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));

            /**
             * @author : Jesslyn
             * @note : change ui if product have problem
             */
            // <code>
            if(data.getBoolean("is_trouble")) {
                myViewHolder.trouble.setVisibility(View.VISIBLE);
                myViewHolder.rowItem.setBackgroundColor(context.getResources().getColor(R.color.disabled));
                myViewHolder.text.setTextColor(context.getResources().getColor(R.color.pdam_disabled));
                myViewHolder.productImage.setImageDrawable(context.getDrawable(R.drawable.ic_default_4));
            }else {
                myViewHolder.trouble.setVisibility(View.GONE);
                myViewHolder.rowItem.setBackgroundColor(context.getResources().getColor(R.color.white));
                myViewHolder.text.setTextColor(context.getResources().getColor(R.color.black));

                if(data.has("image") && !BaseActivity.isNullOrEmpty(data.getString("image"))){
                    Picasso.get()
                            .load(data.getString("image"))
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .fit().centerInside()
                            .into(myViewHolder.productImage);
                }else{
                    myViewHolder.productImage.setImageDrawable(context.getDrawable(R.drawable.ic_default_4));
                }
            }
            // </code>
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