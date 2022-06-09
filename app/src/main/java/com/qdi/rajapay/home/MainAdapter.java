package com.qdi.rajapay.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    public ArrayList<JSONObject> arr;
    ClickListener clickListener;
    Context context;

    MainSubAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    public interface ClickListener{
        void onClick(int position1,int position2);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public RecyclerView list;
        public View view_hr;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            list = view.findViewById(R.id.list);
            view_hr =view.findViewById(R.id.view_hr);
        }
    }

    public MainAdapter(ArrayList<JSONObject> arr,Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_list_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("name"));

            array = new ArrayList<>();
            for(int l=0;l<data.getJSONArray("arr").length();l++)
                array.add(data.getJSONArray("arr").getJSONObject(l));

            adapter = new MainSubAdapter(array,context);
            layout_manager = new GridLayoutManager(context,4);
            myViewHolder.list.setAdapter(adapter);
            myViewHolder.list.setLayoutManager(layout_manager);

            if(i == arr.size() - 1){
                myViewHolder.view_hr.setVisibility(View.GONE);
            }

            adapter.setOnItemClickListener(new MainSubAdapter.ClickListener() {
                @Override
                public void onClick(int position) {
                    clickListener.onClick(i,position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}