package com.qdi.rajapay.contact_us;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdi.rajapay.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ContactUsConfirmationAdapter extends RecyclerView.Adapter<ContactUsConfirmationAdapter.MyViewHolder> {
    public ArrayList<JSONObject> arr;
    ClickListener clickListener;
    Context context;

    boolean from_system = false;

    public interface ClickListener{
        void onUploadClick(int position);
        void onChangeImageClick(int position);
        void onInputChange(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView key,value;
        public ImageView iv, upload;
        public LinearLayout upload_layout, image_layout;
        public Button change_image;
        public CardView input_layout;
        public EditText input, input_text;

        public MyViewHolder(View view, int i) {
            super(view);

            key = view.findViewById(R.id.key);
            value = view.findViewById(R.id.value);
            iv = view.findViewById(R.id.imageView);
            upload = view.findViewById(R.id.upload);
            upload_layout = view.findViewById(R.id.upload_layout);
            change_image = view.findViewById(R.id.change_image);
            image_layout = view.findViewById(R.id.image_layout);
            input_layout = view.findViewById(R.id.input_layout);
            input = view.findViewById(R.id.input);
            input_text = view.findViewById(R.id.input_text);
        }
    }

    public ContactUsConfirmationAdapter(ArrayList<JSONObject> arr, Context context) {
        this.arr=arr;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_us_confirmation_item, viewGroup, false);
        return new MyViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final JSONObject data = arr.get(i);
        try {
            myViewHolder.key.setText(data.getString("key"));
            if(data.getString("key").equalsIgnoreCase("Lampiran:")){
                myViewHolder.upload_layout.setVisibility(View.VISIBLE);
                myViewHolder.value.setVisibility(View.GONE);
                myViewHolder.input_layout.setVisibility(View.GONE);

                if(data.has("image_url")){
                    myViewHolder.image_layout.setVisibility(View.VISIBLE);
                    myViewHolder.upload.setVisibility(View.GONE);
                    Picasso.get()
                            .load(data.getString("image_url"))
                            .into(myViewHolder.iv);
                }
                else{
                    myViewHolder.image_layout.setVisibility(View.GONE);
                    myViewHolder.upload.setVisibility(View.VISIBLE);
                }
            }else{
                myViewHolder.upload_layout.setVisibility(View.GONE);
                if(data.has("type") && data.getString("type").equals("input")){
                    myViewHolder.input_layout.setVisibility(View.VISIBLE);
                    if(data.has("input_type") && data.getString("input_type").equals("text")){
                        myViewHolder.input.setVisibility(View.GONE);
                        myViewHolder.input_text.setVisibility(View.VISIBLE);

                        /**
                         * @author Dinda
                         * @note Case CICD 9921 - Contact Us issue when typing
                         */
                        // <code-9921>
                        if(data.has("data") && data.getString("data").equals("shopName")){
                            InputFilter[] fa= new InputFilter[1];
                            fa[0] = new InputFilter.LengthFilter(100);
                            myViewHolder.input_text.setFilters(fa);
                        }
                        // </code-9921>
                    }
                    else{
                        myViewHolder.input.setVisibility(View.VISIBLE);
                        myViewHolder.input_text.setVisibility(View.GONE);

                        // <code-9921>
                        if(data.has("data") && data.getString("data").equals("last6DigitCard")){
                            InputFilter[] fa= new InputFilter[1];
                            fa[0] = new InputFilter.LengthFilter(6);
                            myViewHolder.input.setFilters(fa);
                        }else if(data.has("data")  && data.getString("data").equals("noAccount")){
                            InputFilter[] fa= new InputFilter[1];
                            fa[0] = new InputFilter.LengthFilter(25);
                            myViewHolder.input.setFilters(fa);
                        }
                        // </code-9921>
                    }
                    myViewHolder.value.setVisibility(View.GONE);
                }
                else{
                    myViewHolder.input_layout.setVisibility(View.GONE);
                    myViewHolder.value.setVisibility(View.VISIBLE);
                    myViewHolder.value.setText(data.getString("value"));
                }
            }

            myViewHolder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onUploadClick(i);
                }
            });

            myViewHolder.change_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onChangeImageClick(i);
                }
            });


            myViewHolder.input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // <code-9921>
                    try {
                        on_text_change(s,data,i,myViewHolder.input);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // </code-9921>
                }
            });
            myViewHolder.input_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // <code-9921>
                    try {
                        on_text_change(s,data,i,myViewHolder.input_text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // </code-9921>
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void on_text_change(CharSequence s, JSONObject data, int i, EditText input) throws JSONException {
        String str = s.toString();
        // <code-9921>
        if(data.has("value") && str.length() > 0){
            data.put("value",str);
            clickListener.onInputChange(i);
        }
        // </code-9921>
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }
}