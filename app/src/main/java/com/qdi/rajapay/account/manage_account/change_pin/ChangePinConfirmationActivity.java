package com.qdi.rajapay.account.manage_account.change_pin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.utils.NumberKeyboard;

/**
 * @module 8.5 Masukan OTP
 * @screen 8.5.2
 */
public class ChangePinConfirmationActivity extends BaseActivity {
    EditText pin;
    Button submit;
    NumberKeyboard nk;
    InputConnection ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_change_pin_confirmation);

        init_toolbar(getResources().getString(R.string.activity_title_verify_new_pin));
        init();
        action();
    }

    private void init() {
        pin = findViewById(R.id.pin);
        submit = findViewById(R.id.submit);

        pin.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        pin.requestFocus();
        nk = findViewById(R.id.number_keyboard);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        ic = pin.onCreateInputConnection(new EditorInfo());
        nk.setInputConnection(ic);
    }

    private void action(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pin.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_pin_empty_confirmation_pin));
                else if(pin.getText().length() < 6)
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_pin_length_not_satisfied));
                else
                    submit();
            }
        });
    }

    private void submit(){
        if(!pin.getText().toString().equals(getIntent().getStringExtra("pin")))
            show_error_message(layout,getResources().getString(R.string.account_manage_account_change_pin_not_same_with_confirmation));
        else {
            /**
             * @author Jesslyn
             * @note 0721531311-179 E05 New user required to change PIN at the first time. New feature would be enhanced at 4.10 - 4.20
             */
            // <code>
            if(getIntent().hasExtra("reqPinChange")){
                if(getIntent().getBooleanExtra("reqPinChange", false)){
                    startActivity(new Intent(ChangePinConfirmationActivity.this, ChangePinEnterOtpActivity.class)
                            .putExtra("pin",  pin.getText().toString())
                            .putExtra("reqPinChange", true)
                    );
                }
            }else{
                startActivity(new Intent(ChangePinConfirmationActivity.this, ChangePinEnterOtpActivity.class)
                        .putExtra("pin",  pin.getText().toString()));
            }
            // </code>
        }
    }
}