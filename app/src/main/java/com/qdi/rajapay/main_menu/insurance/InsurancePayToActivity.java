package com.qdi.rajapay.main_menu.insurance;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

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
import java.util.List;
import java.util.Map;

/**
 * @module 4.16 BPJS
 * @screen 4.16.7
 */
public class InsurancePayToActivity extends BaseActivity {
    RecyclerView list;

    InsurancePayToAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    JSONObject data, fee_data, user_data, selected_data;
    Double admin_cost = 0d;
    ArrayList<JSONObject> array_info = new ArrayList<>();
    List<JSONObject> arr_detail_child = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_insurance_pay_to);

        init_toolbar(getResources().getString(R.string.home_insurance_2));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // @CFI
        adapter.setOnItemClickListener(new InsurancePayToAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    selected_data = array.get(position);
                    get_data();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        prepare_data();
        adapter = new InsurancePayToAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        fee_data = new JSONObject(getIntent().getStringExtra("fee_data"));
        user_data = new JSONObject(user_SP.getString("user","{}"));

        /**
         * @author : Jesslyn
         * @note : add Price Tag Icon
         */
        // </code>
        preparePriceTag(this, InsurancePriceListActivity.class);
        // </code>
    }

    private void prepare_data() throws JSONException {
        for(int x=0;x<12;x++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", (x+1) + " " + getResources().getString(R.string.main_menu_insurance_pay_to_item));
            jsonObject.put("data",x+1);
            jsonObject.put("is_trouble",false);
            array.add(jsonObject);
        }
    }

    private void add_key_value_information(String title, String value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",title);
        jsonObject.put("value",value);
        array_info.add(jsonObject);
    }

    private void add_key_value_detail_information(String title, String value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",title);
        jsonObject.put("value",value);
        arr_detail_child.add(jsonObject);
    }

    private void get_information() throws JSONException {
        array_info.clear();

        add_key_value_information("No VA",data.getString("noBpjs"));
        add_key_value_information("Nama",data.getString("nmCust"));
        add_key_value_information("Nama Cabang",data.getString("namaCabang"));
        add_key_value_information("Jumlah Periode",data.getString("jumlahPeriode") + " Bulan");
        add_key_value_information("Jumlah Peserta",data.getString("jumlahPeserta") + " Orang");
        add_key_value_information("Tagihan","Rp. "+formatter.format(data.getDouble("price")));
        add_key_value_information("Biaya Admin","Rp. "+formatter.format(admin_cost));
        add_key_value_information("Total Tagihan","Rp. "+formatter.format(data.getDouble("price") + admin_cost));

        List<JSONArray> arr_detail = new ArrayList<>();
        for(int x = 0; x < data.getJSONArray("detailPeserta").length(); x++){
            arr_detail_child.clear();
            JSONObject detail = data.getJSONArray("detailPeserta").getJSONObject(x);
            add_key_value_detail_information("No VA",detail.getString("noPeserta"));
            add_key_value_detail_information("Nama",detail.getString("nama"));
            add_key_value_detail_information("Premi","Rp. "+formatter.format(detail.getDouble("premi")));
            add_key_value_detail_information("Saldo","Rp. "+formatter.format(detail.getDouble("saldo")));

            arr_detail.add(new JSONArray(arr_detail_child.toString()));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key","Detail Peserta");
        jsonObject.put("type","detail_array");
        jsonObject.put("value",new JSONArray(arr_detail.toString()));
        array_info.add(jsonObject);
    }

    private void get_data() throws JSONException {
        url = BASE_URL+"/mobile/postpaid/bpjs-info";
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("productType", "BPJSKES");
        param.put("noBpjs", getIntent().getStringExtra("no") );
        param.put("noHp", user_data.getString("noHp") );
        param.put("jumlahPeriode", selected_data.getString("data") );
        param.put("idProduct", fee_data.getString("idProduct") );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss_wait_modal();
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        response_data.put("price",response_data.getDouble("tagihan"));
                        data = response_data;
                        admin_cost = response_data.has("admin") ?
                                fee_data.getDouble("adminFee") + response_data.getDouble("admin") :
                                fee_data.getDouble("adminFee");

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
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.home_insurance_bill)+
                "\",\"price\":"+data.getDouble("price")+",\"detail\":\"" + data.getString("noBpjs") + "\"}"));
        // </code>
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+
                "\",\"price\":"+admin_cost+"}"));

        data1.put("data",data);
        data1.put("breakdown_price",new JSONArray(arr.toString()));
        data1.put("information_data",new JSONArray(array_info.toString()));
        data1.put("data_post",add_data_to_post());
        data1.put("url_post","/mobile/postpaid/bpjs-transaction");
        data1.put("url_pay","/mobile/postpaid/bpjs-payment");

        startActivity(new Intent(InsurancePayToActivity.this, MainMenuConfirmationActivity.class)
                .putExtra("type","insurance")
                .putExtra("data",data1.toString()));
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noBpjs",data.getString("noBpjs"));
        jsonObject.put("idProduct",data.getDouble("idProduct"));
        jsonObject.put("cdeProduct",data.getString("cdeProduct"));
        jsonObject.put("productType",data.getString("productType"));
        jsonObject.put("nameProduct",data.getString("nameProduct"));
        jsonObject.put("productCategory",data.getString("productCategory"));
        jsonObject.put("image",data.getString("image"));
        jsonObject.put("adminFee",data.getDouble("adminFee"));
        jsonObject.put("typeTxn","POSTPAIDBPJS");
        jsonObject.put("nmCust",data.getString("nmCust"));
        jsonObject.put("namaCabang",data.getString("namaCabang"));
        jsonObject.put("jumlahPeriode",data.getString("jumlahPeriode"));
        jsonObject.put("jumlahPeserta",data.getDouble("jumlahPeserta"));
        jsonObject.put("detailPeserta",data.getJSONArray("detailPeserta"));
        jsonObject.put("customerData",data.getString("customerData"));
        jsonObject.put("tagihan",data.getDouble("tagihan"));
        jsonObject.put("admin",data.getDouble("admin"));
        jsonObject.put("total",data.getDouble("total"));
        jsonObject.put("idRef",data.getString("idRef"));
        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));

        return jsonObject;
    }
}