package com.qdi.rajapay.main_menu.prepaid_mobile_credit;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoAdapter;
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoCategoryAdapter;
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoModal;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @module 4.11 Prabayar Pulsa
 * @screen 4.11.1
 * @screen 4.11.6
 */
public class PrepaidMobileCreditInputPhoneNoActivity extends BaseActivity {
    LinearLayout choose_packet_layout, operator_available_layout;
    EditText phone_no;
    RecyclerView list, listCategory;
    ImageView contact, operator;
    CoordinatorLayout layout;

    PrepaidDataInputPhoneNoAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> provider_array = new ArrayList<>();

    PrepaidDataInputPhoneNoCategoryAdapter catAdapter;
    ArrayList<JSONObject> arrCat = new ArrayList<>(), selectedCat = new ArrayList<>();
    JSONObject arrProd = new JSONObject();

    JSONObject data_selected, selected_provider = new JSONObject(), data_list;
    String selected_tab = "regular";

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if(phone_no.getText().length() > 7)
                    check_provider();
                else{
                    operator_available_layout.setVisibility(View.VISIBLE);
                    choose_packet_layout.setVisibility(View.GONE);
                    /**
                     * @author : Jesslyn
                     * @note : set image to invisible instead of gone
                     */
                    // <code>
                    operator.setVisibility(View.INVISIBLE);
                    // </code>
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_prepaid_mobile_credit_input_phone_no);

