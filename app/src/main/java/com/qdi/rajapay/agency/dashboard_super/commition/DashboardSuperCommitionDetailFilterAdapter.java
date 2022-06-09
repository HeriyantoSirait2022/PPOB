package com.qdi.rajapay.agency.dashboard_super.commition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardSuperCommitionDetailFilterAdapter extends RecyclerView.Adapter<DashboardSuperCommitionDetailFilterAdapter.MyViewHolder> {
    public ArrayList<JSONObject> arr;
    ClickListener clickListener;
    Context context;
    boolean is_system = false;

    public interface ClickListener{
        void onClick(int position, boolean selected);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton radio;

        public MyViewHolder(View view, int i) {
            super(view);

            radio = view.findViewById(R.id.radio);
        }
    }

    public DashboardSuperCommitionDetailFilterAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agency_commition_history_filter_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.radio.setText(data.getString("radio"));
            myViewHolder.radio.setChecked(data.getBoolean("selected"));

            myViewHolder.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(is_system){
                        is_system = false;
                        return;
                    }
                    clickListener.onClick(i,isChecked);
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