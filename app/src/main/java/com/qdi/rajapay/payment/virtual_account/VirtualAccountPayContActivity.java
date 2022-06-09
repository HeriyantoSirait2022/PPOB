package com.qdi.rajapay.payment.virtual_account;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.contact_us.ContactUsListActivity;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.payment.HowToPayActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @module 5.1 Transaksi
 * @screen 5.1.2.1.1
 * @note detail payment for Virtual Account payment
 */
public class VirtualAccountPayContActivity extends BaseActivity {
    TextView total_price, account_no, finish_time, order_date, order_id;
    ImageView copy_transaction_id;
    Button detail,contact_cs,how_to_pay;
    ImageView copy, bank_icon;
    TextView order_status;
    TextView hour, minute, second;

    JSONObject data_payment, data_payment_header, data_detail;
    OrderData orderData;

    String account_no_data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_account_pay);

        init_toolbar(getResources().getString(R.string.activity_title_detail_transaction));
        try {
            init();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VirtualAccountPaymentDetailContModal modal = new VirtualAccountPaymentDetailContModal();
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        how_to_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VirtualAccountPayContActivity.this, HowToPayActivity.class)
                        .putExtra("payment",data_payment.toString())
                        .putExtra("payment_header", data_payment_header.toString()));
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clip = ClipData.newPlainText("", account_no_data);
                clipboard.setPrimaryClip(clip);
                show_error_message(layout,getResources().getString(R.string.copied_clipboard_label));
            }
        });

        contact_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VirtualAccountPayContActivity.this, ContactUsListActivity.class));
            }
        });

        copy_transaction_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clip = ClipData.newPlainText("", orderData.getIdOrder() );
                clipboard.setPrimaryClip(clip);
                show_error_message(layout,getResources().getString(R.string.copied_clipboard_label));
            }
        });
    }

    private void init() throws JSONException, ParseException {
        total_price = findViewById(R.id.total_price);
        detail = findViewById(R.id.detail);
        copy = findViewById(R.id.copy);
        account_no = findViewById(R.id.account_no);
        contact_cs = findViewById(R.id.contact_cs);
        how_to_pay = findViewById(R.id.how_to_pay);
        bank_icon = findViewById(R.id.bank_icon);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        second = findViewById(R.id.second);
        finish_time = findViewById(R.id.finish_time);
        order_date = findViewById(R.id.order_date);
        order_id = findViewById(R.id.order_id);
        copy_transaction_id = findViewById(R.id.copy_transaction_id);

        orderData = (OrderData) getIntent().getSerializableExtra("data");

        Picasso.get()
                .load(orderData.getImage())
                .into(bank_icon);

        total_price.setText("Rp. "+orderData.getFormattedTotBillAmount());

        order_status = findViewById(R.id.order_status);
        order_status.setBackgroundColor(getResources().getColor(R.color.waiting));
        order_status.setText(getResources().getString(R.string.agency_commition_history_pending));

        SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy (HH:mm)");
        order_date.setText(format_date.format(orderData.getDtOrder() ));
        order_id.setText("Order ID #"+orderData.getIdOrder() );

        get_detail();
    }

    private void get_detail() throws JSONException {
        url = BASE_URL+"/mobile/deposit/topup-va-detail";

        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("idOrder", orderData.getIdOrder() );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data_detail = response_data;

                        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy");
                        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
                        Date end_date = parse_date.parse(data_detail.getString("expiredDate"));
                        Date now = new Date();
                        finish_time.setText(format_date.format(end_date)+" "+format_time.format(end_date));
                        account_no_data = data_detail.getString("noVirtualAcct");
                        account_no.setText(account_no_data);

                        // @note prepare how to pay
                        data_payment_header = new JSONObject();
                        data_payment_header.put("paymentMtd", response_data.getString("paymentMtd"));

                        data_payment = new JSONObject();
                        data_payment.put("cdeBank", response_data.getString("cdeBank"));
                        data_payment.put("noAcct", response_data.getString("noVirtualAcct")); ;
                        data_payment.put("total", orderData.getFormattedTotBillAmount());
                        data_payment.put("image_url", orderData.getImage());

                        new CountDownTimer(end_date.getTime() - now.getTime(), 1000) {
                            @Override
                            public void onTick(long millis) {
                                String hours = String.format(Locale.getDefault(), "%02d",
                                        TimeUnit.MILLISECONDS.toHours(millis));
                                String minutes = String.format(Locale.getDefault(), "%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
                                String seconds = String.format(Locale.getDefault(), "%02d",
                                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                hour.setText(hours);
                                minute.setText(minutes);
                                second.setText(seconds);
                            }

                            @Override
                            public void onFinish() {
                                hour.setText("00");
                                minute.setText("00");
                                second.setText("00");
                            }
                        }.start();
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    error_handling(error, layout);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        consume_api(jsonObjectRequest);
    }
}