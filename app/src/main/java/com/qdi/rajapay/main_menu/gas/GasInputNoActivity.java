package com.qdi.rajapay.main_menu.gas;

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
import com.qdi.rajapay.main_menu.electrical.ElectricalPriceListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.15 PGN (Gas)
 * @screen 4.15.1
 */
public class GasInputNoActivity extends BaseActivity {
    ImageView image;
    EditText no;
    Button next;

    JSONObject data, fee_data;
    Double admin_cost = 3000d;
    ArrayList<JSONObject> array = new ArrayList<>();

    /**
     * @author Jesslyn
     * @note add flag for active and problem product
     */
    // <code>
    boolean isProblem = false, isActive = true;
    // </code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_gas_input_no);

        init_toolbar(getResources().getString(R.string.home_gas));
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

    private void init() throws JSONException {
        image = findViewById(R.id.image);
        no = findViewById(R.id.no);

        // @CFI
        next = findViewById(R.id.next);

        /**
         * @author : Jesslyn
         * @note : 1. Set programming flow always get_fee_data()
         *         2. Adding preparePriceTag function
         *         3. Add event handler for click price tag
         */
        // <code>
        get_fee_data();
        preparePriceTag(this, ElectricalPriceListActivity.class);
        add_ons_view_array.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @author Dinda
                 * @note case CICD 9453 - tag NPE
                 */
                // <code-9453>
                if (isActiveNotProblem(isActive, isProblem) && fee_data != null)
                    startActivity(new Intent(GasInputNoActivity.this, GasPriceListActivity.class)
                            .putExtra("fee_data", fee_data.toString()));
                // </code-9453>
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

    /**
     * @author Jesslyn
     * @note set function onNext
     */
    // <code>
    private void onNext(){
        if(no.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.main_menu_gas_empty_no));
        else if(isActiveNotProblem(isActive, isProblem)){
            try {
                get_data();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // </code>

    /**
     * @author Jessln
     * @note set mode enabled for activity
     * @param enabled
     */
    // <code>
    private void setEnabled(boolean enabled){
        no.setFocusable(enabled);
        no.setEnabled(enabled);
        next.setEnabled(enabled);
        pk.setEnabled(enabled);
        if(enabled)
            pk.setVisibility(View.VISIBLE);
        else
            pk.setVisibility(View.GONE);
    }
    // </code>

    /**
     * @author : Jesslyn
     * @note : add wait modal
     * @throws JSONException
     */
    // <code>
    private void get_fee_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/postpaid/pgn-feeinfo";

        JSONObject param = getBaseAuth();
        param.put("productType", "PGN");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        fee_data = response_data;
                        fee_data.put("text","Tagihan PGN");
                        fee_data.put("price",response_data.getDouble("adminFee"));
                        fee_data.put("price_str","Rp. "+formatter.format(response_data.getDouble("adminFee")));
                        /**
                         * @author : Jesslyn
                         * @note : 1. if isActive == 1 then check isProblem, otherwise make ui disable
                         *         2. get isProblem flag and detail, maintain (disable) ui if isProblem == 1
                         */
                        // <code>
                        isActive = true;
                        if(fee_data.getInt("isProblem") == 0) {
                            isProblem = false;
                            fee_data.put("detail", !fee_data.isNull("detailToken") ? fee_data.getString("detailToken") : "-");
                            setEnabled(true);
                        }
                        else if(fee_data.getInt("isProblem") == 1){
                            isProblem = true;
                            fee_data.put("detail", getResources().getString(R.string.trouble_label));
                            isActiveNotProblem(isActive, isProblem);
                            setEnabled(false);
                        }
                        fee_data.put("is_important", fee_data.getInt("isProblem") == 1);
                        // </code>
                    } else {
                        isActive = false;
                        setEnabled(false);
                        // no need to call isActiveNotProblem cause response from server is unsure
                        show_error_message(layout,response_data.getString("message"));
                        // </code>
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    /**
                     * @author Jesslyn
                     * @note check respond, if respond is Data Not Found, then for this activity assume is active = 0. Server not returned any data if isActive = 0
                     */
                    // <code>
                    isActive = false;
                    setEnabled(false);

                    String response_data = new String(error.networkResponse.data, "UTF-8");
                    JSONObject data = new JSONObject(response_data);
                    if (data.has("response")) {
                        JSONObject data_response = data.getJSONObject("response");
                        if (data_response.has("message")) {
                            String message = data_response.getString("message");

                            if(message.equalsIgnoreCase(getStr(R.string.data_not_found))){
                                dismiss_wait_modal();
                                // assume is inactive problem (because if isActive = 0, WS not returned any data
                                isActiveNotProblem(isActive, isProblem);
                            }else{
                                error_handling(error, layout);
                            }
                        }
                    }
                    // </code>
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
    // </code>

    private void get_information() throws JSONException {
        array.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key","ID PEL");
        jsonObject.put("value",data.getString("noPgn"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Nama");
        jsonObject.put("value",data.getString("nmCust"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Alamat");
        jsonObject.put("value",data.getString("alamat"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Periode");
        jsonObject.put("value",data.getString("periode"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Stand Awal");
        jsonObject.put("value",data.getString("standAwal"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Stand Akhir");
        jsonObject.put("value",data.getString("standAkhir"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Pemakaian");
        jsonObject.put("value",data.getString("pemakaian"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Tagihan");
        jsonObject.put("value","Rp. "+formatter.format(data.getDouble("tagihan")));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Biaya Admin");
        jsonObject.put("value","Rp. "+formatter.format(admin_cost));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("key","Total Tagihan");
        jsonObject.put("value","Rp. "+formatter.format(data.getDouble("tagihan") + admin_cost));
        array.add(jsonObject);
    }

    private void get_data() throws JSONException {
        url = BASE_URL+"/mobile/postpaid/pgn-info";
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("productType", "PGN");
        param.put("noPgn", no.getText().toString() );
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
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.home_gas_bill)+
                "\",\"price\":"+data.getDouble("price")+",\"detail\":\"" + no.getText().toString() + "\"}"));
        // </code>
        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+
                "\",\"price\":"+admin_cost+"}"));

        data1.put("data",data);
        data1.put("breakdown_price",new JSONArray(arr.toString()));
        data1.put("information_data",new JSONArray(array.toString()));
        data1.put("data_post",add_data_to_post());
        data1.put("url_post","/mobile/postpaid/pgn-transaction");
        data1.put("url_pay","/mobile/postpaid/pgn-payment");

        startActivity(new Intent(GasInputNoActivity.this, MainMenuConfirmationActivity.class)
                .putExtra("type","gas")
                .putExtra("data",data1.toString()));
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noPgn",data.getString("noPgn"));
        jsonObject.put("idProduct",data.getString("idProduct"));
        jsonObject.put("cdeProduct",data.getString("cdeProduct"));
        jsonObject.put("productType",data.getString("productType"));
        jsonObject.put("nameProduct",data.getString("nameProduct"));
        jsonObject.put("productCategory",data.getString("productCategory"));
        jsonObject.put("image",data.getString("image"));
        jsonObject.put("adminFee",data.getDouble("adminFee"));
        jsonObject.put("typeTxn","POSTPAIDPGN");
        jsonObject.put("nmCust",data.getString("nmCust"));
        jsonObject.put("alamat",data.getString("alamat"));
        jsonObject.put("periode",data.getString("periode"));
        jsonObject.put("standAwal",data.getString("standAwal"));
        jsonObject.put("standAkhir",data.getString("standAkhir"));
        jsonObject.put("pemakaian",data.getString("pemakaian"));
        jsonObject.put("ref",data.getString("ref"));
        jsonObject.put("tagihan",data.getDouble("tagihan"));
        jsonObject.put("admin",data.getDouble("admin"));
        jsonObject.put("totalTagihan",data.getDouble("totalTagihan"));
        jsonObject.put("idRef",data.getString("idRef"));
        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));

        return jsonObject;
    }
}