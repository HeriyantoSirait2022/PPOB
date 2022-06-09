package com.qdi.rajapay.deposit;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.payment.ChoosePaymentActivity;
import com.qdi.rajapay.utils.PriceKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 4.1 Isi Saldo
 * @screen 4.1.1
 */
public class TopUpActivity extends BaseActivity {
    EditText price;
    RecyclerView list;
    PriceKeyboard pk;
    InputConnection ic;

    TopUpAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>(), array_payment_method = new ArrayList<>();

    Double admin_price = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_deposit);

        init_toolbar(getResources().getString(R.string.activity_title_top_up_deposit));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new TopUpAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    get_data_payment(array.get(position).getDouble("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        price = findViewById(R.id.price);
        list = findViewById(R.id.list);

        prepare_data();

        adapter = new TopUpAdapter(array,this);
        layout_manager = new GridLayoutManager(this,3);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        pk = findViewById(R.id.price_keyboard);

        ic = price.onCreateInputConnection(new EditorInfo());
        pk.setInputConnection(ic);

        price.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    /**
                     * @authors : Cherry
                     * @notes : Fixing issue  java.lang.NumberFormatException: empty String.
                     *          Fixing handled value
                     */
                    // <code>
                    if(price.getText().toString().isEmpty()){
                        displaySnackBar(getStr(R.string.deposit_amount_empty));
                        return false;
                    }
                    // </code>

                    Double converted  = Double.parseDouble(price.getText().toString().replace(".",""));
                    /**
                     * @authors : Cherry
                     * @notes : Patching, add minimum amount for topup deposit
                     */

                    // <code>
                    if(converted < 50000D){
                        displaySnackBar(getStr(R.string.deposit_amount_minimum));
                        return false;
                    }
                    // </code>
                    try {
                        get_data_payment(converted);
                        return true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                };

                return handled;
            }
        });

        super.edittext_currency(price);
    }

    private void prepare_data() throws JSONException {
        String[] arr_string = new String[6];
        arr_string[0] = "Rp. 50.000";
        arr_string[1] = "Rp. 100.000";
        arr_string[2] = "Rp. 250.000";
        arr_string[3] = "Rp. 500.000";
        arr_string[4] = "Rp. 750.000";
        arr_string[5] = "Rp. 1.000.000";

        int[] arr_data = new int[6];
        arr_data[0] = 50000;
        arr_data[1] = 100000;
        arr_data[2] = 250000;
        arr_data[3] = 500000;
        arr_data[4] = 750000;
        arr_data[5] = 1000000;

        for(int x=0;x<arr_string.length;x++){
            JSONObject data = new JSONObject();
            data.put("name",arr_string[x]);
            data.put("data",arr_data[x]);
            array.add(data);
        }
    }

    private void get_data_payment(final Double nominal) throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("nmTxn","Top Up Deposit RAJAPAY");
        arr.put("billAmount",nominal);
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));
        /**
         * @author Dinda
         * @note add usedRefCde
         */
        // <code>
        setUserRefCde(arr);
        // </code>

        url = BASE_URL+"/mobile/deposit/data-transaction";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray jsonArray = response_data.getJSONArray("arrPaymentMtdFee");
                        for(int x = 0; x < jsonArray.length(); x++)
                            array_payment_method.add(jsonArray.getJSONObject(x));

                        ArrayList<JSONObject> arr = new ArrayList<>();
                        JSONObject data = new JSONObject();
                        try {
                            arr.add(new JSONObject("{\"title\":\"Top Up Deposit RAJAPAY\",\"price\":"+nominal+"}"));
                            arr.add(new JSONObject("{\"title\":\"Biaya Admin\",\"price\":"+admin_price+"}"));

                            data.put("data",new JSONObject("{\"data\":"+nominal+"}"));
                            data.put("invoice_data",response_data);
                            data.put("data_payment_method",new JSONArray(array_payment_method.toString()));
                            data.put("breakdown_price",new JSONArray(arr.toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        startActivity(new Intent(TopUpActivity.this, ChoosePaymentActivity.class)
                                .putExtra("type","deposit")
                                .putExtra("data",data.toString()));
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
}