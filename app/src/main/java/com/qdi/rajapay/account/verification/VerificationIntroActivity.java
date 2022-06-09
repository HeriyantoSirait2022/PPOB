package com.qdi.rajapay.account.verification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 8.7 Verifikasi Akun
 * @screen 8.7.1
 */
public class VerificationIntroActivity extends BaseActivity {
    RecyclerView list;
    Button submit;

    VerificationIntroAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ArrayList<Class> array_class = new ArrayList<>();

    JSONObject verification_data = new JSONObject();
    Boolean clickable = true;
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification_intro);

        init_toolbar(getResources().getString(R.string.activity_title_basic_verify));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new VerificationIntroAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                if(clickable)
                    startActivity(new Intent(VerificationIntroActivity.this,array_class.get(position)));
                else
                    displaySnackBar(message);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submit();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            prepare_data();
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        submit = findViewById(R.id.submit);

        prepare_data();
        adapter = new VerificationIntroAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        get_status();
    }

    private void prepare_data() throws JSONException {
        verification_data = new JSONObject(user_SP.getString("verification_data","{}"));

        array.clear();
        array_class.clear();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.account_verification_intro_id_card));
        jsonObject.put("is_complete",verification_data.has("id_card_uri") &&
                verification_data.has("id_card") && verification_data.has("selfie_uri")) ;
        array.add(jsonObject);
        array_class.add(VerificationEnterIdNoActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.account_verification_intro_biometric));
        jsonObject.put("is_complete", verification_data.has("signature_uri"));
        array.add(jsonObject);
        array_class.add(VerificationBiometricIntroActivity.class);
    }

    private void get_status() {
        url = BASE_URL+"/mobile/account/show-verification-status";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");

                    // Status W dan S
                    if(response_data.getString("description").equals("VERFY_PROCESSED") || response_data.getString("description").equals("VERFY_SUCCESS")){
                        submit.setVisibility(View.GONE);
                        clickable = false;
                        message = response_data.getString("message");
                    }
                    else{
                        submit.setVisibility(View.VISIBLE);
                        clickable = true;
                        message = "";
                    }

                    show_error_message(layout,response_data.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismiss_wait_modal();
                try {
                    if(error.networkResponse != null){
                        String response_data = new String(error.networkResponse.data, "UTF-8");
                        JSONObject data = new JSONObject(response_data);

                        if (data.has("response")) {
                            JSONObject data_response = data.getJSONObject("response");

                            if(data_response.has("code")){
                                if(data_response.getInt("code") == 404){
                                    return;
                                }
                            }
                        }
                    }

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

    private boolean validateData() throws JSONException{
        Boolean v = false;

        if(!verification_data.has("id_card")){
            displaySnackBar(getStr(R.string.f_idcard_empty));
            return v;
        }else{
            if(verification_data.getString("id_card").isEmpty()){
                displaySnackBar(getStr(R.string.f_idcard_empty));
                return v;
            }else if(verification_data.getString("id_card").length() < 6){
                displaySnackBar(getStr(R.string.w_idcard_digit));
                return v;
            }
        }

        if(!verification_data.has("id_card_uri")){
            displaySnackBar(getStr(R.string.f_idcardphoto_empty));
            return v;
        }

        if(!verification_data.has("selfie_uri")){
            displaySnackBar(getStr(R.string.f_selfie_empty));
            return v;
        }

        if(!verification_data.has("signature_uri")){
            displaySnackBar(getStr(R.string.f_signature_empty));
            return v;
        }

        return true;
    }

    private void submit() throws JSONException, FileNotFoundException {
        if(!validateData()){
            return;
        }else{
            show_wait_modal();

            Uri ktp_uri = Uri.parse(verification_data.getString("id_card_uri"));
            Uri selfie_uri = Uri.parse(verification_data.getString("selfie_uri"));
            Uri signature_uri = Uri.parse(verification_data.getString("signature_uri"));

            JSONObject arr = new JSONObject();
            arr.put("nik",verification_data.getString("id_card"));
            arr.put("photoKTP",resizeBase64Image(ktp_uri));
            arr.put("photoSelfie",resizeBase64Image(selfie_uri));
            arr.put("photoKTPsign",resizeBase64Image(signature_uri));
            arr.put("idLogin",user_SP.getString("idLogin",""));
            arr.put("idUser",user_SP.getString("idUser",""));
            arr.put("token",user_SP.getString("token",""));

            url = BASE_URL+"/mobile/account/basic-verification";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            finish();
                            Intent intent = new Intent(VerificationIntroActivity.this, MainActivity.class);
                            response_data.put("dMsg", getStr(R.string.i_verification_waiting));
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
}
