package com.qdi.rajapay.print;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qid.objx.TwoCols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintDevicePreviewAdapter extends RecyclerView.Adapter<PrintDevicePreviewAdapter.MyViewHolder> {

    public List<TwoCols> list = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView col1,col2, seperator;

        public MyViewHolder(View view) {
            super(view);

            col1 = view.findViewById(R.id.tv_col1);
            col2 = view.findViewById(R.id.tv_col2);
            seperator = view.findViewById(R.id.tv_seperator);
        }
    }

    public void setList(List<TwoCols> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.print_device_preview_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        TwoCols data = list.get(i);
        myViewHolder.col1.setText(data.cols1);

        /**
         * @authors : Maria Florencia CC Ms. Jesslyn
         * @notes : fix up issue cols1
         */
        // <code>
        myViewHolder.col1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        // </code>

        if(data.cols2 != null && (Arrays.asList("LEFT", "CENTER", "RIGHT").contains(data.cols2))) {
            if(data.cols2.equals("LEFT"))
                myViewHolder.col1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            else if(data.cols2.equals("RIGHT"))
                myViewHolder.col1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            else if(data.cols2.equals("CENTER"))
                myViewHolder.col1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            myViewHolder.col2.setVisibility(View.GONE);
            myViewHolder.seperator.setVisibility(View.GONE);
        } else {
            if(data.cols1.equalsIgnoreCase("Total")) {
                myViewHolder.col2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                myViewHolder.seperator.setVisibility(View.GONE);
            }else {
                myViewHolder.col2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                myViewHolder.seperator.setVisibility(View.VISIBLE);
            }
            myViewHolder.col2.setText(data.cols2);
            myViewHolder.col2.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
