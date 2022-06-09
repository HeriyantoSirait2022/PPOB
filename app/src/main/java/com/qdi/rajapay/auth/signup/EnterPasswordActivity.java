package com.qdi.rajapay.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.auth.ConditionPasswordAdapter;
import com.qdi.rajapay.auth.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @module 2.0 Daftar
 * @screen 2.3
 */
public class EnterPasswordActivity extends BaseActivity {
    EditText password;
    Button next;
    RecyclerView list;
    TextView signup;

    JSONObject data;

    ConditionPasswordAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    Boolean is_password_accepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_enter_password);

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

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    check_condition(array,charSequence);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterPasswordActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        init_show_password(password);
    }

    private void init() throws JSONException {
        password = findViewById(R.id.password);
        next = findViewById(R.id.next);
        signup = findViewById(R.id.signup);
        list = findViewById(R.id.list);
        layout = findViewById(R.id.layout);

        data = new JSONObject(getIntent().getStringExtra("data"));

        array = add_condition_password();
        adapter = new ConditionPasswordAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void submit() throws JSONException {
        if(password.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.password_empty_label));
        else {
            int counter = 0;
            for(int x=0;x<array.size();x++){
                if(!array.get(x).getBoolean("is_fulfill_condition"))
                    break;
                counter++;
            }

            if(counter == array.size())
                is_password_accepted = true;
            else
                is_password_accepted = false;

            if(!is_password_accepted)
                show_error_message(layout,getResources().getString(R.string.password_condition_not_met_label));
            else {
                data.put("password", password.getText().toString());
                startActivity(new Intent(EnterPasswordActivity.this, EnterReferalCodeActivity.class)
                        .putExtra("data", data.toString()));
            }
        }
    }
}