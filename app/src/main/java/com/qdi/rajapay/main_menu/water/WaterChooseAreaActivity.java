package com.qdi.rajapay.main_menu.water;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.electrical.ElectricalPriceListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.17 PDAM
 * @screen 4.17.1
 */
public class WaterChooseAreaActivity extends BaseActivity {
    RecyclerView list;

    WaterChooseAreaAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>(), add_ons_array = new ArrayList<>();

    /**
     * @author Jesslyn
     * @note  0721531311-177 E02 Add search for PDAM merchant at 4.17.1
     */
    // <code>
    EditText search;
    ArrayList<JSONObject> temp = new ArrayList();
    // </code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_water_choose_area);

        init_toolbar(getResources().getString(R.string.main_menu_water_choose_provider_toolbar));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * @author : Jesslyn
         * @note : change behavior of sending extras
         */
        // <code>
        adapter.setOnItemClickListener(new WaterChooseAreaAdapter.ClickListener() {
            @Override
            public void onClick(int position) {

                if(search.getText().length() == 0){
                    // @notes : use array variable because user assume not searching anyting
                    // or perhaps already search but canceled search
                    startActivity(new Intent(WaterChooseAreaActivity.this, WaterInputNoActivity.class)
                            .putExtra("data_provider",array.toString())
                            .putExtra("selected", position));
                }else{
                    // @notes : use temp array because user search pdam area
                    startActivity(new Intent(WaterChooseAreaActivity.this, WaterInputNoActivity.class)
                            .putExtra("data_provider",temp.toString())
                            .putExtra("selected", position));
                }

            }
        });
        // </code>

        /**
         * @author Jesslyn
         * @note  0721531311-177 E02 Add search for PDAM merchant at 4.17.1
         */
        // <code>
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
        // </code>
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

        adapter = new WaterChooseAreaAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        search = findViewById(R.id.search);

        get_data();

        // <code>
        /**
         * @author : Jesslyn
         * @note : Add price tag icon at the top-left activity
         */
        preparePriceTag(this, ElectricalPriceListActivity.class);
        add_ons_view_array.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WaterChooseAreaActivity.this, WaterPriceListActivity.class)
                        .putExtra("data_provider", array.toString()));
            }
        });
        // </code>
    }

    /**
     * @author : Jesslyn
     * @note : add wait modal
     */
    // <code>
    private void get_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/postpaid/pdam-feeinfo";

        JSONObject param = getBaseAuth();
        param.put("productType", "PDAM");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray jsonArray = response_data.getJSONArray("arrFeePdamRslt");
                        array.clear();
                        for(int x = 0; x < jsonArray.length(); x++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            jsonObject.put("text",jsonObject.getString("nameProduct"));
                            jsonObject.put("price",jsonObject.getDouble("adminFee"));
                            jsonObject.put("price_str","Rp. "+formatter.format(jsonObject.getDouble("adminFee")));
                            jsonObject.put("is_trouble",jsonObject.getInt("isProblem") == 0 ? false : true);

                            array.add(jsonObject);
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
    // </code>
}