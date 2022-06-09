package com.qdi.rajapay.agency.dashboard_super.bonus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardSuperBonusAdapter extends RecyclerView.Adapter<DashboardSuperBonusAdapter.MyViewHolder> {
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
        public TextView text,detail;
        public LinearLayout layout;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            detail = view.findViewById(R.id.detail);
            layout = view.findViewById(R.id.layout);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public DashboardSuperBonusAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agency_bonus_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("price_str"));
            myViewHolder.detail.setText(data.getString("text"));

            if(data.getBoolean("enabled"))
                myViewHolder.layout.setBackground(context.getResources().getDrawable(R.drawable.ic_milestone_achieved));
            else
                myViewHolder.layout.setBackground(context.getResources().getDrawable(R.drawable.ic_milestone_not_achieved));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}