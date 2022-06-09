package com.qdi.rajapay.agency.upgrade_premium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 7.0 Keagenan
 * @screen 7.1.2
 */
public class UpgradePremiumTermConditionActivity extends BaseActivity {
    WebView syarat_ketentuan;
    Button agree;
    int upgradePrice = 0;
    JSONObject data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_upgrade_premium_term_condition);

        init_toolbar(getResources().getString(R.string.term_condition_label));

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        syarat_ketentuan = (WebView) findViewById(R.id.syarat_ketentuan);
        agree = findViewById(R.id.agree);


        Intent intent = getIntent();
        if(intent.hasExtra("data")){
            data = new JSONObject(getIntent().getStringExtra("data"));

            if(data.has("amount")){
                upgradePrice = data.getInt("amount");
                String text = getStr(R.string.agency_upgrade_premium_detail_agree_to_pay) + " " + formatter.format(upgradePrice);
                agree.setText(text);
            }
        }

        String text = " <ul>" +
                "            <li>" +
                "                Biaya Upgrade menjadi Agen Premium adalah Rp." + formatter.format(upgradePrice) + " dan langsung dibayarkan melalui deposit RAJAPAY.\n" +
                "            </li>" +
                "<br/>" +
                "            <li>" +
                "                Biaya Upgrade Agen Premium yang sudah dibayarkan tidak dapat dikembalikan.\n" +
                "            </li>" +
                "<br/>" +
                "            <li>" +
                "                Jika Agen Premium tidak menginginkan hadiah, dapat memilih deposit RAJAPAY yang nominalnya sudah ditentukan oleh Tim Manajemen RAJAPAY.\n" +
                "            </li>" +
                "<br/>" +
                "            <li>" +
                "                Hadiah deposit RAJAPAY yang berdampak pada nominal deposit Agen Premium yang lebih dari Rp.10.000.000 akan diberikan dengan periode/waktu tertentu oleh Tim Manajemen RAJAPAY. Sehingga deposit RAJAPAY Agen Premium tidak melebihi limit dalam satu waktu.\n" +
                "            </li>" +
                "        </ul>";
        syarat_ketentuan.loadDataWithBaseURL(null, text, "text/html", "utf-8", null);
    }

    private void submit() throws JSONException {
        JSONObject user_data = new JSONObject(user_SP.getString("user",""));

        JSONObject arr = new JSONObject();
        arr.put("acctType",data.getString("acctType"));
        arr.put("amount",data.getDouble("amount"));
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        /**
         * @author Dinda
         * @note Case CICD 9684 - Missing cdeReferral code
         */
        // <code-9684>
        /**
         * @author Dinda
         * @note Let base activity manage usedRefCode parameter
         * <code>
         *     arr.put("cdeReferral", user_data.getString("usedRefCde"));
         * </code>
         */
        // <code>
        setUserRefCde(arr);
        // </code>
        // </code-9684>
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/agen/upgrade-premium";
        Log.d("url", url);
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        Intent intent = new Intent(UpgradePremiumTermConditionActivity.this, MainActivity.class);
                        response_data.put("dMsg", getStr(R.string.s_premium_upgrade));
                        intent.putExtra("data", response_data.toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
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