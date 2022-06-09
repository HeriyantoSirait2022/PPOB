package com.qdi.rajapay.account.verification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

/**
 * @module 8.7 Verifikasi Akun
 * @screen 8.7.6
 */
public class VerificationBiometricIntroActivity extends BaseActivity {
    Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification_biometric_intro);

        init_toolbar(getResources().getString(R.string.account_verification_intro_biometric));
        init();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerificationBiometricIntroActivity.this, VerificationUploadSignatureActivity.class));
            }
        });
    }

    private void init(){
        upload = findViewById(R.id.upload);
    }
}