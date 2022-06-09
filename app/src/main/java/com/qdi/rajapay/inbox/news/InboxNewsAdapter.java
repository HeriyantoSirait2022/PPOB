package com.qdi.rajapay.inbox.news;

import android.content.Context;
import android.os.Build;
import android.text.Html;
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

public class InboxNewsAdapter extends RecyclerView.Adapter<InboxNewsAdapter.MyViewHolder> {
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
        public TextView title,date;
        public ImageView new_indicator;

        public MyViewHolder(View view, int i) {
            super(view);

            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            new_indicator = view.findViewById(R.id.new_indicator);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public InboxNewsAdapter(ArrayList<JSONObject> arr,Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inbox_news_list_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.title.setText(data.getString("title"));
            /**
             * @authors : Jesslyn
             * @notes : issue 27 -  JIRA (add bold at time)
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                myViewHolder.date.setText(Html.fromHtml(data.getString("date"), Html.FROM_HTML_MODE_COMPACT));
            } else {
                myViewHolder.date.setText(Html.fromHtml(data.getString("date")));
            }
            if(data.getBoolean("is_today"))
                myViewHolder.new_indicator.setVisibility(View.VISIBLE);
            else
                myViewHolder.new_indicator.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}