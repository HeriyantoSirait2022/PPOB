package com.qdi.rajapay.main_menu.prepaid_data;

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

public class PrepaidDataInputPhoneNoCategoryAdapter extends RecyclerView.Adapter<PrepaidDataInputPhoneNoCategoryAdapter.MyViewHolder> {
    public ArrayList<JSONObject> arr;
    ClickListener clickListener;
    Context context;
    public int curSelected;
    public int paddingPixel;

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public void setPaddingPixel(int paddingPixel){
        this.paddingPixel = paddingPixel;
    }

    public int getPaddingPixel(){
        return this.paddingPixel;
    }

    public PrepaidDataInputPhoneNoCategoryAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
        this.curSelected = -1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_prepaid_data_input_phone_no_item_category, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));

            if(data.getBoolean("isSelected")) {
                myViewHolder.text.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                myViewHolder.text.setTextColor(context.getResources().getColor(R.color.white));
                curSelected = i;
            }else{
                myViewHolder.text.setBackgroundColor(context.getResources().getColor(R.color.disabled));
                myViewHolder.text.setTextColor(context.getResources().getColor(R.color.black));
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