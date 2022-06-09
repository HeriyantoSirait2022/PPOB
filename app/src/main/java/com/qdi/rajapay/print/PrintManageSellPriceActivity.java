package com.qdi.rajapay.print;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.enums.TransactionType;
import com.qdi.rajapay.utils.NumberUtils;
import com.qdi.rajapay.utils.PriceKeyboard;

/**
 * @module 5.1 Transaksi
 * @sccreen 5.1.3
 *
 * @module 6.18 Laporan Bulanan
 * @screen 6.18.6+
 */
public class PrintManageSellPriceActivity extends BaseActivity {
    EditText price;
    Button submit;
    PriceKeyboard pk;
    OrderData data;
    Double minPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_manage_sell_price);

        init_toolbar(getResources().getString(R.string.cashier_manage_sell_price));
        init();
    }

    private void submit(){
        if(price.getText().toString().equals("")) {
            show_error_message(layout, getResources().getString(R.string.print_confirmation_manage_sell_price_empty_sell_price));
            return;
        }

        double priceDouble = Double.valueOf(price.getText().toString().replace(".",""));
        if(priceDouble < minPayment) {
            show_error_message(layout, getResources().getString(R.string.print_confirmation_manage_sell_price_lower_sell_price));
        } else {
            setResult(Activity.RESULT_OK, new Intent()
                    .putExtra("sell_price", priceDouble));
            finish();
        }
    }

    private void init() {
        data = (OrderData) getIntent().getSerializableExtra("data");

        price = findViewById(R.id.price);
        submit = findViewById(R.id.submit);
        pk = findViewById(R.id.price_keyboard);


        if (Build.VERSION.SDK_INT >= 11) {
            price.setRawInputType(InputType.TYPE_CLASS_TEXT);
            price.setTextIsSelectable(true);
        } else {
            price.setRawInputType(InputType.TYPE_NULL);
            price.setFocusable(true);
        }

        price.setShowSoftInputOnFocus(false);
        price.setMaxLines(1);
        price.setImeOptions(EditorInfo.IME_ACTION_DONE);

        /**
         * @authors : Jesslyn
         * @notes : tune up algorithm to making function call only one time
         */

        // <code>
        String val = data.getFormattedTxnSellPriceHint();
        minPayment = data.getMinPayment();

        /**
         * @author Eliza Sutantya
         * @patch FR19022
         * @notes exclude prepaid product master that doesn't have product detail
         * ...... please maintain here otherwise agen price (that had been set on kasir->atur penjualan won't works
         */
        // <code>
        if(data.getTypeTxn() == TransactionType.PREPAID_BANKTRANSFER){
            val = NumberUtils.format(data.getBillAmount() + data.getAgenPrice());
        }
        // </code>

        price.setHint(val);
        price.setText(val);
        // </code>

        if(data.getSellingData() != null) {
            /**
             * @authors : Jesslyn
             * @notes : optimize setHint
             */
            // <code>
            val = data.getSellingData().getFormattedTxnSellPrice();
            price.setText(val);
            price.setHint(val);
            // </code>
        }

        price.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    submit();
                }
                return handled;
            }
        });

        super.edittext_currency(price);

        InputConnection ic = price.onCreateInputConnection(new EditorInfo());
        pk.setInputConnection(ic);
    }
}