package com.qdi.rajapay.account.information;

import android.os.Bundle;
import android.webkit.WebView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

public class InformationPrivacyPolicyActivity extends BaseActivity {
    WebView webview_content;

    JSONObject data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information_privacy_policy);

        try {
            init();
            init_toolbar(data.getString("infoName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        webview_content = findViewById(R.id.webview_content);

        data = new JSONObject(getIntent().getStringExtra("data"));
        webview_content.loadUrl(data.getString("url"));
    }
}