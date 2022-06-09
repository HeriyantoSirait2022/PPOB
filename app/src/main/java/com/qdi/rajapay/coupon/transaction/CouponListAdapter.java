package com.qdi.rajapay.coupon.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.MyViewHolder> {
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
        public TextView title, expired_date, heading;
        public LinearLayout couponNoticeLayout;
        public ImageView image, couponIndicator;
        public Button use;

        public MyViewHolder(View view, int i) {
            super(view);

            title = view.findViewById(R.id.title);
            heading = view.findViewById(R.id.heading);
            expired_date = view.findViewById(R.id.expired_date);
            use = view.findViewById(R.id.use);
            image = view.findViewById(R.id.image);
            couponNoticeLayout = view.findViewById(R.id.coupon_notice);
            couponIndicator = view.findViewById(R.id.coupon_indicator);

            use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public CouponListAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coupon_list_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.title.setText(data.getString("title"));
            myViewHolder.expired_date.setText(data.getString("expired_date"));
            /**
             * @author Eliza Sutantya
             * @patch FR19022
             * @notes 0911202110-103 D03 Create Coupon List (ref: Payfazz) and coupon detail (Standard Development Kit (SDK)) - L3)
             * ...... 1. add feature action title
             * ...... 2. remove image set image drawable and set default image for null
             */
            // <code>
            if(data.has("button_action_title"))
                myViewHolder.use.setText(data.getString("button_action_title"));

            if(!data.isNull("image_url") && !BaseActivity.isNullOrEmpty(data.getString("image_url")))
                Picasso.get()
                        .load(data.getString("image_url"))
                       .into(myViewHolder.image);
            else
                myViewHolder.image.setImageDrawable(context.getDrawable(R.drawable.ic_image_na));
            // </code>

            if(i == (arr.size() - 1)){
                myViewHolder.couponNoticeLayout.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.couponNoticeLayout.setVisibility(View.GONE);
            }

            if(data.has("isOwned") && data.getBoolean("isOwned")){
                myViewHolder.couponIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_coupon_primary));
                if(data.has("qty_left")){
                    myViewHolder.heading.setVisibility(View.VISIBLE);
                    myViewHolder.heading.setText("Tersisa " + data.getInt("qty_left") + " kupon lagi!");
                }else{
                    myViewHolder.heading.setVisibility(View.GONE);
                    myViewHolder.heading.setText("");
                }
            }
            else {
                myViewHolder.couponIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_coupon_grey));
                myViewHolder.heading.setVisibility(View.GONE);
                myViewHolder.heading.setText("");
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