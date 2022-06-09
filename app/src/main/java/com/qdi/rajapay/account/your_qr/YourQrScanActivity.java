package com.qdi.rajapay.account.your_qr;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @module 8.8 QR Anda
 * @screen 8.8.5
 */

public class YourQrScanActivity extends BaseActivity{
    FrameLayout frame_camera;
    ZXingScannerView scannerView;

    JSONObject result_data = new JSONObject();
    ZXingScannerView.ResultHandler rh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_your_qr_scan);

        init_toolbar();
        init();

        get_data("ZTyjLTStPUrvVe0l4gNG97BhBBMu53");
    }

    @Override
    protected void onStart() {
        scannerView.startCamera();
        super.onStart();
    }

    public void onDismiss() {
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        scannerView.stopCamera();
        super.onPause();
    }

    public void rescan(){
        if(scannerView != null){
            scannerView.resumeCameraPreview(rh);
        }
    }

    private void init(){
        frame_camera = findViewById(R.id.frame_camera);

        scannerView = new ZXingScannerView(this);
        scannerView.setAutoFocus(true);
        rh = new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                get_data(result.getText());
            }
        };

        scannerView.setResultHandler(rh);
        frame_camera.addView(scannerView);
    }

    private void get_data(String qr_code) {
        url = BASE_URL+"/mobile/account/read-deposit-qr";
        Log.d("url",url);

        JSONObject param = getBaseAuth();
        try{
            param.put("qrCode", qr_code);
        }catch(Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        result_data = response_data;

                        YourQrScanConfirmModal modal = new YourQrScanConfirmModal();
                        modal.show(getSupportFragmentManager(),"modal");
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
