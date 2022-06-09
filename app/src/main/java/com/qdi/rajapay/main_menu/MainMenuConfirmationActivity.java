package com.qdi.rajapay.main_menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.payment.ChoosePaymentActivity;
import com.qdi.rajapay.payment.SuccessActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.12 Prabayar Token PLN
 * @screen 4.12.7
 *
 * @module 4.13 Pascabayar Seluler
 * @screen 4.13.7
 *
 * @module 4.14 Listrik
 * @screen 4.14.2
 *
 * @module 4.15 PGN
 * @screen 4.15.2
 *
 * @module 4,16 BPJS
 * @screen 4.16.2
 *
 * @module 4.17 PDAM
 * @screen 4.17.3
 *
 * @module 4.18 TV Berlangganan
 * @screen 4.18.3
 *
 * @module 4.19 Telkom
 * @screen 4.19.3
 *
 * @module 4.20 Multifinance
 * @screen 4.20.3
 */

public class MainMenuConfirmationActivity extends BaseActivity {
    RecyclerView price_list,information_list;
    TextView total_price, total_price_min, total_price_max;
    ImageView image;
    LinearLayout total_min_max_layout, total_layout;
    Button next;

    MainMenuConfirmationPriceAdapter price_adapter;
    MainMenuConfirmationInformationAdapter information_adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> price_array = new ArrayList<>(),information_array = new ArrayList<>();

