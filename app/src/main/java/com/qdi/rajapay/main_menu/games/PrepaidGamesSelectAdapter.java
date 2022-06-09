package com.qdi.rajapay.main_menu.games;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrepaidGamesSelectAdapter extends RecyclerView.Adapter<PrepaidGamesSelectAdapter.MyViewHolder> {
    public ArrayList<JSONObject> arr, arrCopy;
    ClickListener clickListener;
    UpdateListener updateListener;
    Context context;

    public interface ClickListener{
        void onClick(int position);
    }

    public interface UpdateListener{
        void onUpdate(boolean isEmpty);
    }

    public void setOnUpdateListener(UpdateListener updateListener){
        this.updateListener = updateListener;
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public ImageView image;
        public RelativeLayout image_frame;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            image = view.findViewById(R.id.image);
            image_frame = view.findViewById(R.id.image_frame);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public PrepaidGamesSelectAdapter(ArrayList<JSONObject> arr, Context context) {
        // copy by value for each arr
        this.arr = new ArrayList<>(arr);
        this.arrCopy = new ArrayList<>(arr);
        this.context = context;
        try{
            this.updateListener = ((UpdateListener) context);
        }catch(ClassCastException e){
            throw new ClassCastException("Activity must implements OnUpdate Interface");
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_sublist_item_xl, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    public void filter(String text) {
        arr.clear();
        if(text.isEmpty()){
            arr.addAll(arrCopy);
        } else{
            text = text.toLowerCase();
            for(JSONObject item: arrCopy){
                try{
                    if(item.getString("productName").toLowerCase().contains(text)){
                        arr.add(item);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }

        if(arr.size() == 0)
            updateListener.onUpdate(true);
        else
            updateListener.onUpdate(false);

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("productName"));

            myViewHolder.image_frame.setBackground(context.getResources().getDrawable(R.drawable.ic_background_icon_squarehdpi));
            if(data.has("image") && !BaseActivity.isNullOrEmpty(data.getString("image"))){
                Picasso.get().load(data.getString("image")).into(myViewHolder.image);
            }else{
                myViewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default_3));
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