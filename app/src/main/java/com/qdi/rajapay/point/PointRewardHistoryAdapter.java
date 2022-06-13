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
import com.qdi.rajapay.model.RewardHistoryData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PointRewardHistoryAdapter extends RecyclerView.Adapter<PointRewardHistoryAdapter.MyViewHolder> {
    public ArrayList<RewardHistoryData> arr;
    PointRewardHistoryAdapter.ClickListener clickListener;
    Context context;

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(PointRewardHistoryAdapter.ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;
        public ImageView image;
        public Button status;

        public MyViewHolder(View view, int i) {
            super(view);

            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            image = view.findViewById(R.id.image);
            status = view.findViewById(R.id.status);
//            redeem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickListener.onClick(getAdapterPosition());
//                }
//            });
//            redeemInProgress = view.findViewById(R.id.redeem_in_progress);
        }
    }

    public PointRewardHistoryAdapter(ArrayList<RewardHistoryData> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public PointRewardHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.point_reward_history_list_item, viewGroup, false);
        return new PointRewardHistoryAdapter.MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull PointRewardHistoryAdapter.MyViewHolder myViewHolder, final int i) {
        RewardHistoryData data = arr.get(i);
        myViewHolder.title.setText(data.reward_name);
        myViewHolder.description.setText(data.reward_description);
        if(!data.image.isEmpty()) {
            Picasso.get()
                    .load(data.image)
                    .into(myViewHolder.image);
        }

        myViewHolder.status.setText(data.status_name);

        switch(data.status){
            case "R": myViewHolder.status.setBackgroundResource(R.color.danger);break;
            case "S": myViewHolder.status.setBackgroundResource(R.color.colorPrimaryDark);break;
            default:  myViewHolder.status.setBackgroundResource(R.color.blue_completed);
        }

//        myViewHolder.redeem.setVisibility(View.GONE);
//        myViewHolder.redeemInProgress.setVisibility(View.GONE);
//        if(data.is_pending){
//            myViewHolder.redeemInProgress.setVisibility(View.VISIBLE);
//        }else{
//            myViewHolder.redeem.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}
