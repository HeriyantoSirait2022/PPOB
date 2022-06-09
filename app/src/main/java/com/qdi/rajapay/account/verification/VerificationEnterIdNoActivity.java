package com.qdi.rajapay.account.verification;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.utils.NumberKeyboard;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @module 8.7 Verifikasi Akun
 * @screen 8.7.2
 */
public class VerificationEnterIdNoActivity extends BaseActivity {
    EditText id_card;
    Button submit;
    NumberKeyboard nk;
    InputConnection ic;

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification_enter_id_no);

        init_toolbar(getResources().getString(R.string.activity_title_photo_and_id_card));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(id_card.getText().toString().equals(""))
                        show_error_message(layout,getResources().getString(R.string.account_verification_enter_id_no_empty_no));
                    else if(id_card.getText().length() < maximum_id_card_no_length)
                        show_error_message(layout,getResources().getString(R.string.account_verification_enter_id_no_length_insufficient));
                    else{
                        data.put("id_card",id_card.getText().toString());
                        user_edit_SP.putString("verification_data",data.toString());
                        user_edit_SP.commit();

                        startActivity(new Intent(VerificationEnterIdNoActivity.this, VerificationUploadIdCardActivity.class));
                         //      .putExtra("data",data.toString()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        id_card.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(is_system_changed){
                    is_system_changed = false;
                    return;
                }
                if(s.toString().length() > maximum_id_card_no_length) {
                    is_system_changed = true;
                    id_card.setText(s.toString().substring(0, maximum_id_card_no_length));
                    id_card.setSelection(id_card.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() throws JSONException {
        id_card = findViewById(R.id.id_card);
        submit = findViewById(R.id.submit);
        nk = findViewById(R.id.number_keyboard);

        id_card.requestFocus();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        ic = id_card.onCreateInputConnection(new EditorInfo());
        nk.setInputConnection(ic);
        data = new JSONObject(user_SP.getString("verification_data","{}"));

        if(data.has("id_card"))
            id_card.setText(data.getString("id_card"));
    }
}