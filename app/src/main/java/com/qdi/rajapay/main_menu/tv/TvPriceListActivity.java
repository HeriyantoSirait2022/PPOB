package com.qdi.rajapay.main_menu.tv;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

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

/**
 * @module 4.18 TV Berlangganan
 * @screen 4.18.7
 */
public class TvPriceListActivity extends BaseActivity {
    ExpandableListView list;

    TvPriceListAdapter adapter;
    ArrayList<JSONObject> array_header = new ArrayList<>();
    HashMap<JSONObject,ArrayList<JSONObject>> array_child = new HashMap<>();

    JSONArray data_provider = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_tv_price_list);

        init_toolbar(getResources().getString(R.string.activity_title_price_list));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new TvPriceListAdapter.ClickListener() {
            @Override
            public void onClick(int group_position, int child_position) {
                /**
                 * @author : Jesslyn
                 * @note : remove price list item event. Confirm Mr. Yo
                 */
                // <code>
                /*
                startActivity(new Intent(TvPriceListActivity.this, TvInputNoActivity.class)
                        .putExtra("data_provider",getIntent().getStringExtra("data_provider")));
                 */
                // </code>
            }
        });

        list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return list.isGroupExpanded(groupPosition) ? list.collapseGroup(groupPosition) : list.expandGroup(groupPosition);
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        /**
         * @author : Jesslyn
         * @note : Change behavior for handing extras
         */
        // <code>
        String extras = (String) isExtras(getIntent(), "data_provider");
        if(isNullOrEmpty(extras)){
            get_data();
        }else{
            data_provider = new JSONArray(extras);
            prepare_data();
        }
        // </code>

        adapter = new TvPriceListAdapter(this,array_header,array_child);
        list.setAdapter(adapter);
    }

    /**
     * @author : Jesslyn
     * @note : add backup method if intent doesn't exist (following documentation)
     * @throws JSONException
     */
    // <code>
    private void get_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/postpaid/tv-feeinfo";

        JSONObject param = getBaseAuth();
        param.put("productType", "TVSUB");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray jsonArray = response_data.getJSONArray("arrFeeTvRslt");
                        data_provider = new JSONArray();
                        for(int x = 0; x < jsonArray.length(); x++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            jsonObject.put("text",jsonObject.getString("nameProduct"));
                            jsonObject.put("price",jsonObject.getDouble("adminFee"));
                            jsonObject.put("price_str","Rp. "+formatter.format(jsonObject.getDouble("adminFee")));
                            jsonObject.put("image_url",jsonObject.getString("image"));
                            jsonObject.put("image",null);
                            if(jsonObject.getInt("isProblem") == 0)
                                jsonObject.put("detail", !jsonObject.isNull("detailToken") ? jsonObject.getString("detailToken") : "-");
                            else
                                jsonObject.put("detail", getResources().getString(R.string.trouble_label));
                            jsonObject.put("is_important", jsonObject.getInt("isProblem") == 1);

                            ArrayList<JSONObject> arrayList = new ArrayList<>();
                            arrayList.add(jsonObject);
                            jsonObject.put("arr",new JSONArray(arrayList.toString()));

                            data_provider.put(jsonObject);
                        }
                        prepare_data();
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

    private void prepare_data() throws JSONException {
        for(int x = 0; x < data_provider.length(); x++) {
            array_header.add(data_provider.getJSONObject(x));

            ArrayList<JSONObject> arrayList = new ArrayList<>();
            for(int y = 0; y < data_provider.getJSONObject(x).getJSONArray("arr").length(); y++) {
                JSONObject jsonObject = data_provider.getJSONObject(x).getJSONArray("arr").getJSONObject(y);
                jsonObject.put("text",jsonObject.getString("text"));
                arrayList.add(jsonObject);
            }
            array_child.put(array_header.get(x), arrayList);
        } ;
    }
}