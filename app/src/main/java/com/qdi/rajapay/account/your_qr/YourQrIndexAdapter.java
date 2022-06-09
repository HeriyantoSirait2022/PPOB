package com.qdi.rajapay.account.your_qr;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class YourQrIndexAdapter extends RecyclerView.Adapter<YourQrIndexAdapter.MyViewHolder> {
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
        public TextView text;
        public Button action;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            action = view.findViewById(R.id.action);

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public YourQrIndexAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.your_qr_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));
            myViewHolder.action.setText(data.getString("action"));
            myViewHolder.action.setCompoundDrawablesWithIntrinsicBounds(data.getInt("icon_action"),0,0,0);

            if(data.getString("action_type").equalsIgnoreCase("outline")){
                myViewHolder.action.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                myViewHolder.action.setTextColor(context.getResources().getColor(R.color.colorPrimary));
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