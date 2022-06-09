package com.qdi.rajapay.main_menu.emoney;

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
 * @module 4.23 Uang Elektronik
 * @screen 4.23.1
 */
public class EmoneyInputActivity extends BaseActivity {
    ImageView productImage;
    EditText no;
    Button next;
    TextView selectedProduct, productAction;
    LinearLayout productRule;

    JSONObject selectedData;
    String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_water_input_no);

        init_toolbar(getResources().getString(R.string.main_menu_emoney_title));
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
        productImage.setImageDrawable(getDrawable(R.drawable.ic_electronic_money));

        productAction = findViewById(R.id.product_action);
        productAction.setText(getString(R.string.main_menu_emoney_input_phone_number));

        no = findViewById(R.id.no);
        no.setHint(getResources().getString(R.string.hint_phone_number));

        next = findViewById(R.id.next);
        selectedProduct = findViewById(R.id.selected_product);

        selectedData = new JSONObject(getIntent().getStringExtra("selected_product"));
        productName = getString(R.string.main_menu_topup_emoney) + " - " + selectedData.getString("productName");
        selectedProduct.setText(productName);

        productRule = findViewById(R.id.product_rule);
        productRule.setVisibility(View.VISIBLE);

        initNumpadKeyboard(no, new NumpadKeyboardSubmit() {
            @Override
            public void onSubmit() {
                onNext();
            }
        });
    }

    private void onNext(){
        if(no.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.main_menu_emoney_empty_error));
        else {
            startActivity(
                    new Intent(EmoneyInputActivity.this, EmoneyDenominationActivity.class)
                            .putExtra("selected_product", selectedData.toString())
                            .putExtra("product_name", productName)
                            .putExtra("product_phone_no", no.getText().toString())
            );
        }
    }
}