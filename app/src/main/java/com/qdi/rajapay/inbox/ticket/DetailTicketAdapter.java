package com.qdi.rajapay.inbox.ticket;

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

public class DetailTicketAdapter extends RecyclerView.Adapter<DetailTicketAdapter.MyViewHolder> {
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
        public TextView text,time;
        public ImageView image;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            time = view.findViewById(R.id.time);
            image = view.findViewById(R.id.image);
        }
    }

    public DetailTicketAdapter(ArrayList<JSONObject> arr,Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_bubble_right, viewGroup, false);
            return new MyViewHolder(view, i);
        }
        else if(i == 1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_bubble_left, viewGroup, false);
            return new MyViewHolder(view, i);
        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_bubble_left, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject data = arr.get(position);
        try {
            if(data.getString("type").equals("sender"))
                return 0;
            else if(data.getString("type").equals("receiver"))
                return 1;
            else
                return 2;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            if(data.getString("data_type").equals("text")) {
                myViewHolder.image.setVisibility(View.GONE);
                myViewHolder.text.setVisibility(View.VISIBLE);
                myViewHolder.text.setText(data.getString("text"));
            }
            else if(data.getString("data_type").equals("image")){
                myViewHolder.text.setVisibility(View.GONE);
                myViewHolder.image.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(data.getString("image"))
                        .into(myViewHolder.image);
            }
            myViewHolder.time.setText(data.getString("time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}