        init_toolbar(getResources().getString(R.string.home_mobile_credit));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // @CFI
        phone_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onNext();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_contact();
            }
        });
    }

    /**
     * @author Jesslyn
     * @note set function onNext
     */
    // <code>
    private void onNext(){
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,2000);
    }
    // </code>

    private void init() throws JSONException {
        choose_packet_layout = findViewById(R.id.choose_packet_layout);
        operator_available_layout = findViewById(R.id.operator_available_layout);
        phone_no = findViewById(R.id.phone_no);
        layout = findViewById(R.id.layout);
        list = findViewById(R.id.list);

        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listCategory = findViewById(R.id.list_category);
        listCategory.setLayoutManager(layoutManager);

        final LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(listCategory);

        listCategory.setOnFlingListener(snapHelper);

        contact = findViewById(R.id.contact);
        operator = findViewById(R.id.operator);

        /**
         * @author Jesslyn
         * @note add shimmer lib
         */
        // <code>
        shimmerInit();
        // </code>

        /**
         * @author Jesslyn
         * @note init custom keyboard
         */
        // <code>
        initNumpadKeyboard(phone_no, new NumpadKeyboardSubmit() {
            @Override
            public void onSubmit() {
                onNext();
            }
        });
        // </code>

        // @CFI
        get_provider_data();
        manage_tab_list();

        catAdapter = new PrepaidDataInputPhoneNoCategoryAdapter(arrCat, this);
        listCategory.setAdapter(catAdapter);

        catAdapter.setOnItemClickListener(new PrepaidDataInputPhoneNoCategoryAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try{
                    if(position > 0 && catAdapter.getPaddingPixel() == 0){
                        int paddingDp = 150;
                        float density = getResources().getDisplayMetrics().density;
                        int paddingPixel = (int)(paddingDp * density);
                        listCategory.setPaddingRelative(paddingPixel,0,paddingPixel,0);
                        catAdapter.setPaddingPixel(paddingPixel);
                    }else if(position == 0){
                        int paddingDp = 0;
                        float density = getResources().getDisplayMetrics().density;
                        int paddingPixel = (int)(paddingDp * density);
                        listCategory.setPaddingRelative(paddingPixel,0,paddingPixel,0);
                        catAdapter.setPaddingPixel(paddingPixel);
                    }
                    listCategory.smoothScrollToPosition(position);

                    arrCat.get(catAdapter.curSelected).put("isSelected", false);
                    arrCat.get(position).put("isSelected", true);

                    selected_tab = arrCat.get(position).getString("text");
                    manage_tab_list();
                }catch (JSONException e){
                    e.printStackTrace();
                }
                catAdapter.notifyDataSetChanged();
            }
        });
    }

    private void manage_tab_list(){
        try {
            selectedCat = new ArrayList<>();
            if(arrProd.has(selected_tab))
                selectedCat = (ArrayList<JSONObject>) arrProd.get(selected_tab);

            adapter = new PrepaidDataInputPhoneNoAdapter(selectedCat,this);
            list.setAdapter(adapter);
        }catch(JSONException e){
            e.printStackTrace();
        }

        layout_manager = new GridLayoutManager(this,2);
        list.setLayoutManager(layout_manager);

        adapter.setOnItemClickListener(new PrepaidDataInputPhoneNoAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    data_selected = selectedCat.get(position);
                    if(selected_tab.equalsIgnoreCase("NGRS")){
                        if(data_selected.getInt("isProblem") == 1)
                            show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                        else
                            get_detail_ngrs(position);
                    }else{
                        if(data_selected.getInt("isProblem") == 1)
                            show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                        else {
                            PrepaidMobileCreditInputPhoneNoModal modal = new PrepaidMobileCreditInputPhoneNoModal();
                            modal.show(getSupportFragmentManager(), "modal");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void check_provider() throws JSONException {
        selected_provider = new JSONObject();
        String input = phone_no.getText().toString();
        if(input.charAt(0) != '0')
            input = "0"+phone_no.getText().toString();

        for(int x = 0; x < provider_array.size(); x++){
            boolean flag = false;
            String[] prefix_array = provider_array.get(x).getString("prefixProduct").split(",");
            for(int y = 0; y < prefix_array.length; y++){
                if(input.length() >= prefix_array[y].length() && check_phone_no(prefix_array[y],input.substring(0,
                        input.length() < prefix_array[y].length() ? input.length() : prefix_array[y].length())
                )) {
                    selected_provider = provider_array.get(x);
                    flag = true;
                    break;
                }
            }

            if(flag)
                break;
        }

        if(!selected_provider.toString().equals("{}")) {
            if(selected_provider.getInt("isProblem") == 0) {
                choose_packet_layout.setVisibility(View.VISIBLE);
                operator_available_layout.setVisibility(View.GONE);
                operator.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(selected_provider.getString("image"))
                        .into(operator);

                get_package_data();
            }
            else
                show_error_message(layout, getResources().getString(R.string.provider_trouble_click_label));
        }
        else {
            operator_available_layout.setVisibility(View.VISIBLE);
            choose_packet_layout.setVisibility(View.GONE);
            /**
             * @author : Jesslyn
             * @note : set image to invisible instead of gone
             */
            // <code>
            operator.setVisibility(View.INVISIBLE);
            // </code>
        }
    }

    private boolean check_phone_no(String prefix, String phone_no) {
        for (int x = 0; x < prefix.length(); x++) {
            if (prefix.charAt(x) != phone_no.charAt(x))
                return false;
        }
        return true;
    }

    private void get_provider_data() {
        url = BASE_URL+"/mobile/prepaid-pulsa/show-operator-prefix";

        JSONObject param = getBaseAuth();
        try{
            param.put("productType", "PULSA");
        }catch(Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray jsonArray = response_data.getJSONArray("arrPulsaPrefix");
                        for(int x = 0; x < jsonArray.length(); x++)
                            provider_array.add(jsonArray.getJSONObject(x));

                        String noHp = getIntent().getStringExtra("no_hp");
                        if(noHp != null) phone_no.setText(noHp);
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

    private void get_package_data() throws JSONException {
        shimmerStart();
        String phone = getPhoneNo(phone_no);

        url = BASE_URL+"/mobile/prepaid/pulsa-pricelist";

        JSONObject param = getBaseAuth();
        try{
            param.put("productType", "PULSA");
            param.put("noHp", phone);
            param.put("idProduct", selected_provider.getString("idProduct") );
        }catch(Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        arrCat.clear();
                        data_list = response_data;

                        JSONObject resp = response_data.getJSONObject("arrPulsaPriceRslt");
                        Iterator<String> keys = resp.keys();

                        while(keys.hasNext()) {
                            ArrayList<JSONObject> temp = new ArrayList<>();
                            String key = keys.next();
                            if (resp.getJSONArray(key) instanceof JSONArray) {
                                JSONArray arr = resp.getJSONArray(key);

                                for (int j = 0; j < arr.length(); j++) {
                                    JSONObject jsonObject = arr.getJSONObject(j);

                                    /**
                                     * @author : Jesslyn
                                     * @note : 1. change status information
                                     *         2. add name, change detail
                                     */
                                    // <code>
                                    jsonObject.put("status",jsonObject.getInt("isProblem") == 0 ? "Tersedia" : "Gangguan");
                                    jsonObject.put("name",jsonObject.getString("namePulsa"));
                                    jsonObject.put("detail",jsonObject.getString("detailPulsa"));
                                    // </code>
                                    jsonObject.put("price",jsonObject.getDouble("vendorAmount"));

                                    /**
                                     * @author Jesslyn
                                     * @note req 14, change price_str using amount
                                     */
                                    // <code>
                                    jsonObject.put("price_str","Rp. "+formatter.format(jsonObject.getDouble("amount")));
                                    // add price display for displaying first price attempt (NGRS)
                                    jsonObject.put("price_display","Rp. "+formatter.format(jsonObject.getDouble("amount")));
                                    // </code>
                                    jsonObject.put("image_url",response_data.getString("image"));
                                    jsonObject.put("productType",response_data.getString("productType"));
                                    jsonObject.put("productCategory",response_data.getString("productCategory"));
                                    jsonObject.put("idProduct",response_data.getString("idProduct"));

                                    temp.add(jsonObject);
                                }

                                // @note maintain tab list and selected tab (first attempt)
                                JSONObject obj = new JSONObject();
                                obj.put("text", key);
                                obj.put("isSelected", arrCat.size() == 0);

                                // @note set selected data to first attempt
                                if(arrCat.size() == 0)
                                    selected_tab = key;

                                arrCat.add(obj);
                                arrProd.put(key, temp);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        catAdapter.notifyDataSetChanged();

                        // @note : display data
                        manage_tab_list();
                        // </code>
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    shimmerStop();
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
                }finally {
                    shimmerStop();
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

    private void get_detail_ngrs(final int index_ngrs) throws JSONException {
        /**
         * @author : Jesslyn
         * @note : 1. add vendorAmount to param request
         *         2. fixing phone number not updated (get phone number directly from phone_no edit text
         *         3. change request to post and use JSONObject
         */
        // <code>
        String noHp = getPhoneNo(phone_no);
        url = BASE_URL+"/mobile/prepaid/pulsa-ngrs-check";

        JSONObject param = getBaseAuth();
        try{
            param.put("noHp", noHp);
            param.put("idProduct", data_list.getString("idProduct") );
            param.put("cdeProduct", data_list.getString("cdeProduct") );
            param.put("nameProduct", data_list.getString("nameProduct") );
            param.put("productType", data_list.getString("productType") );
            param.put("productCategory", data_list.getString("productCategory") );
            param.put("idProductDetail", selectedCat.get(index_ngrs).getString("idProductDetail") );
            param.put("codePulsa", selectedCat.get(index_ngrs).getString("codePulsa") );
            param.put("namePulsa", selectedCat.get(index_ngrs).getString("namePulsa") );
            param.put("typePulsa", selectedCat.get(index_ngrs).getString("typePulsa") );
            param.put("codeCheckPulsa", selectedCat.get(index_ngrs).getString("codeCheckPulsa") );
            param.put("vendorAmount", selectedCat.get(index_ngrs).getString("vendorAmount") );
            param.put("amount", selectedCat.get(index_ngrs).getString("amount") );
        }catch(Exception e){
            e.printStackTrace();
        }
        // </code>
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONObject jsonObject = selectedCat.get(index_ngrs);
                        /**
                         * @author : Jesslyn
                         * @note : 1. Fixing issue price always 0
                         *         2. change to vendorAmount (to make standarization code)
                         */
                        // <code>
                        jsonObject.put("price",response_data.getDouble("vendorAmount"));
                        jsonObject.put("price_str","Rp. "+formatter.format(response_data.getDouble("vendorAmount")));
                        // </code>

                        /**
                         * @author Dinda
                         * @note Case CICD 10145 - replace amount and vendorAmount with data from NGRS check
                         */
                        // <code>
                        jsonObject.put("vendorAmount", response_data.getInt("vendorAmount"));
                        jsonObject.put("amount", response_data.getInt("amount"));
                        // </code>

                        /**
                         * @author : Jesslyn
                         * @note : disable notify dataset change
                         */
                        // <code>
                        // adapter.notifyItemChanged(index_ngrs);
                        // </code>

                        data_selected = jsonObject;

                        PrepaidMobileCreditInputPhoneNoModal modal = new PrepaidMobileCreditInputPhoneNoModal();
                        modal.show(getSupportFragmentManager(),"modal");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CONTACT_INTENT){
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();

                    for(String column:c.getColumnNames()) {
                        if(c.getString(c.getColumnIndex(column)) != null && column.equals("data4"))
                        /**
                         * @author Jesslyn
                         * @note Fixing issue +62
                         */
                            // <code>
                            phone_no.setText(parsePhoneNo(c.getString(c.getColumnIndex(column))));
                        // </code>
                    }
                }
            }
        }
    }
}