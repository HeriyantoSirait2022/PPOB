package com.qdi.rajapay.account.verification;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.ChooseMediaPickerModal;
import com.qdi.rajapay.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @module 8.7 Verifikasi Akun
 * @screen 8.7.4
 */
public class VerificationUploadSelfieActivity extends BaseActivity {
    Button submit,example;
    ImageView image,upload;

    JSONObject data;
    Uri image_uri;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification_upload_selfie);

        init_toolbar(getResources().getString(R.string.activity_title_selfie));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(image_uri == null) {
                        show_error_message(layout, getResources().getString(R.string.account_verification_upload_selfie_empty_picture));
                        return;
                    }

                    data.put("selfie_uri", image_uri);

                    user_edit_SP.putString("verification_data",data.toString());
                    user_edit_SP.commit();

                    show_error_message(layout,"Data berhasil disimpan");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            startActivity(new Intent(VerificationUploadSelfieActivity.this, VerificationIntroActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    },delay_time_for_alert);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseMediaPickerModal modal = new ChooseMediaPickerModal("selfie.jpg");
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseMediaPickerModal modal = new ChooseMediaPickerModal("selfie.jpg");
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificationUploadSelfieModal modal = new VerificationUploadSelfieModal();
                modal.show(getSupportFragmentManager(),"modal");
            }
        });
    }

    private void init() throws JSONException {
        upload = findViewById(R.id.upload);
        submit = findViewById(R.id.submit);
        image = findViewById(R.id.image);
        example = findViewById(R.id.example);

        data = new JSONObject(user_SP.getString("verification_data","{}"));

        String contentUri = data.getString("selfie_uri");
        if (isFileExists(contentUri)) {
            image_uri = Uri.parse(contentUri);
            show_image();
        }else{
            displaySnackBar(getStr(R.string.w_image_notexists));
        }
    }

    private void show_image(){
        upload.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(image_uri)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .resize(1280, 720)
                .centerInside()
                .into(image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_INTENT)
                image_uri = camera_uri;
            else if(requestCode == GALLERY_INTENT) {
                image_uri = data.getData();
                /**
                 * @author Eliza Suntantya
                 * @patch FR19022
                 * @notes add uri permission for api level > kitkat
                 */
                // <code>
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getContentResolver().takePersistableUriPermission(image_uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                // </code>
            }

            if(validateDataType(image_uri)){
                show_image();
            }else{
                displaySnackBar(getStr(R.string.f_filename_format));
            }
        }
    }
}