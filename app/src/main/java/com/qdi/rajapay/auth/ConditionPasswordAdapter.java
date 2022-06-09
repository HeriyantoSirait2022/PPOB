package com.qdi.rajapay.auth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConditionPasswordAdapter extends RecyclerView.Adapter<ConditionPasswordAdapter.MyViewHolder> {
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
        public TextView text;
        public AppCompatCheckBox checkbox;

        public MyViewHolder(View view, int i) {
            super(view);

            text = view.findViewById(R.id.text);
            checkbox = view.findViewById(R.id.checkbox);

//            ColorStateList colorStateList = new ColorStateList(
//                    new int[][]{
//                            new int[]{-android.R.attr.state_enabled}, //disabled
//                            new int[]{android.R.attr.state_enabled} //enabled
//                    },
//                    new int[] {
//                            R.color.checkbox_disabled, //disabled
//                            R.color.colorPrimary //enabled
//                    }
//            );
//            checkbox.setButtonTintList(colorStateList);
            checkbox.setChecked(true);
        }
    }

    public ConditionPasswordAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.password_condition_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data = arr.get(i);
        try {
            myViewHolder.text.setText(data.getString("text"));

            myViewHolder.checkbox.setEnabled(data.getBoolean("is_fulfill_condition"));
//            if(!data.getBoolean("is_fulfill_condition"))
//                myViewHolder.checkbox.setTextAppearance(R.style.checkBoxDisabledTheme);
//            else
//                myViewHolder.checkbox.setTextAppearance(R.style.checkBoxTheme);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}
