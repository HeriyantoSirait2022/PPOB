package com.qdi.rajapay.account.your_qr;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.account.your_qr.change_nominal.YourQrChangeNominalActivity;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 8.8 QR Anda
 * @screen 8.8.1
 */
public class YourQrIndexActivity extends BaseActivity {
    TextView name, scan;
    ImageView qr;
    RecyclerView list;

    YourQrIndexAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ArrayList<Class> array_class = new ArrayList<>();

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_your_qr_index);

        init_toolbar(getResources().getString(R.string.account_your_qr_title));
        try {
            init();
        } catch (JSONException | WriterException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new YourQrIndexAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try{
                    if(array.get(position).getString("action").equals(getResources().getString(R.string.account_your_qr_share))){
                        // YourQrShareModal modal = new YourQrShareModal();
                        // modal.show(getSupportFragmentManager(),"modal");
                        shareQr();
                    }
                    else {
                        finish();
                        startActivity(new Intent(YourQrIndexActivity.this, array_class.get(position)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_verified_status();
            }
        });
    }

    private void shareQr(){
        try {
            Bitmap icon = get_qr_bitmap(data.getString("id"));
            shareImage(icon);
        } catch (JSONException | WriterException e) {
            e.printStackTrace();
        }
    }

    public void shareImage(Bitmap bitmap) {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "my_rajapay_qr_code.jpg");
        Uri imageContentUri = contentResolver.insert(contentUri, newImageDetails);

        try (ParcelFileDescriptor fileDescriptor =
                     contentResolver.openFileDescriptor(imageContentUri, "w", null)) {
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            OutputStream outputStream = new FileOutputStream(fd);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap", e);
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageContentUri);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Kode QR Rajapay Saya");
        sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendIntent.setType("image/*");
        Intent shareIntent = Intent.createChooser(sendIntent, "Bagikan dengan");
        startActivity(shareIntent);
    }

    private void init() throws JSONException, WriterException {
        list = findViewById(R.id.list);
        qr = findViewById(R.id.qr);
        name = findViewById(R.id.name);
        scan = findViewById(R.id.scan);

        prepare_data();
        adapter = new YourQrIndexAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        name.setText(data.getString("name"));
        get_data();

        Intent intent = getIntent();
        showMessage(intent);
    }

    private void get_data() {
        url = BASE_URL+"/mobile/account/show-qrcode";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        show_qr_code(qr,response_data.getString("qrCode"));
                        data.put("id",response_data.getString("qrCode"));
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException | WriterException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    error_handling(error, layout);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        consume_api(jsonObjectRequest);

    }

    private void get_verified_status() {
        url = BASE_URL+"/mobile/account/qr-status";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed") &&
                            response_data.getString("description").equals("VERIFIED")) {
                        startActivity(new Intent(YourQrIndexActivity.this,YourQrScanActivity.class));
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    error_handling(error, layout);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        consume_api(jsonObjectRequest);

    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.account_your_qr_change_nominal));
        jsonObject.put("action", getResources().getString(R.string.account_your_qr_change_nominal_button));
        jsonObject.put("icon_action", R.drawable.ic_icon_edithdpi);
        jsonObject.put("action_type", "full");
        array.add(jsonObject);
        array_class.add(YourQrChangeNominalActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.account_your_qr_share_qr_code));
        jsonObject.put("action", getResources().getString(R.string.account_your_qr_share));
        jsonObject.put("icon_action", R.drawable.ic_share_primary_24);
        jsonObject.put("action_type", "outline");
        array.add(jsonObject);
        array_class.add(YourQrChangeNominalActivity.class);

        JSONObject user_data = new JSONObject(user_SP.getString("user",""));
        data = new JSONObject();
        data.put("name",user_data.getString("shopName"));
    }
}