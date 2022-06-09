package com.qdi.rajapay.main_menu.bank_transfer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * @module 4.24 Transfer Bank
 * @screen 4.24.2
 */
public class BankTransferInputActivity extends BaseActivity {
    ImageView productImage;
    EditText no;
    Button next;
    TextView selectedProduct, productAction, textRule;
    LinearLayout productRule;

    JSONObject selectedData;
    String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_water_input_no);

        init_toolbar(getResources().getString(R.string.home_transfer_bank));
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

    private void init() throws JSONException {
        productImage = findViewById(R.id.product_image);
        productImage.setImageDrawable(getDrawable(R.drawable.ic_transfer_bank));

        productAction = findViewById(R.id.product_action);
        productAction.setText(getString(R.string.main_menu_banktransfer_input_bank_account));

        no = findViewById(R.id.no);
        no.setHint(getResources().getString(R.string.hint_bank_account));

        next = findViewById(R.id.next);
        selectedProduct = findViewById(R.id.selected_product);

        selectedData = new JSONObject(getIntent().getStringExtra("selected_product"));
        productName = getString(R.string.main_menu_banktransfer_title) + " - " + selectedData.getString("productName");
        selectedProduct.setText(productName);

        productRule = findViewById(R.id.product_rule);
        productRule.setVisibility(View.VISIBLE);

        textRule = findViewById(R.id.text_rule);
        textRule.setText(getString(R.string.main_menu_banktransfer_rule));

        initNumpadKeyboard(no, new NumpadKeyboardSubmit() {
            @Override
            public void onSubmit() {
                onNext();
            }
        });
    }

    private void onNext(){
        if(no.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.main_menu_banktransfer_error_empty_account_no));
        else {
            startActivity(
                    new Intent(BankTransferInputActivity.this, BankTransferDenominationActivity.class)
                            .putExtra("selected_product", selectedData.toString())
                            .putExtra("product_name", productName)
                            .putExtra("product_account_no", no.getText().toString())
            );
        }
    }
}