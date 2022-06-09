package com.qdi.rajapay.contact_us;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.ChooseMediaPickerModal;
import com.qdi.rajapay.R;
import com.qdi.rajapay.inbox.InboxListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 4.3 Hubungi Kami
 * @screen 4.3.3
 */
public class ContactUsConfirmationActivity extends BaseActivity {
    Button back,send_now;
    TextView title,date,id,status;
    RecyclerView list;

    ContactUsConfirmationAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    JSONObject data,complaint_data;
    int selected_position = 0;
    String card_name = "", card_no = "", acc_no = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_confirmation);

        init_toolbar(getResources().getString(R.string.activity_title_confirmation));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        send_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean flag = false;
                    if(data.getString("typeTxn").equals("TOPUPDEPOSITBT")) {
                        for (int x = 0; x < array.size(); x++) {
                            if (array.get(x).has("data") &&
                                    array.get(x).getString("data").equals("last6DigitCard") &&
                                    array.get(x).getString("value").length() < 6) {
                                flag = true;
                                show_error_message(layout, "Nomor kartu debit kurang dari 6 karakter");
                                break;
                            }
                        }
                    }

                    if(!flag)
                        submit();
                } catch (JSONException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        adapter.setOnItemClickListener(new ContactUsConfirmationAdapter.ClickListener() {
            @Override
            public void onUploadClick(int position) {
                selected_position = position;
                ChooseMediaPickerModal modal = new ChooseMediaPickerModal("contact-us.jpg");
                modal.show(getSupportFragmentManager(),"modal");
            }

            @Override
            public void onChangeImageClick(int position) {
                selected_position = position;
                ChooseMediaPickerModal modal = new ChooseMediaPickerModal("contact-us.jpg");
                modal.show(getSupportFragmentManager(),"modal");
            }

            @Override
            public void onInputChange(int position) {
                try {
                    if(array.get(position).getString("data").equals("shopName"))
                        card_name = array.get(position).getString("value");
                    else if(array.get(position).getString("data").equals("noAccount"))
                        acc_no = array.get(position).getString("value");
                    else if(array.get(position).getString("data").equals("last6DigitCard"))
                        card_no = array.get(position).getString("value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        back = findViewById(R.id.back);
        send_now = findViewById(R.id.send_now);
        list = findViewById(R.id.list);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        id = findViewById(R.id.id);
        status = findViewById(R.id.status);

        data = new JSONObject(getIntent().getStringExtra("data"));
        complaint_data = new JSONObject(getIntent().hasExtra("complaint_data") ?
                getIntent().getStringExtra("complaint_data") : "{}");

        prepare_data();
        adapter = new ContactUsConfirmationAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        title.setText(data.getString("title"));
        date.setText(data.getString("date"));
        status.setText(data.getString("status"));
        id.setText(data.getString("id"));

        setColorFromDisplayStr(status,data.getString("status"),this);

//        if(!data.getString("status").equals("Menunggu") && !data.getString("status").equals("Proses"))
//            send_now.setVisibility(View.GONE);
    }

    private void prepare_data() throws JSONException {
        JSONObject user_data = new JSONObject(user_SP.getString("user",""));

        JSONObject data = new JSONObject();
        data.put("key","Masalah:");
        data.put("value",complaint_data.has("question") ? complaint_data.getString("question") : "-");
        array.add(data);

        data = new JSONObject();
        data.put("key","Detail Masalah:");
        data.put("value",complaint_data.has("detail") ? complaint_data.getString("detail") : "-");
        array.add(data);

        if(this.data.getString("typeTxn").equals("TOPUPDEPOSITBT")) {
            data = new JSONObject();
            data.put("key", "Nama Pemilik Kartu Debit:");
            data.put("value", "");
//            if(this.data.getString("status").equals("Menunggu") || this.data.getString("status").equals("Proses"))
                data.put("type", "input");
            data.put("input_type", "text");
            data.put("data", "shopName");
            array.add(data);

            data = new JSONObject();
            data.put("key", "Nomor Rekening Pemilik Kartu:");
            data.put("value", "");
//            if(this.data.getString("status").equals("Menunggu") || this.data.getString("status").equals("Proses"))
                data.put("type", "input");
            data.put("data", "noAccount");
            array.add(data);

            data = new JSONObject();
            data.put("key", "Nomor Kartu Debit (6 Digit Terakhir):");
            data.put("value", "");
//            if(this.data.getString("status").equals("Menunggu") || this.data.getString("status").equals("Proses"))
                data.put("type", "input");
            data.put("data", "last6DigitCard");
            array.add(data);
        }

        data = new JSONObject();
        data.put("key","Lampiran:");
        data.put("value","-");
        array.add(data);

    }

    private void submit() throws JSONException, FileNotFoundException {
        if(data.getString("typeTxn").equals("TOPUPDEPOSITBT") && card_name.equals(""))
            show_error_message(layout,getResources().getString(R.string.contact_us_confirmation_card_name_empty));
        else if(data.getString("typeTxn").equals("TOPUPDEPOSITBT") && card_no.equals(""))
            show_error_message(layout,getResources().getString(R.string.contact_us_confirmation_card_no_empty));
        else if(data.getString("typeTxn").equals("TOPUPDEPOSITBT") && acc_no.equals(""))
            show_error_message(layout,getResources().getString(R.string.contact_us_confirmation_acc_no_empty));
        else if(image_uri == null)
            show_error_message(layout,getResources().getString(R.string.contact_us_confirmation_attachment_empty));
        else if(data.getString("typeTxn").equals("TOPUPDEPOSITBT") && card_name.length() > 100)
            show_error_message(layout,getResources().getString(R.string.contact_us_confirmation_card_name_length_exceed));
        else if(data.getString("typeTxn").equals("TOPUPDEPOSITBT") && acc_no.length() > 25)
            show_error_message(layout,getResources().getString(R.string.contact_us_confirmation_acc_no_length_exceed));
        else {
            JSONObject arr = new JSONObject();
            arr.put("idOrder", data.getString("idOrder"));
            arr.put("dtOrder", data.getString("dtOrder"));
            arr.put("nmTxn", data.getString("nmTxn"));
            arr.put("typeTxn", data.getString("typeTxn"));
            arr.put("detailProblem", complaint_data.getString("detail"));
            arr.put("photoTxn", uri_to_string(image_uri));
            arr.put("idLogin", user_SP.getString("idLogin", ""));
            arr.put("idUser", user_SP.getString("idUser", ""));
            arr.put("token", user_SP.getString("token", ""));
            for (int x = 0; x < array.size(); x++) {
                if (array.get(x).has("type") &&
                        array.get(x).getString("type").equals("input") &&
                        !array.get(x).getString("value").equals(""))
                    arr.put(array.get(x).getString("data"), array.get(x).getString("value"));
            }
            if(data.getString("typeTxn").equals("TOPUPDEPOSITBT")) {
                arr.put("name", card_name);
                arr.put("noAccount", acc_no);
                arr.put("last6DigitCard", card_no);
            }

            url = BASE_URL + "/mobile/contact-us/send";
            show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            show_error_message(layout, "Keluhan telah disimpan");

                            startActivity(new Intent(ContactUsConfirmationActivity.this, InboxListActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .putExtra("caller", ContactUsConfirmationActivity.class.getCanonicalName())
                                    .putExtra("selected", "ticket"));
                        } else {
                            show_error_message(layout, response_data.getString("message"));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_INTENT){
                image_uri = camera_uri;
            }
            else if(requestCode == GALLERY_INTENT){
                String dataString = data.getDataString();
                image_uri = Uri.parse(dataString);
            }
            try {
                array.get(selected_position).put("image_url",image_uri);
                adapter.notifyItemChanged(selected_position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}