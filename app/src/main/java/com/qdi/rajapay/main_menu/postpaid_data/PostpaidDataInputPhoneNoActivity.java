package com.qdi.rajapay.main_menu.postpaid_data;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.MainMenuConfirmationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.13 Pascabayar Seluler
 * @screen 4.13.2
 */
public class PostpaidDataInputPhoneNoActivity extends BaseActivity {
    ImageView image;
    EditText phone_no;
    ImageView contact;
    Button next;

    JSONObject data_provider,data;
    Double admin_cost = 0d;
    ArrayList<JSONObject> array = new ArrayList<>();

    /**
     * @author : Jesslyn
     * @note : add 2 variables for handling all provider data and selected provider
     */
    // <code>
    JSONArray all_provider;
    int selected;
    // </code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_postpaid_data_input_phone_no);

        init_toolbar(getResources().getString(R.string.home_postpaid));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_contact();
            }
        });
    }

    /**
     * @author Jesslyn
     * @note set function onNext
     */
    // <code>
    private void onNext(){
        if(phone_no.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.main_menu_postpaid_data_empty_phone_no));
        else {
            try {
                get_data();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // </code>

    private void init() throws JSONException {
        image = findViewById(R.id.image);
        phone_no = findViewById(R.id.phone_no);

        // @CFI
        next = findViewById(R.id.next);
        contact = findViewById(R.id.contact);

        /**
         * @author : Jesslyn
         * @note : change how activity handling data_provider data by sending all provider from 4.13.1 instead of sending selected provider
         */
        // <code>
        all_provider = new JSONArray(getIntent().getStringExtra("data_provider"));
        selected = getIntent().getIntExtra("selected", 0);
        data_provider = all_provider.getJSONObject(selected);
        // </code>

        /**
         * @author : Jesslyn
         * @note : 1. add Price Tag Icon
         *         2. send back all provider data tp 4.13.2
         */
        // </code>
        preparePriceTag(this, PostpaidDataPriceListActivity.class);
        add_ons_view_array.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostpaidDataInputPhoneNoActivity.this, PostpaidDataPriceListActivity.class)
                    .putExtra("data_provider", all_provider.toString()));
            }
        });
        // </code>

        /**
         * @author Jesslyn
         * @note init custom keyboard
         */
        // <code>
        initNumpadKeyboard(phone_no, new NumpadKeyboardSubmit() {
            @Override
            public void onSubmit() {
                onNext();
            }
        });
    }

    private void get_information() throws JSONException {
        array.clear();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key","Layanan");
        jsonObject.put("value",data.getString("nameProduct"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","No Pelanggan");
        jsonObject.put("value",data.getString("noHp"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Nama");
        jsonObject.put("value",data.getString("nmCust"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Periode");
        jsonObject.put("value",data.getString("periode"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Lembar Tagih");
        jsonObject.put("value",data.getString("totBill"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Tagihan");
        jsonObject.put("value","Rp. "+formatter.format(data.getDouble("billAmount")));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Biaya Admin");
        jsonObject.put("value","Rp. "+formatter.format(admin_cost));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Total");
        jsonObject.put("value","Rp. "+formatter.format(data.getDouble("billAmount") + admin_cost));
        array.add(jsonObject);
    }

    private void get_data() throws JSONException {
        url = BASE_URL+"/mobile/postpaid/cellular-info";
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("productType", "CELL");
        param.put("noHp", phone_no.getText().toString() );
        param.put("idProduct", data_provider.getString("idProduct") );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss_wait_modal();
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        response_data.put("price",response_data.getDouble("billAmount"));
                        data = response_data;
                        admin_cost = response_data.has("admin") ?
                                data_provider.getDouble("adminFee") + response_data.getDouble("admin") :
                                data_provider.getDouble("adminFee");

                        redirect_page();
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

    private void redirect_page() throws JSONException {
        get_information();

        ArrayList<JSONObject> arr = new ArrayList<>();
        JSONObject data1 = new JSONObject();
        /**
         * @author Jesslyn
         * @note add detail
         */
        // <code>
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.home_postpaid)+" "+data_provider.getString("text")+
                "\",\"price\":"+data.getDouble("price")+",\"detail\":\"" + phone_no.getText().toString() + "\"}"));
        // </code>
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+
                "\",\"price\":"+admin_cost+"}"));

        data1.put("data",data);
        data1.put("data_provider",data_provider);
        data1.put("breakdown_price",new JSONArray(arr.toString()));
        data1.put("information_data",new JSONArray(array.toString()));
        data1.put("data_post",add_data_to_post());
        data1.put("url_post","/mobile/postpaid/cellular-transaction");
        data1.put("url_pay","/mobile/postpaid/cellular-payment");

        startActivity(new Intent(PostpaidDataInputPhoneNoActivity.this, MainMenuConfirmationActivity.class)
                .putExtra("type","postpaid_data")
                .putExtra("data",data1.toString()));
    }

    private JSONObject add_data_to_post() throws JSONException {
//        JSONObject info = data.getJSONObject("info");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noHp",data.getString("noHp"));
        jsonObject.put("idProduct",data.getString("idProduct"));
        jsonObject.put("cdeProduct",data.getString("cdeProduct"));
        jsonObject.put("productType",data.getString("productType"));
        jsonObject.put("nameProduct",data.getString("nameProduct"));
        jsonObject.put("productCategory",data.getString("productCategory"));
        jsonObject.put("image",data.getString("image"));
        jsonObject.put("adminFee",data.getDouble("adminFee"));
        jsonObject.put("typeTxn","POSTPAIDCELL");
        jsonObject.put("nmCust",data.getString("nmCust"));
        jsonObject.put("periode",data.getString("periode"));
        jsonObject.put("totBill",data.getDouble("totBill"));
        jsonObject.put("billAmount",data.getDouble("billAmount"));
        jsonObject.put("adminFeeVendor",data.getDouble("adminFeeVendor"));
        jsonObject.put("totBillAmountVendor",data.getDouble("totBillAmountVendor"));
        jsonObject.put("idRef",data.getString("idRef"));
        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));

        return jsonObject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CONTACT_INTENT){
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();

                    for(String column:c.getColumnNames()) {
                        if(c.getString(c.getColumnIndex(column)) != null && column.equals("data4"))
                            phone_no.setText(c.getString(c.getColumnIndex(column)));
                    }
                }
            }
        }
    }
}