package com.qdi.rajapay.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChoosePaymentBankAdapter extends RecyclerView.Adapter<ChoosePaymentBankAdapter.MyViewHolder> {
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
        public ImageView image;
        public TextView text;
        public CardView layout;

        public MyViewHolder(View view, int i) {
            super(view);

            image = view.findViewById(R.id.image);
            layout = view.findViewById(R.id.layout);
            text = view.findViewById(R.id.text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * @author Jesslyn
                     * @note interrupt interface if child view isPriblem == 1
                     */
                    // <code>
                    int i = getAdapterPosition();
                    JSONObject data = arr.get(i);

                    try {
                        if(data.has("isProblem") && data.getInt("isProblem") == 0) {
                            clickListener.onClick(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // </code>
                }
            });
        }
    }

    public ChoosePaymentBankAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.choose_payment_bank_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            if(!data.isNull("image")) {
                myViewHolder.image.setVisibility(View.VISIBLE);
                myViewHolder.text.setVisibility(View.GONE);
                myViewHolder.image.setImageDrawable(context.getResources().getDrawable(data.getInt("image")));
            }
            else if(!data.isNull("image_url")) {
                myViewHolder.image.setVisibility(View.VISIBLE);
                myViewHolder.text.setVisibility(View.GONE);
                Picasso.get()
                        .load(data.getString("image_url"))
                        .into(myViewHolder.image);
            }
            else if(!data.isNull("name")) {
                myViewHolder.image.setVisibility(View.GONE);
                myViewHolder.text.setVisibility(View.VISIBLE);
                myViewHolder.text.setText(data.getString("name"));
            }

            /**
             * @author Jesslyn
             * @note Perform check for payment channel isProblem
             */
            // <code>
            if(data.has("isProblem") && data.getInt("isProblem") == 1){
                myViewHolder.layout.setBackground(context.getResources().getDrawable(R.color.disabled));
            }else{
                if(data.getBoolean("selected"))
                    myViewHolder.layout.setBackground(context.getResources().getDrawable(R.drawable.rounded_blue));
                else
                    myViewHolder.layout.setBackground(context.getResources().getDrawable(R.color.white));
            }
            // </code>
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}