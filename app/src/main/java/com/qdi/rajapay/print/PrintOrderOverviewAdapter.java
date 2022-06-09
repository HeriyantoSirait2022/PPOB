package com.qdi.rajapay.print;

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

import java.util.List;

public class PrintOrderOverviewAdapter extends RecyclerView.Adapter<PrintOrderOverviewAdapter.MyViewHolder> {
    public List<JSONObject> arr;
    ClickListener clickListener;
    Context context;

    public interface ClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,price, total, price_header;

        public MyViewHolder(View view, int i) {
            super(view);

            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            total = view.findViewById(R.id.total);
            price_header = view.findViewById(R.id.price_header);
        }
    }

    public PrintOrderOverviewAdapter(List<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_print_price_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.name.setText(data.getString("name"));

            myViewHolder.price.setVisibility(View.GONE);
            myViewHolder.total.setVisibility(View.GONE);
            myViewHolder.price_header.setVisibility(View.VISIBLE);

            myViewHolder.price_header.setText(data.getString("price_str"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}
