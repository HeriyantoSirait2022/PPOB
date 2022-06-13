package com.qdi.rajapay.point;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.model.RewardData;
import com.qdi.rajapay.utils.NumberUtils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.ArrayList;

public class PointRewardAdapter extends RecyclerView.Adapter<PointRewardAdapter.MyViewHolder> {
    public ArrayList<RewardData> arr;
    PointRewardAdapter.ClickListener clickListener;
    Context context;

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(PointRewardAdapter.ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, point, description;
        public ImageView image;
        public Button redeem, redeemInProgress;

        public MyViewHolder(View view, int i) {
            super(view);

            title = view.findViewById(R.id.title);
            point = view.findViewById(R.id.point);
            description = view.findViewById(R.id.description);
            image = view.findViewById(R.id.image);
            redeem = view.findViewById(R.id.redeem);
            redeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
            redeemInProgress = view.findViewById(R.id.redeem_in_progress);
        }
    }

    public PointRewardAdapter(ArrayList<RewardData> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public PointRewardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.point_reward_list_item, viewGroup, false);
        return new PointRewardAdapter.MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull PointRewardAdapter.MyViewHolder myViewHolder, final int i) {
        RewardData data = arr.get(i);
        myViewHolder.title.setText(data.name);
        int p = 0;
        if(data.point > 0){
            p = data.point;
        }

        myViewHolder.point.setText(MessageFormat.format("{0}", NumberUtils.format(p))+ " poin");
        myViewHolder.description.setText(data.description);
        if(!data.image.isEmpty()) {
            Picasso.get()
                    .load(data.image)
                    .into(myViewHolder.image);
        }

        myViewHolder.redeem.setVisibility(View.GONE);
        myViewHolder.redeemInProgress.setVisibility(View.GONE);
        if(data.is_pending){
            myViewHolder.redeemInProgress.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.redeem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}
