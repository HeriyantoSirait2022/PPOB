package com.qdi.rajapay.main_menu.phone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhoneChooseProviderAdapter extends RecyclerView.Adapter<PhoneChooseProviderAdapter.MyViewHolder> {
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
        public TextView text,trouble;
        public ImageView image,arrow_right;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            image = view.findViewById(R.id.image);
            trouble = view.findViewById(R.id.trouble);
            arrow_right = view.findViewById(R.id.arrow_right);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public PhoneChooseProviderAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_with_image_list_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));
            if(data.has("image"))
                myViewHolder.image.setImageDrawable(context.getResources().getDrawable(data.getInt("image")));
            else if(data.has("image_url"))
                Picasso.get()
                        .load(data.getString("image_url"))
                        .into(myViewHolder.image);

            if(data.getInt("isProblem") == 1) {
                myViewHolder.arrow_right.setVisibility(View.GONE);
                myViewHolder.trouble.setVisibility(View.VISIBLE);
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