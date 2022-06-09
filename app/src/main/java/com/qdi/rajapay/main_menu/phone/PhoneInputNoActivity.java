package com.qdi.rajapay.main_menu.phone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.MainMenuConfirmationActivity;
import com.qdi.rajapay.main_menu.postpaid_data.PostpaidDataPriceListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @module 4.19 Telkom
 * @screen 4.19.2
 */
public class PhoneInputNoActivity extends BaseActivity {
    ImageView image;
    EditText no;
    Button next;

    JSONObject data, data_provider;
    Double admin_cost = 0d, price = 0d;
    ArrayList<JSONObject> array = new ArrayList<>();
    List<JSONObject> arr_detail_child = new ArrayList<>();

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
        setContentView(R.layout.activity_main_menu_phone_input_no);

        init_toolbar(getResources().getString(R.string.home_phone));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // @CFI
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });
    }

    /**
     * @author Jesslyn
     * @note set function onNext
     */
    // <code>
    private void onNext(){
        if(no.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.main_menu_tv_empty_no));
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
        no = findViewById(R.id.no);

        // @CFI
        next = findViewById(R.id.next);

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
                startActivity(new Intent(PhoneInputNoActivity.this, PhonePriceListActivity.class)
                        .putExtra("asd", all_provider.toString()));
            }
        });
        // </code>

        /**
         * @author Jesslyn
         * @note init custom keyboard
         */
        // <code>
        initNumpadKeyboard(no, new NumpadKeyboardSubmit() {
            @Override
            public void onSubmit() {
                onNext();
            }
        });
        // </code>
    }

    private void add_key_value_information(String title, String value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",title);
        jsonObject.put("value",value);
        array.add(jsonObject);
    }

    private void add_key_value_detail_information(String title, String value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",title);
        jsonObject.put("value",value);
        arr_detail_child.add(jsonObject);
    }

    private void get_information() throws JSONException {
        array.clear();

        add_key_value_information("ID Pel",data.getString("noTelkom"));
        add_key_value_information("Nama",data.getString("nmCust"));
        add_key_value_information("Layanan",data.getString("nameProduct"));
        add_key_value_information("DIVRE/DATEL",data.getString("divre") + " / " + data.getString("datel") );
        add_key_value_information("Jumlah Tagihan",data.getString("jumlahTagihan"));

        add_key_value_information("Tagihan","Rp. "+formatter.format(data.getDouble("price")));
        add_key_value_information("Biaya Admin","Rp. "+formatter.format(admin_cost));
        add_key_value_information("Total Tagihan","Rp. "+formatter.format(data.getDouble("price") + admin_cost));

        List<JSONArray> arr_detail = new ArrayList<>();
        for(int x = 0; x < data.getJSONArray("tagihan").length(); x++){
            arr_detail_child.clear();
            JSONObject detail = data.getJSONArray("tagihan").getJSONObject(x);
            add_key_value_detail_information("Periode",detail.getString("periode"));
            add_key_value_detail_information("- Tagihan","Rp. "+formatter.format(detail.getDouble("nilaiTagihan")));
            add_key_value_detail_information("- Admin","Rp. "+formatter.format(detail.getDouble("admin")));
            add_key_value_detail_information("- Total","Rp. "+formatter.format(detail.getDouble("total")));

            arr_detail.add(new JSONArray(arr_detail_child.toString()));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key","Detail Tagihan");
        jsonObject.put("type","detail_array");
        jsonObject.put("value",new JSONArray(arr_detail.toString()));
        array.add(jsonObject);
    }

    private void get_data() throws JSONException {
        url = BASE_URL+"/mobile/postpaid/telkom-info";
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("productType", "TELKOM");
        param.put("noTelkom", no.getText().toString() );
        param.put("idProduct", data_provider.getString("idProduct") );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss_wait_modal();
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data = response_data;

                        admin_cost = 0d;
                        price = 0d;
                        for(int x = 0; x < data.getJSONArray("tagihan").length(); x++){
                            admin_cost += data.getJSONArray("tagihan").getJSONObject(x).getDouble("admin");
                            price += data.getJSONArray("tagihan").getJSONObject(x).getDouble("nilaiTagihan");
                        }
                        data.put("price",price);

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
        arr.add(new JSONObject("{\"title\":\""+data_provider.getString("text")+
                "\",\"price\":"+data.getDouble("price")+",\"detail\":\"" + no.getText().toString() + "\"}"));
        // </code>
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+
                "\",\"price\":"+admin_cost+"}"));

        data1.put("data",data);
        data1.put("breakdown_price",new JSONArray(arr.toString()));
        data1.put("information_data",new JSONArray(array.toString()));
        data1.put("data_post",add_data_to_post());
        data1.put("url_post","/mobile/postpaid/telkom-transaction");
        data1.put("url_pay","/mobile/postpaid/telkom-payment");

        startActivity(new Intent(PhoneInputNoActivity.this, MainMenuConfirmationActivity.class)
                .putExtra("type","phone")
                .putExtra("data",data1.toString()));
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noTelkom",data.getString("noTelkom"));
        jsonObject.put("idProduct",data.getString("idProduct"));
        jsonObject.put("cdeProduct",data.getString("cdeProduct"));
        jsonObject.put("productType",data.getString("productType"));
        jsonObject.put("nameProduct",data.getString("nameProduct"));
        jsonObject.put("productCategory",data.getString("productCategory"));
        jsonObject.put("image",data.getString("image"));
        jsonObject.put("adminFee",data.getDouble("adminFee"));
        jsonObject.put("typeTxn","POSTPAIDTELKOM");
        jsonObject.put("nmCust",data.getString("nmCust"));
        jsonObject.put("kodeArea",data.getString("kodeArea"));
        jsonObject.put("divre",data.getString("divre"));
        jsonObject.put("datel",data.getString("datel"));
        jsonObject.put("jumlahTagihan",data.getString("jumlahTagihan"));
        jsonObject.put("tagihan",data.getJSONArray("tagihan"));
        jsonObject.put("totalTagihan",data.getDouble("totalTagihan"));
        jsonObject.put("idRef",data.getString("idRef"));
        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));

        return jsonObject;
    }
}