package com.qdi.rajapay.main_menu;

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

public class MainMenuConfirmationInformationAdapter extends RecyclerView.Adapter<MainMenuConfirmationInformationAdapter.MyViewHolder> {
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
        public TextView key,value, semicolon;

        public MyViewHolder(View view, int i) {
            super(view);

            key = view.findViewById(R.id.key);
            value = view.findViewById(R.id.value);
            semicolon = view.findViewById(R.id.semicolon);
        }
    }

    public MainMenuConfirmationInformationAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_confirmation_information_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            if(data.has("key") && !data.isNull("key"))
                myViewHolder.key.setText(data.getString("key"));
            if(data.has("value") && !data.isNull("value"))
                myViewHolder.value.setText(data.getString("value"));
            /**
             * @author Jesslyn
             * @note Case Circle CI 554 : remove semicolon for empty key and value
             */
            // <code>
            if(data.has("key") && !data.isNull("key") && data.has("value") && !data.isNull("value"))
                myViewHolder.semicolon.setText(":");
            else
                myViewHolder.semicolon.setText("");
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