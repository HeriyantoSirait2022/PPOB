package com.qdi.rajapay.main_menu.emoney;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EmoneyDenominationAdapter extends RecyclerView.Adapter<EmoneyDenominationAdapter.MyViewHolder> {
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
        public TextView price;
        public LinearLayout layout;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            price = view.findViewById(R.id.price);
            layout = view.findViewById(R.id.layout_item);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject data = arr.get(getAdapterPosition());
                    try {
                        if(data.has("isProblem") && data.getInt("isProblem") != 1)
                            clickListener.onClick(getAdapterPosition());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public EmoneyDenominationAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_with_price_right_arrow_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            if(data.has("text"))
                myViewHolder.text.setText(data.getString("text"));

            if(data.has("isProblem")){
                if(data.getInt("isProblem") == 1){
                    myViewHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.disabled));
                    myViewHolder.text.setTextColor(context.getResources().getColor(R.color.pdam_disabled));
                    myViewHolder.price.setTextColor(context.getResources().getColor(R.color.danger));

                    myViewHolder.price.setText( context.getString(R.string.trouble_label));
                }else{
                    myViewHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.white));
                    myViewHolder.price.setTextColor(context.getResources().getColor(R.color.black));
                    myViewHolder.text.setTextColor(context.getResources().getColor(R.color.black));

                    if(data.has("totalPrice"))
                        myViewHolder.price.setText( ((BaseActivity)context).toLocalIdr(data.getString("totalPrice")) );
                }
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