package com.qdi.rajapay.contact_us;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.enums.ProductType;
import com.qdi.rajapay.model.enums.TransactionStatus;
import com.qdi.rajapay.model.enums.TransactionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 4.3 Hubungi Kami
 * @screen 4.3.1
 */
public class ContactUsListActivity extends BaseActivity {
    RecyclerView list;

    ContactUsListAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_list);

        init_toolbar(getResources().getString(R.string.activity_title_choose_trouble_transaction));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new ContactUsListAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                JSONObject jsonObject = array.get(position);

                /**
                 * @author Jesslyn
                 * @note possible typeTxn is null (for deposit order id which not yet selected payment method)
                 *       by default set to TOPUP_DEPOSIT_BT
                 */
                // <code>
                try {
                    if(jsonObject.has("typeTxn") && isNullOrEmpty(jsonObject.getString("typeTxn"))){
                        if(jsonObject.has("idOrder") && OrderData.getProductTypeData(jsonObject.getString("idOrder")) == ProductType.DEPOSIT){
                            jsonObject.put("typeTxn", TransactionType.TOPUP_GENERAL.toString());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // </code>
                startActivity(new Intent(ContactUsListActivity.this,ContactUsDetailActivity.class)
                        .putExtra("data",jsonObject.toString()));
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        adapter = new ContactUsListAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        get_data();
    }

    private void get_data() {
        url = BASE_URL+"/mobile/contact-us/transactions";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");

                    if (!response_data.getString("type").equals("Failed")) {
                        for(int x = 0; x < response_data.getJSONArray("arrTxnRslt").length(); x++){
                            SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy");
                            SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
                            JSONObject data_transaction = response_data.getJSONArray("arrTxnRslt").getJSONObject(x);

                            data_transaction.put("title",data_transaction.getString("nmTxn"));
                            data_transaction.put("date",format_date.format(parse_date.parse(data_transaction.getString("dtOrder")))+
                                    " ("+format_time.format(parse_date.parse(data_transaction.getString("dtOrder")))+")");
                            data_transaction.put("id","#"+data_transaction.getString("idOrder"));

                            /**
                             * @author Jesslyn
                             * @note change to use TransactionStatus instead of transalate status
                             */
                            // <code>
                            TransactionStatus ts = TransactionStatus.fromString(data_transaction.getString("status"));
                            data_transaction.put("status", ts.toDisplayString());
                            // </code>
                            array.add(data_transaction);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException | ParseException e) {
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