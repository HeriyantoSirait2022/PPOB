package com.qdi.rajapay.main_menu.bank_transfer;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.deposit.TopUpAdapter;
import com.qdi.rajapay.main_menu.MainMenuConfirmationActivity;
import com.qdi.rajapay.main_menu.phone.PhoneInputNoActivity;
import com.qdi.rajapay.payment.ChoosePaymentActivity;
import com.qdi.rajapay.utils.PriceKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 4.24 Transfer Bank
 * @screen 4.24.3
 */
public class BankTransferDenominationActivity extends BaseActivity {
    EditText price;
    RecyclerView list;
    PriceKeyboard pk;
    InputConnection ic;
    TextView amountTitle;

    TopUpAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>(), arrayNominal = new ArrayList<>();

    LinearLayout keyboard;

    JSONObject selectedProduct, data;
    JSONArray bankTransferInfo;
    String productName, productAccountNo, accountNo, accountName;
    Double adminCost = 0d, totalTransfer = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_deposit);

        init_toolbar(getResources().getString(R.string.home_transfer_bank));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new TopUpAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    get_data(arrayNominal.get(position).getDouble("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        if(getIntent().hasExtra("selected_product")){
            selectedProduct = new JSONObject(getIntent().getStringExtra("selected_product"));
        }

        if(getIntent().hasExtra("product_account_no")){
            productAccountNo = getIntent().getStringExtra("product_account_no");
        }

        if(getIntent().hasExtra("product_name")){
            productName = getIntent().getStringExtra("product_name");
        }

        price = findViewById(R.id.price);
        list = findViewById(R.id.list);

        amountTitle = findViewById(R.id.title_amount);
        amountTitle.setText(getStr(R.string.main_menu_banktransfer_start_transfer));

        prepare_data();

        adapter = new TopUpAdapter(arrayNominal,this);
        layout_manager = new GridLayoutManager(this,3);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        keyboard = findViewById(R.id.keyboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        pk = findViewById(R.id.price_keyboard);

        ic = price.onCreateInputConnection(new EditorInfo());
        pk.setInputConnection(ic);

        price.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if(price.getText().toString().isEmpty()){
                        displaySnackBar(getStr(R.string.main_menu_banktransfer_error_empty_denomination));
                        return false;
                    }

                    Double converted  = Double.parseDouble(price.getText().toString().replace(".",""));
                    if(converted < 50000D){
                        displaySnackBar(getStr(R.string.deposit_amount_minimum));
                        return false;
                    }

                    try {
                        get_data(converted);
                        return true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });

        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
                        android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if(v.equals(price) && keyboard.getVisibility() == View.GONE) {
                    price.requestFocus();
                    keyboard.setVisibility(View.VISIBLE);
                }
            }
        });

        super.edittext_currency(price);
    }

    @Override
    public void onBackPressed(){
        if(keyboard.getVisibility() == View.GONE)
            super.onBackPressed();
        else
            keyboard.setVisibility(View.GONE);
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
            arrayNominal.add(data);
        }
    }

    private void get_data(final Double nominal) throws JSONException {
        url = BASE_URL+"/mobile/prepaid/banktransfer-info";
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("productType", "BANKTRANSFER");
        param.put("bankAccountNo", productAccountNo );
        param.put("idProduct", selectedProduct.getString("productId") );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss_wait_modal();
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data = response_data;

                        bankTransferInfo = data.getJSONArray("arrBankTransferPriceList");
                        adminCost = bankTransferInfo.getJSONObject(0).getDouble("adminFee");
                        accountName = bankTransferInfo.getJSONObject(0).getString("accountName");
                        accountNo = bankTransferInfo.getJSONObject(0).getString("accountNo");
                        totalTransfer = nominal;

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

    private void get_information() throws JSONException {
        array.clear();

        add_key_value_information("Pemilik Rekening", accountName);
        add_key_value_information("Nomor Rekening", accountNo);
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
        arr.add(new JSONObject("{\"title\":\""+ productName+
                "\",\"price\":"+totalTransfer+",\"detail\":\"" + productAccountNo + "\"}"));
        // </code>
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+
                "\",\"price\":"+adminCost+"}"));

        data1.put("data",data);
        data1.put("breakdown_price",new JSONArray(arr.toString()));
        data1.put("information_data",new JSONArray(array.toString()));
        data1.put("data_post",add_data_to_post());
        data1.put("url_post","/mobile/prepaid/banktransfer-transaction");
        data1.put("url_pay","/mobile/prepaid/topup-banktransfer");

        startActivity(new Intent(BankTransferDenominationActivity.this, MainMenuConfirmationActivity.class)
                .putExtra("type","BANKTRANSFER")
                .putExtra("data",data1.toString()));
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));
        jsonObject.put("bankCode",data.getString("cdeProduct"));
        jsonObject.put("bankAccountNo", accountNo);
        jsonObject.put("amount", totalTransfer);
        jsonObject.put("idProduct",data.getString("idProduct"));
        jsonObject.put("productType",data.getString("productType"));
        jsonObject.put("productCategory",data.getString("productCategory"));
        jsonObject.put("image",data.getString("image"));

        return jsonObject;
    }

    private void add_key_value_information(String title, String value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",title);
        jsonObject.put("value",value);
        array.add(jsonObject);
    }
}