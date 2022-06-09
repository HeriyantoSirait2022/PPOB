package com.qdi.rajapay.print;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import java.util.List;

public class PrintChooseDeviceAdapter extends RecyclerView.Adapter<PrintChooseDeviceAdapter.MyViewHolder> {
    public List<BluetoothDevice> arr;
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

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public PrintChooseDeviceAdapter(List<BluetoothDevice> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    public void setArr(List<BluetoothDevice> arr) {
        this.arr = arr;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_only_item_with_line, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        BluetoothDevice data = arr.get(i);
        myViewHolder.text.setText(data.getName());
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}