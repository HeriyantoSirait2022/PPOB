package com.qdi.rajapay.main_menu.electrical_token;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.MainMenuConfirmationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList; 

/**
 * @module 4.12 Prabayar Token PLN
 * @screen 4.12.6
 */
public class ElectricalTokenPriceListActivity extends BaseActivity {
    RecyclerView list;
    TextView service_name;

    JSONObject data, data_selected;

    ElectricalTokenPriceListAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>(), array_information = new ArrayList<>();

    Double admin_cost = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_electrical_token_price_list);

        init_toolbar(getResources().getString(R.string.activity_title_price_list));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new ElectricalTokenPriceListAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    data_selected = array.get(position);
                    if(data_selected.getInt("isProblem") == 0) {
                        admin_cost = data_selected.getDouble("amount") - data_selected.getDouble("vendorAmount");
                        get_information();

                        ArrayList<JSONObject> arr = new ArrayList<>();
                        JSONObject data1 = new JSONObject();

                        /**
                         * @author Jesslyn
                         * @note add detail
                         */
                        // <code>
                        arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.home_electrical_token) +
                                "\",\"price\":" + array.get(position).getDouble("price")+",\"detail\":\"" + data.getString("no")+ "\"}"));
                        // </code>
                        arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.admin_cost_label) +
                        "\",\"price\":" + admin_cost + "}"));

                        data1.put("data", data);
                        data1.put("breakdown_price", new JSONArray(arr.toString()));
                        data1.put("information_data", new JSONArray(array_information.toString()));
                        data1.put("data_post", add_data_to_post());
                        data1.put("url_post", "/mobile/prepaid/tokenpln-transaction");
                        data1.put("url_pay", "/mobile/prepaid/topup-tokenpln");

                        startActivity(new Intent(ElectricalTokenPriceListActivity.this, MainMenuConfirmationActivity.class)
                                .putExtra("type", "electrical_token")
                                .putExtra("data", data1.toString()));
                    }
                    else
                        show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        service_name = findViewById(R.id.service_name);

        data = new JSONObject(getIntent().getStringExtra("data"));

        service_name.setText(data.getString("service_name"));

        prepare_data();
        adapter = new ElectricalTokenPriceListAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void prepare_data() throws JSONException {
        for(int x = 0; x < data.getJSONArray("arr_token").length(); x++)
            array.add(data.getJSONArray("arr_token").getJSONObject(x));
    }

    private void get_information() throws JSONException {
        array_information.clear();
        JSONObject info = data.getJSONObject("info");

        /**
         * @author Jesslyn
         * @note 1. add MSN
         *       2. change noCust subscriberID
         *
         * @author Eliza Sutantya
         * @patch FR19022 - PP12511
         * @notes remove otomax inquiry
         */
        // <code>

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key","Nama Produk");
        jsonObject.put("value",data_selected.getString("nameToken"));
        array_information.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","ID Pelanggan");
        jsonObject.put("value", data.getString("no"));
        array_information.add(jsonObject);

//        jsonObject.put("key","MSN");
//        jsonObject.put("value",info.getString("msn"));
//        array_information.add(jsonObject);

//        jsonObject = new JSONObject();
//        jsonObject.put("key","ID Pelanggan");
//        jsonObject.put("value",info.getString("subscriberID"));
//        array_information.add(jsonObject);
        // </code>

//        jsonObject = new JSONObject();
//        jsonObject.put("key","Nama");
//        jsonObject.put("value",info.getString("nmCust"));
//        array_information.add(jsonObject);

//        jsonObject = new JSONObject();
//        jsonObject.put("key","Tarif / Daya");
//        jsonObject.put("value",info.getString("tarif") + " / " + info.getString("daya") + " VA");
//        array_information.add(jsonObject);

//        jsonObject = new JSONObject();
//        jsonObject.put("key","Tagihan");
//        jsonObject.put("value","Rp. "+formatter.format(data_selected.getDouble("vendorAmount")));
//        array_information.add(jsonObject);
//
//        jsonObject = new JSONObject();
//        jsonObject.put("key","Biaya Admin");
//        jsonObject.put("value","Rp. "+formatter.format(admin_cost));
//        array_information.add(jsonObject);
//
//        jsonObject = new JSONObject();
//        jsonObject.put("key","Total");
//        jsonObject.put("value","Rp. "+formatter.format(data_selected.getDouble("amount")));
//        array_information.add(jsonObject);
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject info = data.getJSONObject("info");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noCust",data.getString("no"));
//        jsonObject.put("noCust",info.getString("noCust"));
//        jsonObject.put("nmCust",info.getString("nmCust"));
//        jsonObject.put("idRef",info.getString("idRef"));
//        jsonObject.put("tarif",info.getString("tarif"));
//        jsonObject.put("daya",info.getString("daya"));
        jsonObject.put("typeTxn","PREPAIDPLN");
        jsonObject.put("idProduct",info.getString("idProduct"));
        jsonObject.put("cdeProduct",info.getString("cdeProduct"));
        jsonObject.put("productType",info.getString("productType"));
        jsonObject.put("nameProduct",info.getString("nameProduct"));
        jsonObject.put("productCategory",info.getString("productCategory"));
        jsonObject.put("image",info.getString("image"));
        jsonObject.put("idProductDetail",data_selected.getString("idProductDetail"));
        jsonObject.put("codeToken",data_selected.getString("codeToken"));
        jsonObject.put("nameToken",data_selected.getString("nameToken"));
        if(!data_selected.isNull("detailToken"))
            jsonObject.put("detailToken",data_selected.getString("detailToken"));
        jsonObject.put("codeToken",data_selected.getString("codeToken"));
        jsonObject.put("vendorAmount",data_selected.getDouble("vendorAmount"));
        jsonObject.put("amount",data_selected.getDouble("amount"));
        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));

        /**
         * @author Jesslyn
         * @note case tdd 12445 - add new data
         *       1. add MSN
         *       2. add $curl_response->data->subscriberID
         *       3. add vendorAdmin
         */
        // <code>
//        jsonObject.put("msn",info.getString("msn"));
//        jsonObject.put("subscriberID",info.getString("subscriberID"));
        jsonObject.put("vendorAdmin",info.getDouble("vendorAdmin"));
        // </code>

        return jsonObject;
    }
}