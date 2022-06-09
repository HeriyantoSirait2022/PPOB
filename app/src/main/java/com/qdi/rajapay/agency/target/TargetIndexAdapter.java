package com.qdi.rajapay.agency.target;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TargetIndexAdapter extends RecyclerView.Adapter<TargetIndexAdapter.MyViewHolder> {
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
        public TextView time,detail,status;
        public ImageView image;

        public MyViewHolder(View view, int i) {
            super(view);

            time = view.findViewById(R.id.time);
            detail = view.findViewById(R.id.detail);
            status = view.findViewById(R.id.status);
            image = view.findViewById(R.id.image);
        }
    }

    public TargetIndexAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agency_target_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.time.setText(data.getString("time"));
            myViewHolder.detail.setText(data.getString("detail"));
            myViewHolder.status.setText(data.getString("status"));
            myViewHolder.image.setImageDrawable(data.getString("status").equals("Sukses") ?
                    context.getResources().getDrawable(R.drawable.icon_check) :
                    context.getResources().getDrawable(R.drawable.ic_icon_cancelhdpi));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}