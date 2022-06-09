package com.qdi.rajapay.main_menu.emoney;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.electrical.ElectricalPriceListActivity;
import com.qdi.rajapay.main_menu.water.WaterChooseAreaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.23 Uang Elektronik
 * @screen 4.23.1
 */
public class EmoneySelectActivity extends BaseActivity {
    RecyclerView list;

    EmoneyProductSelectAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    EditText search;
    TextView chooseProduct;
    ArrayList<JSONObject> temp = new ArrayList();

    View hasDataLayout, noDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_water_choose_area);

        init_toolbar(getResources().getString(R.string.main_menu_emoney_title));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new EmoneyProductSelectAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                if(search.getText().length() == 0){
                    // @notes : use array variable because user assume not searching anyting
                    // or perhaps already search but canceled search
                    startActivity(new Intent(EmoneySelectActivity.this, EmoneyInputActivity.class)
                            .putExtra("selected_product",array.get(position).toString()));
                }else{
                    // @notes : use temp array because user search pdam area
                    startActivity(new Intent(EmoneySelectActivity.this, EmoneyInputActivity.class)
                            .putExtra("selected_product",temp.get(position).toString()));
                }

            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
    }

    void filter(String text){
        temp = new ArrayList<>();
        for(JSONObject d: array){
            try{
                if(d.getString("text").toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        adapter = new EmoneyProductSelectAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        search = findViewById(R.id.search);
        search.setHint(R.string.hint_search_product);

        hasDataLayout = findViewById(R.id.has_data_layout);
        noDataLayout = findViewById(R.id.empty_data_layout);

        chooseProduct = findViewById(R.id.choose_product);
        chooseProduct.setText(R.string.main_menu_emoney_choose_product);

        get_data();
    }

    private void get_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/prepaid/emoney-list";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONObject emoneyList = response_data.getJSONObject("emoneyList");

                        JSONArray jsonArray = emoneyList.getJSONArray("EMONEY");
                        array.clear();
                        for(int x = 0; x < jsonArray.length(); x++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            jsonObject.put("text",jsonObject.getString("productName"));
                            jsonObject.put("image_url",jsonObject.getString("image"));
                            jsonObject.put("is_trouble", jsonObject.getInt("isProblem") != 0);

                            array.add(jsonObject);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }

                    onUpdate(array.isEmpty());
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    dismiss_wait_modal();
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

                onUpdate(array.isEmpty());
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

    public void onUpdate(boolean isEmpty) {
        if(isEmpty) {
            noDataLayout.setVisibility(View.VISIBLE);
            hasDataLayout.setVisibility(View.GONE);
        }else{
            noDataLayout.setVisibility(View.GONE);
            hasDataLayout.setVisibility(View.VISIBLE);
        }
    }
}