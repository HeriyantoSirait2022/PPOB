package com.qdi.rajapay.account.information;

import android.os.Bundle;
import android.webkit.WebView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

public class InformationRefundActivity extends BaseActivity {
    //    TextView kebijakan_refund, kebijakan_detail;
    WebView webview_content;

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information_refund);

        init_toolbar(getResources().getString(R.string.account_information_refund));

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        webview_content = findViewById(R.id.webview_content);

        data = new JSONObject(getIntent().getStringExtra("data"));
        webview_content.loadUrl(data.getString("url"));
//        kebijakan_detail = findViewById(R.id.detail);
//
//        String kebijakan = "<strong>RajaPay</strong> berkomitmen untuk memproses refund dana pelanggan dengan ketentuan sebagai berikut :  ";
//        String detail = "1. Refund dilakukan ke rekening bank pihak    pengirim dana dilakukan selambat-lambatnya dalam 3 (tiga) hari kerja sejak permintaan refund dan data-data yang dibutuhkan sudah lengkap diberikan oleh pelanggan kepada CS Rajapay dengan mengirim email ke <b>cs@rajapay.com</b> dengan subject <b>REFUND_orderID</b>";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            kebijakan_refund.setText(Html.fromHtml(kebijakan, Html.FROM_HTML_MODE_COMPACT));
//            kebijakan_detail.setText(Html.fromHtml(detail, Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            kebijakan_refund.setText(Html.fromHtml(kebijakan));
//            kebijakan_detail.setText(Html.fromHtml(detail));
//        }
    }
}