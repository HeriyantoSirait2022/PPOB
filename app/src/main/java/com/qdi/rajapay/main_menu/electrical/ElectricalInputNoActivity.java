package com.qdi.rajapay.main_menu.electrical;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @module 4.14 Listrik
 * @screen 4.14.1
 */

public class ElectricalInputNoActivity extends BaseActivity {
    ImageView image;
    EditText no;
    Button next;

    JSONObject data, fee_data;
    Double admin_cost = 0d;
    Double denda_cost = 0d;
    Double price = 0d;
    ArrayList<JSONObject> array = new ArrayList<>();
    List<JSONObject> arr_detail_child = new ArrayList<>();

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
        setContentView(R.layout.activity_main_menu_electrical_input_no);

        init_toolbar(getResources().getString(R.string.home_electrical));
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
    }

    /**
     * @author Jesslyn
     * @note set function onNext
     */
    // <code>
    private void onNext() {
        if (no.getText().toString().equals(""))
            show_error_message(layout, getResources().getString(R.string.main_menu_water_empty_no));
        else if (isActiveNotProblem(isActive, isProblem)) {
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
        next = findViewById(R.id.next);

        /**
         * @author : Jesslyn
         * @note : 1. remove get extras and set flow program always call get_fee_data() (4.14.1 had been set as main class for 4.14. prev : 4.14.6
         *         2. Add price tag icon at the top-left activity
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
                    startActivity(new Intent(ElectricalInputNoActivity.this, ElectricalPriceListActivity.class)
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
     * @param enabled
     * @author Jessln
     * @note set mode enabled for activity
     */
    // <code>
    private void setEnabled(boolean enabled) {
        no.setFocusable(enabled);
        no.setEnabled(enabled);
        next.setEnabled(enabled);
        pk.setEnabled(enabled);
        if (enabled)
            pk.setVisibility(View.VISIBLE);
        else
            pk.setVisibility(View.GONE);
    }
    // </code>

    /**
     * @author : Jesslyn
     * @note : add wait modal
     */
    // <code>
    private void get_fee_data() {
        show_wait_modal();
        url = BASE_URL + "/mobile/postpaid/pln-feeinfo";

        JSONObject param = getBaseAuth();
        try {
            param.put("productType", "PLN");
        }catch(Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        fee_data = response_data;
                        fee_data.put("text", "Tagihan PLN");
                        fee_data.put("price", response_data.getDouble("adminFee"));
                        fee_data.put("price_str", "Rp. " + formatter.format(response_data.getDouble("adminFee")));
                        /**
                         * @author : Jesslyn
                         * @note : 1. if isActive == 1 then check isProblem, otherwise make ui disable
                         *         2. get isProblem flag and detail, maintain (disable) ui if isProblem == 1
                         */
                        // <code>
                        isActive = true;
                        if (fee_data.getInt("isProblem") == 0) {
                            isProblem = false;
                            fee_data.put("detail", !fee_data.isNull("detailToken") ? fee_data.getString("detailToken") : "-");
                            setEnabled(true);
                        } else if (fee_data.getInt("isProblem") == 1) {
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
                        show_error_message(layout, response_data.getString("message"));
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

                            if (message.equalsIgnoreCase(getStr(R.string.data_not_found))) {
                                dismiss_wait_modal();
                                // assume is inactive problem (because if isActive = 0, WS not returned any data
                                isActiveNotProblem(isActive, isProblem);
                            } else {
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

    private void add_key_value_information(String title, String value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", title);
        jsonObject.put("value", value);
        array.add(jsonObject);
    }

    private void add_key_value_detail_information(String title, String value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", title);
        jsonObject.put("value", value);
        arr_detail_child.add(jsonObject);
    }

    private void get_information() throws JSONException {
        array.clear();
        add_key_value_information("IDPEL", data.getString("noPln"));
        add_key_value_information("Nama", data.getString("nmCust"));
        add_key_value_information("Tarif/Daya", data.getString("tarif") + " / " + data.getString("daya") +" VA");

        String additionalInfo = "";
        if(data.getInt("lembarTagihanTotal") > data.getInt("lembarTagihan"))
            additionalInfo = "(Maks. 4 per transaksi)";
        add_key_value_information("Lembar Tagihan", data.getString("lembarTagihan") + " / " + data.getString("lembarTagihanTotal") + " " + additionalInfo);
        // use lembarTagihan for actual tagihan which want to pay
        // add_key_value_information("Lembar Tagih", data.getString("lembarTagihanTotal"));

        add_key_value_information("RP TAG PLN", "Rp. " + formatter.format(data.getDouble("price")));
        add_key_value_information("Denda", "Rp. " + formatter.format(denda_cost));
        add_key_value_information("Admin", "Rp. " + formatter.format(admin_cost));
        add_key_value_information("Total Bayar", "Rp. " + formatter.format(data.getDouble("price") + admin_cost + denda_cost));

        List<JSONArray> arr_detail = new ArrayList<>();
        for (int x = 0; x < data.getJSONArray("detilTagihan").length(); x++) {
            arr_detail_child.clear();
            JSONObject detail = data.getJSONArray("detilTagihan").getJSONObject(x);
            add_key_value_detail_information("Periode", detail.getString("periode"));
            add_key_value_detail_information("- Tagihan", "Rp. " + formatter.format(detail.getDouble("nilaiTagihan")));
            add_key_value_detail_information("- Denda", "Rp. " + formatter.format(detail.getDouble("denda")));
            add_key_value_detail_information("- Admin", "Rp. " + formatter.format(detail.getDouble("admin")));
            add_key_value_detail_information("- Total", "Rp. " + formatter.format(detail.getDouble("total")));

            arr_detail.add(new JSONArray(arr_detail_child.toString()));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "Detail Tagihan");
        jsonObject.put("type", "detail_array");
        jsonObject.put("value", new JSONArray(arr_detail.toString()));
        array.add(jsonObject);
    }

    private void get_data() throws JSONException {
        url = BASE_URL + "/mobile/postpaid/pln-info";
        show_wait_modal();

        JSONObject param = getBaseAuth();
        param.put("productType", "PLN");
        param.put("noPln", no.getText().toString() );
        param.put("idProduct", fee_data.getString("idProduct") );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss_wait_modal();
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data = response_data;
                        data.put("price", 0);
                        price = 0d;
                        admin_cost = 0d;

                        for (int x = 0; x < data.getJSONArray("detilTagihan").length(); x++) {
                            /**
                             * @author Jesslyn
                             * @note BDD 10101 - 1. admin fee added from WS
                             *                   2. add denda cost for info only
                             */
                            // <code>
                            denda_cost += data.getJSONArray("detilTagihan").getJSONObject(x).getDouble("denda");
                            admin_cost += data.getJSONArray("detilTagihan").getJSONObject(x).getDouble("admin");
                            price += data.getJSONArray("detilTagihan").getJSONObject(x).getDouble("nilaiTagihan");
                            // </code>
                        }
                        data.put("price", price);

                        redirect_page();
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

    private void redirect_page() throws JSONException {
        get_information();

        ArrayList<JSONObject> arr = new ArrayList<>();
        JSONObject data1 = new JSONObject();

        Double denda = 0d;
        for (int x = 0; x < data.getJSONArray("detilTagihan").length(); x++) {
            denda += data.getJSONArray("detilTagihan").getJSONObject(x).getDouble("denda");
        }

        /**
         * @author Jesslyn
         * @note add detail
         */
        // <code>
        arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.home_electrical_bill) +
                "\",\"price\":" + data.getDouble("price") +
                ",\"detail\":\"" + no.getText().toString() + "\"}"));
        // </code>
        arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.admin_cost_label) +
                "\",\"price\":" + admin_cost + "}"));
        arr.add(new JSONObject("{\"title\":\"" + getResources().getString(R.string.fine_label) +
                "\",\"price\":" + denda + "}"));

        data1.put("data", data);
        data1.put("breakdown_price", new JSONArray(arr.toString()));
        data1.put("information_data", new JSONArray(array.toString()));
        data1.put("data_post", add_data_to_post());
        data1.put("url_post", "/mobile/postpaid/pln-transaction");
        data1.put("url_pay", "/mobile/postpaid/pln-payment");

        startActivity(new Intent(ElectricalInputNoActivity.this, MainMenuConfirmationActivity.class)
                .putExtra("type", "electrical")
                .putExtra("data", data1.toString()));
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noPln", data.getString("noPln"));
        jsonObject.put("idProduct", data.getString("idProduct"));
        jsonObject.put("cdeProduct", data.getString("cdeProduct"));
        jsonObject.put("productType", data.getString("productType"));
        jsonObject.put("nameProduct", data.getString("nameProduct"));
        jsonObject.put("productCategory", data.getString("productCategory"));
        jsonObject.put("image", data.getString("image"));
        jsonObject.put("adminFee", data.getDouble("adminFee"));
        jsonObject.put("typeTxn", "POSTPAIDPLN");
        jsonObject.put("nmCust", data.getString("nmCust"));
        jsonObject.put("tarif", data.getString("tarif"));
        jsonObject.put("daya", data.getString("daya"));
        jsonObject.put("lembarTagihanTotal", data.getString("lembarTagihanTotal"));
        jsonObject.put("lembarTagihan", data.getString("lembarTagihan"));
        jsonObject.put("detilTagihan", data.getJSONArray("detilTagihan"));
        jsonObject.put("totBillAmountVendor", data.getDouble("totBillAmountVendor"));
        jsonObject.put("idRef", data.getString("idRef"));
        jsonObject.put("idLogin", user_SP.getString("idLogin", ""));
        jsonObject.put("idUser", user_SP.getString("idUser", ""));
        jsonObject.put("token", user_SP.getString("token", ""));

        return jsonObject;
    }
}