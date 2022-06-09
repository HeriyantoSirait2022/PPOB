package com.qdi.rajapay.account.manage_account.edit_password;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

/**
 * @module 8.4 Password Akun
 * @screen 8.4.1
 */
public class EditPasswordIndexActivity extends BaseActivity {
    EditText password;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_password_index);

        init_toolbar(getResources().getString(R.string.activity_title_new_password));
        init();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.password_empty_label));
                else{
                    startActivity(new Intent(EditPasswordIndexActivity.this,EditPasswordConfirmationActivity.class)
                            .putExtra("password",password.getText().toString()));
                }
            }
        });

        init_show_password(password);
    }

    private void init(){
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
    }
}