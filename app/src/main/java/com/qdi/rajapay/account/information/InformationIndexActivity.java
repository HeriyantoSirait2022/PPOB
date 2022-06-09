package com.qdi.rajapay.account.information;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONArray;
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
 * @module 8.10 Informasi
 * @screen 8.10.1
 */

public class InformationIndexActivity extends BaseActivity {
    RecyclerView list;
    TextView version_info;

    InformationIndexAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ArrayList<Class> array_class = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information_index);

        init_toolbar(getResources().getString(R.string.account_information_title));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new InformationIndexAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(InformationIndexActivity.this,InformationPrivacyPolicyActivity.class)
                        .putExtra("data",array.get(position).toString()));
            }
        });
    }

    private void init() throws JSONException, PackageManager.NameNotFoundException {
        list = findViewById(R.id.list);
        version_info = findViewById(R.id.version_info);

//        prepare_data();
        adapter = new InformationIndexAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        version_info.setText(getResources().getString(R.string.information_version_app)+" "+pInfo.versionName);

        get_data();
    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.account_information_refund));
        array.add(jsonObject);
        array_class.add(InformationRefundActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.account_information_privasi));
        array.add(jsonObject);
        array_class.add(InformationPrivacyPolicyActivity.class);

    }

    private void get_data() {
        url = BASE_URL+"/mobile/account/show-info";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray array_info = response_data.getJSONArray("arrInfoList");
                        for(int x = 0;x < array_info.length();x++){
                            array_info.getJSONObject(x).put("text",array_info.getJSONObject(x).getString("infoName"));
                            array.add(array_info.getJSONObject(x));
                        }
                        adapter.notifyDataSetChanged();
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
}