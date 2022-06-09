package com.qdi.rajapay.payment.alfamart;

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
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.contact_us.ContactUsListActivity;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.ReportData;
import com.qdi.rajapay.model.enums.TransactionType;
import com.qdi.rajapay.order.OrderDetailActivity;
import com.qdi.rajapay.payment.HowToPayActivity;

import org.json.JSONArray;
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

public class AlfamartPayActivity extends BaseActivity implements APICallback.ItemCallback<OrderData> {
    TextView total_price, order_status, finish_time;
    Button how_to_pay, contact_cs, copy_price;
    ImageView copy_order_no;
    TextView order_no;
    TextView hour, minute, second;
    ImageView copy_transaction_id;
    TextView order_id,order_date;

    JSONObject data, data_payment, data_payment_header, order_data, coupon_data = new JSONObject(),
            data_detail = new JSONObject();
    String account_no_data = "111930040032432";
    Double total_price_data = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alfamart_pay);

        init_toolbar(getResources().getString(R.string.activity_title_detail_transaction));
        try {
            init();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        how_to_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @author Liao Mei
                 * @note add no acct to data_payment array
                 */
                // <code>
                try{
                    data_payment.put("total", total_price_data);
                    data_payment.put("noAcct", account_no_data);
                }catch(JSONException e){
                    e.printStackTrace();
                }
                // </code>
                /**
                 * @author Jesslyn
                 * @note change intent extra from variable, add intent extra
                 */
                // <code>
                startActivity(new Intent(AlfamartPayActivity.this, HowToPayActivity.class)
                        .putExtra("payment",data_payment.toString())
                        .putExtra("payment_header", data_payment_header.toString()));
                // </code>
            }
        });

        contact_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlfamartPayActivity.this, ContactUsListActivity.class));
            }
        });

        copy_order_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clip = ClipData.newPlainText("", data_detail.getString("paymentCode"));
                    clipboard.setPrimaryClip(clip);
                    show_error_message(layout,getResources().getString(R.string.copied_clipboard_label));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        copy_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @auhtor Jesslyn
                 * @note fixing issue copy price : false copied price
                 */
                // <code>
                clip = ClipData.newPlainText("", formatter.format(total_price_data));
                // </code>
                clipboard.setPrimaryClip(clip);
                show_error_message(layout,getResources().getString(R.string.copied_clipboard_label));
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
        how_to_pay = findViewById(R.id.how_to_pay);
        contact_cs = findViewById(R.id.contact_cs);
        order_no = findViewById(R.id.order_no);
        copy_price = findViewById(R.id.copy_price);
        copy_order_no = findViewById(R.id.copy_order_no);
        order_status = findViewById(R.id.order_status);
        total_price = findViewById(R.id.total_price);
        order_date = findViewById(R.id.order_date);
        order_id = findViewById(R.id.order_id);
        copy_transaction_id = findViewById(R.id.copy_transaction_id);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        second = findViewById(R.id.second);
        finish_time = findViewById(R.id.finish_time);

        data = new JSONObject(getIntent().getStringExtra("data"));
        data_payment = new JSONObject(getIntent().getStringExtra("payment"));
        data_payment_header = new JSONObject(getIntent().getStringExtra("payment_header"));
        order_data = new JSONObject(getIntent().getStringExtra("order_data"));
        coupon_data = new JSONObject(getIntent().getStringExtra("coupon_data"));

        // <code>
        set_total_price();
        total_price.setText(formatter.format(total_price_data));
        // </code>

        order_status = findViewById(R.id.order_status);
        order_status.setBackgroundColor(getResources().getColor(R.color.waiting));
        order_status.setText(getResources().getString(R.string.agency_commition_history_pending));

        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy (HH:mm)");

        order_date.setText(format_date.format(parse_date.parse(data.getJSONObject("invoice_data").getString("dtOrder"))));
        order_id.setText("Order ID #"+data.getJSONObject("invoice_data").getString("idOrder"));

        submit();
    }

    private void set_total_price() throws JSONException {
        Double total = 0d;
        JSONArray array = data.getJSONArray("breakdown_price");
        for(int x=0;x<array.length();x++)
            total += array.getJSONObject(x).getDouble("price");
        total_price.setText(formatter.format(total));
        total_price_data = total;
        order_data.put("totBillAmount", total_price_data);
    }

    private void submit() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("idOrder",order_data.getString("idOrder"));
        arr.put("dtOrder",order_data.getString("dtOrder"));
        arr.put("typeTxn","TOPUPDEPOSITRETAIL");
        arr.put("paymentMtd",data_payment_header.getString("paymentMtd"));
        arr.put("idBank",data_payment.getString("idBank"));
        arr.put("cdeBank",data_payment.getString("cdeBank"));
        if(coupon_data.has("idCoupon")) {
            arr.put("cdeCoupon", coupon_data.getString("cdeCoupon"));
            arr.put("couponAmount", coupon_data.getDouble("price") * -1);
        }
        arr.put("adminFee",data_payment.getDouble("adminFee"));
        arr.put("billAmount",order_data.getDouble("billAmount"));
        arr.put("totBillAmount",order_data.getDouble("totBillAmount"));
        arr.put("expired",10);
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/deposit/topup-retail";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        get_detail();
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
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

    private void get_detail() throws JSONException {
        url = BASE_URL+"/mobile/deposit/topup-retail-detail";
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("idOrder", order_data.getString("idOrder") );

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
                        Date start_date = parse_date.parse(data_detail.getString("dtOrder"));
                        Date end_date = parse_date.parse(data_detail.getString("expiredDate"));
                        finish_time.setText(format_date.format(end_date)+" "+format_time.format(end_date));
                        total_price_data = data_detail.getDouble("totBillAmount");
                        total_price.setText(formatter.format(total_price_data));

                        order_no.setText(data_detail.getString("paymentCode"));
                        account_no_data = data_detail.getString("paymentCode");

                        new CountDownTimer(end_date.getTime() - start_date.getTime(), 1000) {
                            @Override
                            public void onTick(long millis) {
                                // , , ;
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

    /**
     * @author Jesslyn
     * @note handling back action and redirect to order detail
     */
    // <code>
    OrderData selected;
    ReportData reportData;

    @Override
    public void onBackPressed() {
        TransactionAPI api;
        api = new TransactionAPI(this, user_SP);

        try {
            reportData = new ReportData();
            reportData.setId(order_data.getString("idOrder"));
            reportData.setTypeTxn(TransactionType.TOPUP_DEPOSIT_RETAIL);

            selected = new OrderData(reportData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        api.getOrderDetail(selected, AlfamartPayActivity.this);
    }

    @Override
    public void onItemResponseSuccess(OrderData item, String message) {
        item.setIdTxnAgen(selected.getIdTxnAgen());
        finish();
        startActivity(new Intent(AlfamartPayActivity.this, OrderDetailActivity.class)
                .putExtra("data",item));
    }
    // </code>
}