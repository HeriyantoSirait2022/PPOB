package com.qdi.rajapay.account.manage_account.edit_email;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

/**
 * @Module 8.3 Email Akun
 * @screen 8.3.1
 */
public class EditEmailIndexActivity extends BaseActivity {
    EditText email;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_email_index);

        init_toolbar(getResources().getString(R.string.account_manage_account_change_email_toolbar));
        init();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_email_empty_email));
                else if(!isEmailValid(email.getText()))
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_email_wrong_format));
                else if(email.getText().length() > 100)
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_email_too_long));
                else{
                    startActivity(new Intent(EditEmailIndexActivity.this, EditEmailEnterOtpActivity.class)
                            .putExtra("email",email.getText().toString()));
                }
            }
        });
    }

    private void init(){
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);
    }
}