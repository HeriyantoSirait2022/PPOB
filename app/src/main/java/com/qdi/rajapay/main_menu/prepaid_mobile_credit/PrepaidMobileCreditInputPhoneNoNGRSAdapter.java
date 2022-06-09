package com.qdi.rajapay.main_menu.prepaid_mobile_credit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrepaidMobileCreditInputPhoneNoNGRSAdapter extends RecyclerView.Adapter<PrepaidMobileCreditInputPhoneNoNGRSAdapter.MyViewHolder> {
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
        public TextView status,first_char,rest_char,detail;

        public MyViewHolder(View view, int i) {
            super(view);

            status = view.findViewById(R.id.status);
            first_char = view.findViewById(R.id.first_char);
            rest_char = view.findViewById(R.id.rest_char);
            detail = view.findViewById(R.id.detail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public PrepaidMobileCreditInputPhoneNoNGRSAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_prepaid_data_input_phone_no_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            String price_str = data.getString("price_str");
            String[] split = price_str.split("\\.");
            String first_char = "", rest_char = "";
            for(int x=0;x<split.length-1;x++) {
                if(x > 0)
                    first_char += ".";
                first_char += split[x];
            }
            rest_char += ".000";

            myViewHolder.first_char.setText(first_char);
            myViewHolder.rest_char.setText(rest_char);
            myViewHolder.status.setText(data.getString("status"));
            myViewHolder.detail.setText(data.getString("detail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}