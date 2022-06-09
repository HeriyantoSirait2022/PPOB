package com.qdi.rajapay.main_menu.emoney;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.enums.ProductType;
import com.qdi.rajapay.payment.ChoosePaymentActivity;
import com.qdi.rajapay.payment.SuccessActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @module 4.23 Uang Elektronik
 * @screen 4.23.3
 */
public class EmoneyDenominationActivity extends BaseActivity {
    RecyclerView list;

    EmoneyDenominationAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    JSONObject selectedProduct, selectedDenomination;

    TextView productPhoneNo, productLegend;
    ImageView productImage;
    View hasDataLayout, noDataLayout;
    String noHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_emoney_denomination);

        init_toolbar(getResources().getString(R.string.main_menu_emoney_denomination_title));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // @CFI
        adapter.setOnItemClickListener(new EmoneyDenominationAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                selectedDenomination = array.get(position);
                try{
                    if(selectedDenomination.has("isProblem")){
                        if(selectedDenomination.getInt("isProblem") == 1){
                            isActiveNotProblem(false, true);
                        }else{
                            processToTransaction();
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        if(getIntent().hasExtra("selected_product")){
            selectedProduct = new JSONObject(getIntent().getStringExtra("selected_product"));
        }

        productLegend = findViewById(R.id.product_legend);
        if(getIntent().hasExtra("product_name")){
            productLegend.setText(getIntent().getStringExtra("product_name"));
        }

        productPhoneNo = findViewById(R.id.product_phone_no);
        if(getIntent().hasExtra("product_phone_no")){
            noHp = getIntent().getStringExtra("product_phone_no");
            productPhoneNo.setText(noHp);
        }

        productImage = findViewById(R.id.product_image);
        if(selectedProduct.has("image") && !isNullOrEmpty(selectedProduct.getString("image"))){
            Picasso.get()
                    .load(selectedProduct.getString("image"))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit().centerInside()
                    .into(productImage);
        }else{
            productImage.setImageDrawable(context.getDrawable(R.drawable.ic_default_4));
        }

        hasDataLayout = findViewById(R.id.has_data_layout);
        noDataLayout = findViewById(R.id.empty_data_layout);

        adapter = new EmoneyDenominationAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        get_data();
    }

    private void get_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/prepaid/emoney-list";

        JSONObject param = getBaseAuth();
        param.put("idProduct", selectedProduct.getString("productId") );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        array.clear();
                        JSONObject resp = response_data.getJSONObject("emoneyPriceList");

                        Iterator<String> keys = resp.keys();
                        while(keys.hasNext()) {
                            ArrayList<JSONObject> temp = new ArrayList<>();
                            String key = keys.next();

                            if (resp.getJSONArray(key) instanceof JSONArray) {
                                JSONArray arr = resp.getJSONArray(key);

                                for(int j = 0; j < arr.length(); j++){
                                    JSONObject jsonObject = arr.getJSONObject(j);

                                    jsonObject.put("text",jsonObject.getString("nameEmoney"));
                                    jsonObject.put("is_trouble", jsonObject.getInt("isProblem") != 0);

                                    array.add(jsonObject);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }

                    onUpdate(array.isEmpty());
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    dismiss_wait_modal();
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

                onUpdate(array.isEmpty());
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

    public void onUpdate(boolean isEmpty) {
        if(isEmpty) {
            noDataLayout.setVisibility(View.VISIBLE);
            hasDataLayout.setVisibility(View.GONE);
        }else{
            noDataLayout.setVisibility(View.GONE);
            hasDataLayout.setVisibility(View.VISIBLE);
        }
    }

    public void processToTransaction(){
        ArrayList<JSONObject> arr = new ArrayList<>();
        JSONObject data = new JSONObject();
        try {
            arr.add(new JSONObject("{\"title\":\""+ selectedDenomination.getString("nameEmoney") +"\",\"price\":"+ selectedDenomination.getDouble("price")+",\"detail\":\"" + noHp + "\"}"));
            arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+"\",\"price\":"+ selectedDenomination.getDouble("adminFee") +"}"));

            data.put("data", selectedProduct);
            data.put("data_post",add_data_to_post());
            data.put("url_post","/mobile/prepaid/emoney-transaction");
            data.put("url_pay","/mobile/prepaid/topup-emoney");
            data.put("breakdown_price",new JSONArray(arr.toString()));

            post_main_menu(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("noHp", noHp);
        jsonObject.put("typeTxn", Objects.requireNonNull(ProductType.fromStrToProductType(selectedDenomination.getString("productType"))).toTxnType() );
        jsonObject.put("idProduct", selectedDenomination.getString("idProduct"));
        jsonObject.put("productType", selectedDenomination.getString("productType"));
        jsonObject.put("idProductDetail", selectedDenomination.getString("idProductDetail"));
        jsonObject.put("codeEmoney", selectedDenomination.getString("codeEmoney"));
        jsonObject.put("nameEmoney", selectedDenomination.getString("nameEmoney"));
        jsonObject.put("idLogin", user_SP.getString("idLogin",""));
        jsonObject.put("idUser", user_SP.getString("idUser",""));
        jsonObject.put("token", user_SP.getString("token",""));

        setUserRefCde(jsonObject);

        return jsonObject;
    }

    private void post_main_menu(final JSONObject data) throws JSONException {
        if(data.has("data_post") && data.has("url_post")) {
            JSONObject arr = data.getJSONObject("data_post");

            url = BASE_URL + data.getString("url_post");
            show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            data.put("invoice_data",response_data);

                            startActivity(new Intent(EmoneyDenominationActivity.this, ChoosePaymentActivity.class)
                                    .putExtra("type","mobile_data")
                                    .putExtra("data",data.toString()));
                        } else {
                            show_error_message(layout, response_data.getString("message"));
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
        else
            startActivity(new Intent(this, SuccessActivity.class)
                    .putExtra("data", data.toString()));
    }
}