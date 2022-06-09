package com.qdi.rajapay.account.manage_account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageAccountIndexAdapter extends RecyclerView.Adapter<ManageAccountIndexAdapter.MyViewHolder> {
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
        public TextView title,description;
        public ImageView image;

        public MyViewHolder(View view, int i) {
            super(view);

            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            image = view.findViewById(R.id.image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public ManageAccountIndexAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            if(!data.getString("description").equals(""))
                myViewHolder.description.setText(data.getString("description"));
            else
                myViewHolder.description.setVisibility(View.GONE);
            myViewHolder.title.setText(data.getString("title"));
            myViewHolder.image.setImageDrawable(context.getDrawable(data.getInt("image")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}