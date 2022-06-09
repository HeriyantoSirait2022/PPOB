package com.qdi.rajapay.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.auth.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @module 2.0 Daftar
 * @screen 2.2
 */
public class EnterNameActivity extends BaseActivity {
    EditText name;
    Button next;
    TextView signup;

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_enter_name);

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterNameActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }

    private void init() throws JSONException {
        name = findViewById(R.id.name);
        next = findViewById(R.id.next);
        signup = findViewById(R.id.signup);
        layout = findViewById(R.id.layout);

        data = new JSONObject(getIntent().getStringExtra("data"));
    }

    private void submit() throws JSONException {
        if(name.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.account_manage_account_change_name_empty_name));
        else {
            data.put("name",name.getText().toString());
            startActivity(new Intent(EnterNameActivity.this, EnterPasswordActivity.class)
                    .putExtra("data", data.toString()));
        }
    }
}