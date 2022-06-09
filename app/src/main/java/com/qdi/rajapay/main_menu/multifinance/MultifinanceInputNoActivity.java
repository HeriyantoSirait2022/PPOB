package com.qdi.rajapay.main_menu.multifinance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.qdi.rajapay.model.OrderData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.20 Multifinance
 * @screen 4.20.2
 */
public class MultifinanceInputNoActivity extends BaseActivity {
    ImageView image;
    EditText no;
    Button next;

    JSONObject data,data_provider,data_sub_service;
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
        setContentView(R.layout.activity_main_menu_multifinance_input_no);

        init_toolbar(getResources().getString(R.string.home_multifinance));
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

        //@CFI
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
                startActivity(new Intent(MultifinanceInputNoActivity.this, MultifinancePriceListActivity.class)
                        .putExtra("data_provider", all_provider.toString()));
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

    private void get_information() throws JSONException {
        array.clear();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key","Layanan");
        jsonObject.put("value",data.getString("nameProduct"));
        array.add(jsonObject);

        if(data.getString("cdeProduct").equals("FNCOLUMD") ||
                data.getString("cdeProduct").equals("FNWOMD")) {
            jsonObject = new JSONObject();
            jsonObject.put("key","ID Pel");
            jsonObject.put("value",data.getString("noContract"));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Nama");
            jsonObject.put("value",data.getString("nmCust"));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Jatuh Tempo");
            jsonObject.put("value", data.getString("jatuhTempo"));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Angsuran Ke");
            jsonObject.put("value", data.getString("angsuranKe"));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Tagihan");
            // jsonObject.put("value","Rp. "+formatter.format(data.getDouble("tagihan")));
            jsonObject.put("value","Rp. "+formatter.format(data.getDouble("tagihan") - data.getDouble("admin") ));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Biaya Admin");
            jsonObject.put("value","Rp. "+formatter.format(admin_cost));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Total Tagihan");
            jsonObject.put("value","Rp. "+formatter.format(data.getDouble("totalBayar") + data.getDouble("adminFee") ));
            array.add(jsonObject);
        }
        else if(data.getString("cdeProduct").equals("FNMAF") ||
                data.getString("cdeProduct").equals("FNMEGA")){
            jsonObject = new JSONObject();
            jsonObject.put("key", "Provider");
            jsonObject.put("value", OrderData.isNullThenConvert(data.getString("provider")) );
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Cabang");
            jsonObject.put("value", OrderData.isNullThenConvert(data.getString("cabang")) );
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","ID Pel");
            jsonObject.put("value",data.getString("noContract"));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Nama");
            jsonObject.put("value",data.getString("nmCust"));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Alamat");
            jsonObject.put("value", OrderData.isNullThenConvert(data.getString("alamat")) );
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Objek");
            jsonObject.put("value",  OrderData.isNullThenConvert(data.getString("itemName")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "No Rangka");
            jsonObject.put("value",  OrderData.isNullThenConvert(data.getString("noRangka")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "No Polisi");
            jsonObject.put("value",  OrderData.isNullThenConvert(data.getString("noPol")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Periode \nPembayaran Terakhir");
            jsonObject.put("value",  OrderData.isNullThenConvert(data.getString("lastPaidPeriod")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Batas Waktu \nPembayaran Terakhir");
            jsonObject.put("value",  OrderData.isNullThenConvert(data.getString("lastPaidDueDate")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key", "Tenor");
            jsonObject.put("value",  OrderData.isNullThenConvert(data.getString("tenor")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Tagihan");
            jsonObject.put("value","Rp. "+formatter.format(data.getDouble("billAmount") - data.getDouble("admin")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Denda");
            jsonObject.put("value","Rp. "+formatter.format(data.getDouble("penaltyBill")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Biaya Admin");
            jsonObject.put("value","Rp. "+formatter.format(admin_cost));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Total Tagihan");
            jsonObject.put("value","Rp. "+formatter.format(data.getDouble("billAmount") +
                    data.getDouble("penaltyBill") + data_provider.getDouble("adminFee")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Minimal Pembayaran");
            jsonObject.put("value","Rp. "+formatter.format(data.getDouble("minPayment") + data_provider.getDouble("adminFee")));
            array.add(jsonObject);

            jsonObject = new JSONObject();
            jsonObject.put("key","Maksimal Pembayaran");
            jsonObject.put("value","Rp. "+formatter.format(data.getDouble("maxPayment") + data_provider.getDouble("adminFee")));
            array.add(jsonObject);
        }
    }

    private void get_data() throws JSONException {
        url = BASE_URL+(data_provider.has("url_info") ?
                data_provider.getString("url_info") :
                "/mobile/postpaid/multifinance-info");
        Log.d("url",url);
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("productType", "MULTIFIN");
        param.put("noContract", no.getText().toString() );
        param.put("idProduct", data_provider.getString("idProduct") );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss_wait_modal();
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data = response_data;
                        admin_cost = response_data.has("admin") ?
                                data_provider.getDouble("adminFee") + response_data.getDouble("admin") :
                                data_provider.getDouble("adminFee");
                        data.remove("type");
                        data.put("typeTxn","POSTPAIDMULTIFINANCE");
                        if(data.getString("cdeProduct").equals("FNCOLUMD") ||
                                data.getString("cdeProduct").equals("FNWOMD")) {
                            /**
                             * @author Jesslyn
                             * @note change price calculation using FNMAF and FNMEGA scenarion
                             *       comment this code if you want to rollback
                             */
                            // data.put("price", data.getDouble("totalBayar"));
                            data.put("price", data.getDouble("totalBayar") - data.getDouble("admin"));
                        }else if(data.getString("cdeProduct").equals("FNMAF") ||
                                data.getString("cdeProduct").equals("FNMEGA")) {

                            // admin = admin from vendor
                            // adminFee = admin from rajapay
                            data.put("price", data.getDouble("billAmount") - data.getDouble("admin"));
                            data.put("totalBayar",data.getDouble("minPayment"));
                        }



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

        if((data.has("minPayment") &&
                data.getDouble("minPayment") == data.getDouble("maxPayment")) ||
                !data.has("minPayment")) {
            /**
             * @author Jesslyn
             * @note add detail
             */
            // <code>
            arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.home_multifinance) + " " + data_provider.getString("text") +
                    "\",\"price\":"+data.getDouble("price")+",\"detail\":\"" + no.getText().toString() + "\"}"));
            // </code>
            arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.admin_cost_label) +
                    "\",\"price\":" + admin_cost + "}"));
        }
        else{
            arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.home_multifinance) + " " + data_provider.getString("text") +
                    "\",\"price\":" + data.getDouble("price") + ",\"detail\":\"" + no.getText().toString() + "\",\"type\":\"min\"}"));
            arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.admin_cost_label) +
                    "\",\"price\":" + admin_cost + ",\"type\":\"admin\"}"));
        }

        Double denda = 0d, tagihan_lain = 0d;
        if(data.has("penaltyBill"))
            denda += data.getDouble("penaltyBill");
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.fine_label)+
                "\",\"price\":"+denda+"}"));

//        data.put("provider",data_provider.toString());
        data1.put("data",data);
        data1.put("breakdown_price",new JSONArray(arr.toString()));
        data1.put("information_data",new JSONArray(array.toString()));
        data1.put("provider",data_provider.toString());
        data1.put("admin_cost",admin_cost);
        data1.put("data_post",data);
        data1.put("url_post",data_provider.has("url_post") ?
                data_provider.getString("url_post") : "/mobile/postpaid/multifinance-transaction");
        data1.put("url_pay",data_provider.has("url_post") ?
                data_provider.getString("url_pay") : "/mobile/postpaid/multifinance-payment");

        startActivity(new Intent(MultifinanceInputNoActivity.this, MainMenuConfirmationActivity.class)
                .putExtra("type","multifinance")
                .putExtra("data",data1.toString()));
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noContract",data.getString("noContract"));
        jsonObject.put("idProduct",data.getString("idProduct"));
        jsonObject.put("cdeProduct",data.getString("cdeProduct"));
        jsonObject.put("productType",data.getString("productType"));
        jsonObject.put("nameProduct",data.getString("nameProduct"));
        jsonObject.put("productCategory",data.getString("productCategory"));
        jsonObject.put("image",data.getString("image"));
        jsonObject.put("adminFee",data.getDouble("adminFee"));
        jsonObject.put("typeTxn","POSTPAIDMULTIFINANCE");
        jsonObject.put("nmCust",data.getString("nmCust"));
        jsonObject.put("jatuhTempo",data.getString("jatuhTempo"));
        jsonObject.put("angsuranKe",data.getString("angsuranKe"));
        jsonObject.put("tagihan",data.getDouble("tagihan"));
        jsonObject.put("admin",data.getDouble("admin"));
        jsonObject.put("totalBayar",data.getDouble("totalBayar"));
        jsonObject.put("fee",data.getDouble("fee"));
        jsonObject.put("idRef",data.getString("idRef"));
        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));

        return jsonObject;
    }
}