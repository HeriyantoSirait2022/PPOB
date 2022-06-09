package com.qdi.rajapay.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.ReportData;
import com.qdi.rajapay.model.enums.ResponseCode;
import com.qdi.rajapay.model.enums.TransactionType;
import com.qdi.rajapay.order.OrderDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * @module 4.10 Prabayar Data
 * @screen 4.10.8+
 *
 * @module 4.11 Prabayar - Pulsa Seluler
 * @screen 4.11.8+
 *
 * @module 4.12 Prabayar - Token PLN
 * @screen 4.12.9+
 *
 * @module 4.13 Pascabayar Seluler
 * @screen 4.13.9+
 *
 * @module 4.14 Pascabayar - Tagihan PLN
 * @screen 4.14.8+
 *
 * @module 4.15 Pascabayar - PGN
 * @screen 4.15.8+
 *
 * @module 4.16 Pascabayar - BPJS
 * @screen 4.16.9+
 *
 * @module 4.17 Pascabayar - PDAM
 * @screen 4.17.9+
 *
 * @module 4.18 Pascabayar - TV Berlangganan
 * @screen 4.18.9+
 *
 * @module 4.19 Pascabayar - Telkom
 * @screen 4.19.9+
 *
 * @module 4.20 Pascabayar - Multifinance
 * @screen 4.20.9+
 *
 */

public class SuccessActivity extends BaseActivity {
    Button back_to_home, print;
    ImageView icon;
    TextView title, detail;

    TransactionAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        init();

        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_edit_SP.putString("bottom_main_menu","home");
                user_edit_SP.commit();

                startActivity(new Intent(SuccessActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("data"));

                    ReportData reportData = new ReportData(
                            jsonObject.getJSONObject("invoice_request").getString("idOrder"),
                            new Date(),
                            TransactionType.fromString(jsonObject.getJSONObject("invoice_request").getString("typeTxn")),
                            0,0
                    );
                    OrderData orderData = new OrderData(reportData);
                    // @notes : get order detail to feeding activity 5.1.2
                    api.getOrderDetail(orderData, new APICallback.ItemCallback<OrderData>() {
                        @Override
                        public void onItemResponseSuccess(OrderData item, String message) {
                            startActivity(new Intent(SuccessActivity.this, OrderDetailActivity.class)
                                    .putExtra("data",item));
                        }

                        @Override
                        public void onAPIResponseFailure(VolleyError error) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(){
        back_to_home = findViewById(R.id.back_to_home);
        print = findViewById(R.id.print);
        icon = findViewById(R.id.icon);
        title = findViewById(R.id.title);
        detail = findViewById(R.id.detail);

        api = new TransactionAPI(this, user_SP);


        /**
         * @author : Jesslyn
         * @note : 1. Change behavior for handing intent extras. if there weren't any extras, then get data from server
         *         2. Case TDD 12491 - Handling manual advice data
         */
        // <code>
        String extras = (String) isExtras(getIntent(), "responseCode");
        if(!isNullOrEmpty(extras) && extras.equalsIgnoreCase(ResponseCode.MANUAL_ADVICE.toString())){
            // manual advice
            setDrawable(icon, R.drawable.ic_icon_manual);
            title.setText(getStr(R.string.payment_manual_title));
            detail.setText(getStr(R.string.payment_manual_subtitle));
            print.setText(getStr(R.string.payment_manual_print));
        }else if(!isNullOrEmpty(extras) && extras.equalsIgnoreCase(ResponseCode.OTOMAX_PENDING.toString())){
            /**
             * @author Jesslyn
             * @patch FR19022
             * @notes otomax pending display scenario
             */
            // <code>
            setDrawable(icon, R.drawable.ic_icon_manual);
            title.setText(getStr(R.string.payment_pending_title));
            detail.setText(getStr(R.string.payment_pending_subtitle));
            print.setText(getStr(R.string.payment_manual_print));
            // </code>
        }else{
            // always success
            setDrawable(icon, R.drawable.ic_icon_success);
            title.setText(getStr(R.string.payment_success_title));
            detail.setText(getStr(R.string.payment_success_subtitle));
            print.setText(getStr(R.string.payment_success_print));
        }
        // </code>
    }

    @Override
    public void onBackPressed() {
        user_edit_SP.putString("bottom_main_menu","home");
        user_edit_SP.commit();

        startActivity(new Intent(SuccessActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}