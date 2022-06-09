package com.qdi.rajapay.account.manage_account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.account.manage_account.change_pin.ChangePinIndexActivity;
import com.qdi.rajapay.account.manage_account.edit_email.EditEmailIndexActivity;
import com.qdi.rajapay.account.manage_account.edit_name.EditNameEnterPasswordActivity;
import com.qdi.rajapay.account.manage_account.edit_password.EditPasswordIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 8.1 Atur Akun
 * @screen 8.1.1
 */

public class ManageAccountIndexActivity extends BaseActivity {
    ImageView image;
    TextView name,role;
    RecyclerView list;

    ManageAccountIndexAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ArrayList<Class> array_class = new ArrayList<>();

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage_index);

        init_toolbar(getResources().getString(R.string.activity_title_manage_account));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new ManageAccountIndexAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(ManageAccountIndexActivity.this,array_class.get(position)));
            }
        });

        Intent intent = getIntent();
        showMessage(intent);
    }

    @Override
    protected void onResume() {
        get_data();
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        showMessage(intent);
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        role = findViewById(R.id.role);

        prepare_data();
        adapter = new ManageAccountIndexAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        JSONObject user_data = new JSONObject(user_SP.getString("user",""));
        data = new JSONObject();
        data.put("name",user_data.getString("shopName"));
        data.put("role",user_data.getString("role"));

        name.setText(data.getString("name"));
        role.setText("("+data.getString("role")+")");
    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_manage_account_change_name_title));
        jsonObject.put("description", getResources().getString(R.string.account_manage_account_change_name_subtitle));
        jsonObject.put("image", R.drawable.ic_perm_contact_calendar_black_24);
        array.add(jsonObject);
        array_class.add(EditNameEnterPasswordActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_manage_account_change_email_title));
        jsonObject.put("description", getResources().getString(R.string.account_manage_account_change_email_subtitle));
        jsonObject.put("image", R.drawable.ic_mail_outline_black_24);
        array.add(jsonObject);
        array_class.add(EditEmailIndexActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_manage_account_change_password_title));
        jsonObject.put("description", getResources().getString(R.string.account_manage_account_change_password_subtitle));
        jsonObject.put("image", R.drawable.ic_lock_black_24);
        array.add(jsonObject);
        array_class.add(EditPasswordIndexActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_manage_account_change_pin_title));
        jsonObject.put("description", getResources().getString(R.string.account_manage_account_change_pin_subtitle));
        jsonObject.put("image", R.drawable.ic_verified_user_black_24);
        array.add(jsonObject);
        array_class.add(ChangePinIndexActivity.class);
    }

    private void get_data() {
        url = BASE_URL+"/mobile/account/user-info";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        user_edit_SP.putString("user",response_data.toString());
                        user_edit_SP.commit();

                        name.setText(response_data.getString("shopName"));
                        role.setText("("+response_data.getString("role")+")");
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
                    dismiss_wait_modal();
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