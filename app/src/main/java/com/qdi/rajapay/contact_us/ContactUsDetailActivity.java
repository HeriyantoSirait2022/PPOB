package com.qdi.rajapay.contact_us;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

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
 * @module 4.3 Hubungi Kami
 * @screen 4.3.2
 */
public class ContactUsDetailActivity extends BaseActivity {
    TextView title,date,id,status;
    RecyclerView list;

    ContactUsDetailAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_detail);

        init_toolbar(getResources().getString(R.string.activity_title_trouble_transaction));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new ContactUsDetailAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(ContactUsDetailActivity.this,ContactUsConfirmationActivity.class)
                        .putExtra("data",getIntent().getStringExtra("data"))
                        .putExtra("complaint_data",array.get(position).toString()));
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        id = findViewById(R.id.id);
        status = findViewById(R.id.status);

        data = new JSONObject(getIntent().getStringExtra("data"));
        title.setText(data.getString("title"));
        date.setText(data.getString("date"));
        status.setText(data.getString("status"));
        id.setText(data.getString("id"));

        adapter = new ContactUsDetailAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(divider);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        setColorFromDisplayStr(status,data.getString("status"),this);

        get_data();
    }

    private void get_data() throws JSONException {
        url = BASE_URL+"/mobile/contact-us/questions";
        Log.d("url",url);

        JSONObject param = getBaseAuth();
        param.put("typeTxn", data.getString("typeTxn"));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        for(int x = 0; x < response_data.getJSONArray("arrQuestRslt").length(); x++){
                            JSONObject data_question = response_data.getJSONArray("arrQuestRslt").getJSONObject(x);

                            data_question.put("title",data_question.getString("question"));
                            array.add(data_question);
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