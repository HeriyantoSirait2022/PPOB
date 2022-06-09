package com.qdi.rajapay.payment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.coupon.transaction.CouponListActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @module 4.1.3 Virtual Account
 * @screen 4.1.3.8+
 *
 * @module 4.10 Prabayar Data
 * @screen 4.10.7+
 *
 * @module 4.11 Prabayar Pulsa
 * @screen 4.11.7+
 */
public class PaymentDetailModal extends BottomSheetDialogFragment {
    LinearLayout content_layout;
    Button coupon, pay_now;
    ImageView image;

    ChoosePaymentActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_detail_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(!parent.check_selected_option())
                        parent.show_error_message(parent.layout,getResources().getString(R.string.choose_payment_not_choosen));
                    else {
                        JSONObject data_header = parent.array_header.get(parent.index_header);

                        JSONObject data1 = new JSONObject();
                        if(parent.getIntent().hasExtra("type") && parent.getIntent().getStringExtra("type").equals("deposit")) {
                            data1.put("categoryCoupon", data_header.getString("category"));
                            data1.put("typeCoupon", data_header.getString("type"));
                            data1.put("idProduct", data_header.getJSONArray("arr").getJSONObject(parent.index_child).getString("idBank"));
                        }
                        else{
                            data1.put("categoryCoupon", data_header.getString("category"));
                            data1.put("typeCoupon", data_header.getString("type"));
                            data1.put("idProduct", data_header.getString("idProduct"));
                            if(parent.data.getJSONObject("invoice_data").getString("productCategory").equals("PREPAID"))
                                data1.put("idProductDetail", parent.data.getJSONObject("invoice_data").getInt("idProductDetail"));
                        }

                        parent.setUpSelected();

                        parent.startActivityForResult(new Intent(parent, CouponListActivity.class)
                                .putExtra("data",data1.toString())
                                .putExtra("admin_fee", parent.admin_fee)
                                .putExtra("total",parent.clean_total), parent.REQUEST_COUPON);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });

        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                try {
                    parent.buy_now();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view) throws JSONException {
        content_layout = view.findViewById(R.id.content_layout);
        coupon = view.findViewById(R.id.coupon);
        pay_now = view.findViewById(R.id.pay_now);
        image = view.findViewById(R.id.image);

        parent = (ChoosePaymentActivity) getActivity();

        JSONArray array = parent.data.getJSONArray("breakdown_price");
        Double total = 0d;
        for(int x=0;x<array.length();x++){
            add_view(array.getJSONObject(x),content_layout);
            total += array.getJSONObject(x).getDouble("price");
        }

        JSONObject data = new JSONObject();
        data.put("title",getResources().getString(R.string.total_price_label));
        data.put("price",total);
        add_view(data,content_layout);

        /**
         * @author Jesslyn
         * @note 1. change to image_url
         *       2. use picasso
         */
        // <code>
        if(parent.data.getJSONObject("data").has("image_url") && !BaseActivity.isNullOrEmpty(parent.data.getJSONObject("data").getString("image_url"))) {
            Picasso.get()
                    .load(parent.data.getJSONObject("data").getString("image_url"))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit().centerInside()
                    .into(image);
        }else{
            if(parent.getIntent().hasExtra("type") && parent.getIntent().getStringExtra("type").equals("deposit")) {
                image.setImageDrawable(parent.getDrawable(R.drawable.ic_money_green));
            }else{
                // default image for prepaid, postpaid
                image.setImageDrawable(parent.getDrawable(R.drawable.ic_default_4));
            }
        }
        // </code>
    }

    private void add_view(JSONObject data, LinearLayout layout) throws JSONException {
        View view1 = getLayoutInflater().inflate(R.layout.payment_detail_item,null);

        TextView title = view1.findViewById(R.id.title);
        TextView price = view1.findViewById(R.id.price);
        TextView detail = view1.findViewById(R.id.detail);

        title.setText(data.has("cdeCoupon") ? data.getString("cdeCoupon") : data.getString("title"));
        price.setText("Rp. "+parent.formatter.format(data.getDouble("price")));
        if(data.getString("title").equals(getResources().getString(R.string.total_price_label))) {
            price.setTextColor(getResources().getColor(R.color.colorPrimary));
            price.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if(data.isNull("detail"))
            detail.setVisibility(View.GONE);
        else{
            detail.setVisibility(View.VISIBLE);
            detail.setText(data.getString("detail"));
        }

        layout.addView(view1);
    }
}