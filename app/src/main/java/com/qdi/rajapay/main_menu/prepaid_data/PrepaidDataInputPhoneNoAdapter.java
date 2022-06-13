package com.qdi.rajapay.main_menu.prepaid_data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.utils.NumberUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;

public class PrepaidDataInputPhoneNoAdapter extends RecyclerView.Adapter<PrepaidDataInputPhoneNoAdapter.MyViewHolder> {
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
        public TextView point, status,first_char,rest_char,detail;

        public MyViewHolder(View view, int i) {
            super(view);

            point = view.findViewById(R.id.point);
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

    public PrepaidDataInputPhoneNoAdapter(ArrayList<JSONObject> arr, Context context) {
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
            myViewHolder.point.setVisibility(View.GONE);

            int p = 0;
            if(data.getInt("point") > 0){
                p = data.getInt("point");
                if(p>0){
                    myViewHolder.point.setVisibility(View.VISIBLE);
                }
            }

            myViewHolder.point.setText(MessageFormat.format("+ {0}", NumberUtils.format(p))+ " poin");

            String price_str = data.getString("price_display");
            String[] split = price_str.split("\\.");
            String first_char = "", rest_char = "";
            if(split.length > 2) {
                for(int x=0;x<split.length-1;x++) {
                    if (x > 0)
                        first_char += ".";
                    first_char += split[x];
                }
                /**
                 * @author : Jesslyn
                 * @note : Fixing issue after period
                 */
                // <code>
                rest_char = "." + split[split.length - 1];
                // </code>
            }
            else {
                first_char = data.getString("price_str");
                rest_char = "";
            }

            myViewHolder.first_char.setText(first_char.replace("Rp.",""));
            myViewHolder.rest_char.setText(rest_char);
            myViewHolder.status.setText(data.getString("status"));
            myViewHolder.detail.setText(data.getString("name"));

            if(data.getInt("isProblem") == 1) {
                myViewHolder.status.setBackgroundColor(context.getResources().getColor(R.color.unavailable));
            }else{
                myViewHolder.status.setBackgroundColor(context.getResources().getColor(R.color.available));
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