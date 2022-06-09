package com.qdi.rajapay.payment.virtual_account;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.contact_us.ContactUsListActivity;
import com.qdi.rajapay.payment.ChoosePaymentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class VirtualAccountConfirmationActivity extends BaseActivity {
    LinearLayout content_layout;
    TextView total;
    Button cancel,confirm,contact_cs;
    ImageView copy_transaction_id;
    TextView order_id,order_date, order_status;

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_account_confirmation);

        try {
            init();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VirtualAccountConfirmationActivity.this, ChoosePaymentActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contact_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VirtualAccountConfirmationActivity.this, ContactUsListActivity.class));
            }
        });

        copy_transaction_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clip = ClipData.newPlainText("", String.valueOf(data.getJSONObject("invoice_data").getString("idOrder")));
                    clipboard.setPrimaryClip(clip);
                    show_error_message(layout,getResources().getString(R.string.copied_clipboard_label));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException, ParseException {
        content_layout = findViewById(R.id.content_layout);
        total = findViewById(R.id.total);
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);
        contact_cs = findViewById(R.id.contact_cs);
        order_status = findViewById(R.id.order_status);
        order_date = findViewById(R.id.order_date);
        order_id = findViewById(R.id.order_id);
        copy_transaction_id = findViewById(R.id.copy_transaction_id);

        data = new JSONObject(getIntent().getStringExtra("data"));
        JSONArray array = data.getJSONArray("breakdown_price");
        Double total_data = 0d;
        for(int x=0;x<array.length();x++){
            add_view(array.getJSONObject(x),content_layout);
            total_data += array.getJSONObject(x).getDouble("price");
        }

        total.setText("Rp. "+formatter.format(total_data));

        order_status.setBackgroundColor(getResources().getColor(R.color.waiting));
        order_status.setText(getResources().getString(R.string.agency_commition_history_pending));

        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy (HH:mm)");

        order_date.setText(format_date.format(parse_date.parse(data.getJSONObject("invoice_data").getString("dtOrder"))));
        order_id.setText("Order ID #"+data.getJSONObject("invoice_data").getString("idOrder"));
    }

    private void add_view(JSONObject data, LinearLayout layout) throws JSONException {
        View view1 = getLayoutInflater().inflate(R.layout.payment_detail_item,null);

        TextView title = view1.findViewById(R.id.title);
        TextView price = view1.findViewById(R.id.price);
        TextView detail = view1.findViewById(R.id.detail);
//        price.setTextColor(null);

        title.setText(data.getString("title"));
        price.setText("Rp. "+formatter.format(data.getDouble("price")));
        if(!data.getString("title").equals(getResources().getString(R.string.total_price_label)))
            price.setTextColor(getResources().getColor(R.color.black));
        if(data.isNull("detail"))
            detail.setVisibility(View.GONE);
        else{
            detail.setVisibility(View.VISIBLE);
            detail.setText(data.getString("detail"));
        }

        layout.addView(view1);
    }
}