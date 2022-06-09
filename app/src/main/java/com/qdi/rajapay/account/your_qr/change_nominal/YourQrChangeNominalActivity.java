package com.qdi.rajapay.account.your_qr.change_nominal;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.deposit.TopUpAdapter;
import com.qdi.rajapay.utils.PriceKeyboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @module 8.8 QR Anda
 * @screen 8.8.2
 */
public class YourQrChangeNominalActivity extends BaseActivity {
    EditText price;
    RecyclerView list;

    TopUpAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    PriceKeyboard pk;
    InputConnection ic;

    Double admin_price = 3000d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_your_qr_change_nominal);

        init_toolbar(getResources().getString(R.string.activity_title_manage_nominal));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new TopUpAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(YourQrChangeNominalActivity.this,YourQrAddNewsActivity.class)
                        .putExtra("data",array.get(position).toString() ));
            }
        });
    }

    private void init() throws JSONException {
        price = findViewById(R.id.price);
        list = findViewById(R.id.list);

        pk = findViewById(R.id.price_keyboard);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        ic = price.onCreateInputConnection(new EditorInfo());
        pk.setInputConnection(ic);

        price.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if(price.getText().toString().isEmpty()) {
                        displaySnackBar(getStr(R.string.f_nominallimit_empty));
                        return handled;
                    }

                    Double value = Double.parseDouble(price.getText().toString().replace(".",""));
                    if(value < 1000){
                        displaySnackBar(getStr(R.string.f_nominallimit_min));
                        return handled;
                    }else if(value > 10000000){
                        displaySnackBar(getStr(R.string.f_nominallimit_max));
                        return handled;
                    }

                    try{
                        JSONObject data = new JSONObject();
                        data.put("name", "Rp. " + price.getText());
                        data.put("data", value);

                        finish();
                        startActivity(new Intent(YourQrChangeNominalActivity.this,YourQrAddNewsActivity.class)
                                .putExtra("data",data.toString()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return handled;
            }
        });

        super.edittext_currency(price);
        prepare_data();

        adapter = new TopUpAdapter(array,this);
        layout_manager = new GridLayoutManager(this,3);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void prepare_data() throws JSONException {
        String[] arr_string = new String[6];
        arr_string[0] = "Rp. 50.000";
        arr_string[1] = "Rp. 100.000";
        arr_string[2] = "Rp. 250.000";
        arr_string[3] = "Rp. 500.000";
        arr_string[4] = "Rp. 750.000";
        arr_string[5] = "Rp. 1.000.000";

        int[] arr_data = new int[6];
        arr_data[0] = 50000;
        arr_data[1] = 100000;
        arr_data[2] = 250000;
        arr_data[3] = 500000;
        arr_data[4] = 750000;
        arr_data[5] = 1000000;

        for(int x=0;x<arr_string.length;x++){
            JSONObject data = new JSONObject();
            data.put("name",arr_string[x]);
            data.put("data",arr_data[x]);
            array.add(data);
        }
    }
}