    JSONObject data, invoice_data, user_data;
    Double total_price_data = 0d, total_price_min_data = 0d, total_price_max_data = 0d, admin_fee = 0d, amount = 0d, adminVendor = 0d;
    boolean is_min_max = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_confirmation);

        init_toolbar(getResources().getString(R.string.activity_title_transaction));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!is_min_max)
                        post_main_menu();
                    else{
                        MainMenuConfirmationEnterAmountModal modal = new MainMenuConfirmationEnterAmountModal();
                        modal.show(getSupportFragmentManager(),"modal");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        price_list = findViewById(R.id.price_list);
        information_list = findViewById(R.id.information_list);
        total_price = findViewById(R.id.total_price);
        next = findViewById(R.id.next);
        image = findViewById(R.id.image);
        total_price_min = findViewById(R.id.total_price_min);
        total_price_max = findViewById(R.id.total_price_max);
        total_min_max_layout = findViewById(R.id.total_min_max_layout);
        total_layout = findViewById(R.id.total_layout);

        prepare_data();
        price_adapter = new MainMenuConfirmationPriceAdapter(price_array,this);
        layout_manager = new LinearLayoutManager(this);
        price_list.setAdapter(price_adapter);
        price_list.setLayoutManager(layout_manager);

        information_adapter = new MainMenuConfirmationInformationAdapter(information_array,this);
        layout_manager = new LinearLayoutManager(this);
        information_list.setAdapter(information_adapter);
        information_list.setLayoutManager(layout_manager);

        user_data = new JSONObject(user_SP.getString("user","{}"));

        if(!is_min_max)
            total_price.setText("Rp. "+formatter.format(total_price_data));
        else{
            total_min_max_layout.setVisibility(View.VISIBLE);
            total_layout.setVisibility(View.GONE);
            total_price_min.setText("Rp. "+formatter.format(total_price_min_data));
            total_price_max.setText("Rp. "+formatter.format(total_price_max_data));
        }

        Picasso.get()
                .load(data.getJSONObject("data_post").getString("image"))
                .into(image);
    }

    private void prepare_data() throws JSONException {
        data = new JSONObject(getIntent().getStringExtra("data"));

        if(getIntent().getStringExtra("type").equals("multifinance") &&
                data.getJSONObject("data").has("minPayment") &&
                data.getJSONObject("data").getDouble("minPayment") != data.getJSONObject("data").getDouble("maxPayment"))
            is_min_max = true;

        for(int x=0;x<data.getJSONArray("breakdown_price").length();x++) {
            JSONObject jsonObject = data.getJSONArray("breakdown_price").getJSONObject(x);
            jsonObject.put("name",jsonObject.getString("title"));
            jsonObject.put( "price_str","Rp. "+formatter.format(jsonObject.getDouble("price")));
            if(jsonObject.has("detail"))
                jsonObject.put("detail",jsonObject.getString("detail"));

            price_array.add(jsonObject);
            if(!is_min_max)
                total_price_data += jsonObject.getDouble("price");
        }

        if(is_min_max) {
            // admin = admin from vendor
            // adminFee = admin from rajapay
            admin_fee = data.getJSONObject("data").getDouble("adminFee");
            adminVendor = data.getJSONObject("data").getDouble("admin");

            total_price_min_data = data.getJSONObject("data").getDouble("minPayment") + admin_fee;
            total_price_max_data = data.getJSONObject("data").getDouble("maxPayment") + admin_fee;
        }

        for(int x=0;x<data.getJSONArray("information_data").length();x++) {
            JSONObject jsonObject1 = data.getJSONArray("information_data").getJSONObject(x);
            if(jsonObject1.has("type") && jsonObject1.getString("type").equals("detail_array")){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key",jsonObject1.getString("key"));

                ArrayList<JSONObject> information_array_temp = new ArrayList<>();

                // loop trough key value
                for(int y = 0; y < jsonObject1.getJSONArray("value").length(); y++){
                    JSONArray jsonArray = jsonObject1.getJSONArray("value").getJSONArray(y);

                    // loop trough value array inside key value
                    for(int z = 0; z < jsonArray.length(); z++) {
                        JSONObject detailTagihan = jsonArray.getJSONObject(z);
                        /**
                         * @author Jesslyn
                         * @note Case Circle CI 922 - Remove admin and total from detail tagihan
                         *       Case Circle CI 1020 - Remove empty space
                         *       Case Circle CI 822 - Filter detail tagihan data
                         */
                        // <code>
                        // check value if contains admin, total, and fee than skip
                        // if length > 1 and contain tagihan then allowed to display

                        /**
                         * @author Jesslyn
                         * @note remove conditional formation for UAT purposes (UAT required detail tagihan, admin, total and fee)
                         */
                        /*
                        if( (!detailTagihan.getString("key").toLowerCase().contains("tagihan") || (jsonObject1.getJSONArray("value").length() > 1)) &&
                                !detailTagihan.getString("key").toLowerCase().contains("admin")  &&
                                !detailTagihan.getString("key").toLowerCase().contains("total") &&
                                !detailTagihan.getString("key").toLowerCase().contains("fee")) {
                         */
                            // skip if value contains only empty string
                            if (!detailTagihan.getString("value").isEmpty()) {
                                // if contains periode and length more than 1 than add periode with counter
                                if(jsonObject1.getJSONArray("value").length() > 1 && detailTagihan.getString("key").toLowerCase().contains("periode")){
                                    detailTagihan.put("key", "Periode " + (y + 1));
                                }
                                information_array_temp.add(detailTagihan);
                            }
                        /*
                        }
                        */
                        // </code>
                    }

                    /**
                     * @author Jesslyn
                     * @note add new line every 1 period (but not at the last array)
                     */
                    // <code>
                    if(y < jsonObject1.getJSONArray("value").length() - 1)
                        information_array_temp.add(new JSONObject());
                    // </code>
                }

                /**
                 * @author Jesslyn
                 * @note Circle CI 1022 - add space before detail tagihan
                 *       Circle CI 1922 - Check if value exist then add detail tagihan and space
                 */
                // <code>
                if(information_array_temp.size() > 0){
                    information_array.add(new JSONObject());
                    information_array.add(jsonObject);

                    information_array.addAll(information_array_temp);
                }
                // </code>
            }
            else{
                /**
                 * @author Jesslyn
                 * @note Case Circle CI 844 : Remove total tagihan from detail
                 */
                // <code>
                /**
                 * @author Jesslyn
                 * @note remove conditional formation for UAT purposes (UAT required detail tagihan, admin, total and fee)
                 */
                // if(!jsonObject1.getString("key").toLowerCase().contains("total tagihan") && !jsonObject1.getString("value").isEmpty())
                information_array.add(jsonObject1);
                // </code>
            }
        }
    }

    public void post_main_menu() throws JSONException {
        if(data.has("data_post") && data.has("url_post")) {
            // for min-max only, to calculate dif price with min_price (result will be added to price at price array in breakdown price
            double priceDiffTemp = 0d;

            if(is_min_max){
                /**
                 * @author Jesslyn
                 * @note deduct totalBayar with admihFee because before call the modal, amount added with admin_fee
                 */
                // <code>
                priceDiffTemp = amount - total_price_min_data;
                data.getJSONObject("data_post").put("totalBayar",amount - admin_fee);
                // </code>
            }

            JSONObject arr = data.getJSONObject("data_post");
            /**
             * @author Dinda
             * @note Let base activity manage usedRefCode parameter
             * <code>
             *     arr.put("usedRefCde",user_data.getString("usedRefCde"));
             * </code>
             */
            // <code>
            setUserRefCde(arr);
            // </code>

            url = BASE_URL + data.getString("url_post");
            Log.d("url",url);
            Log.d("arr",arr.toString());
            show_wait_modal();

            final double priceDiff = priceDiffTemp;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            JSONObject data1 = new JSONObject();
                            try {
                                ArrayList<JSONObject> arrayList = price_array;
                                /**
                                 * @author Jesslyn
                                 * @note fixing issue breakdown_price problem when using min_max scenario
                                 */
                                // <code>
                                 if(is_min_max){
                                     // assume always index 0
                                     // add price with price diff
                                     arrayList.get(0).put("price", arrayList.get(0).getDouble("price") + priceDiff);
                                 }
                                 // </code>

                                invoice_data = response_data;

                                data.put("breakdown_price",new JSONArray(arrayList.toString()));
                                /**
                                 * @auhtor Jesslyn
                                 * @note add image url to display PLN image
                                 */
                                // <code>
                                data.put("image_url", data.getJSONObject("data_post").getString("image"));
                                // </code>
                                data1.put("data",data);
                                data1.put("invoice_data",response_data);
                                data1.put("data_post",data.getJSONObject("data_post"));
                                data1.put("url_post",data.getString("url_post"));
                                data1.put("url_pay",data.getString("url_pay"));
                                data1.put("breakdown_price",new JSONArray(arrayList.toString()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            startActivity(new Intent(MainMenuConfirmationActivity.this, ChoosePaymentActivity.class)
                                    .putExtra("type",getIntent().getStringExtra("type"))
                                    .putExtra("data",data1.toString()));
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
            startActivity(new Intent(MainMenuConfirmationActivity.this, SuccessActivity.class)
                    .putExtra("data", getIntent().getStringExtra("data")));
    }
}