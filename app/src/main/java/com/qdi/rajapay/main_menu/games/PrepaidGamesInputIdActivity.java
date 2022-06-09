package com.qdi.rajapay.main_menu.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoAdapter;
import com.qdi.rajapay.model.enums.ProductType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @module 4.22 Games
 * @screen 4.22.2
 */
public class PrepaidGamesInputIdActivity extends BaseActivity {
    LinearLayout choose_packet_layout, operator_available_layout;
    EditText idGame;
    RecyclerView list, listCategory;
    RecyclerView.LayoutManager layout_manager;
    View hasDataLayout, noDataLayout;
    CardView inputIdGameLayout;

    CoordinatorLayout layout;
    TextView gameNames;

    PrepaidDataInputPhoneNoAdapter adapter;
    ArrayList<JSONObject> arrProd = new ArrayList<>();
    JSONObject selectedProduct, selectedDetail;
    ProductType productType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_prepaid_games_input_id);

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        choose_packet_layout = findViewById(R.id.choose_packet_layout);
        operator_available_layout = findViewById(R.id.operator_available_layout);
        idGame = findViewById(R.id.id_game);
        layout = findViewById(R.id.layout);
        list = findViewById(R.id.list);
        gameNames = findViewById(R.id.game_names);
        inputIdGameLayout = findViewById(R.id.input_id_games_layout);

        adapter = new PrepaidDataInputPhoneNoAdapter(arrProd,PrepaidGamesInputIdActivity.this);
        list.setAdapter(adapter);
        layout_manager = new GridLayoutManager(this,2);
        list.setLayoutManager(layout_manager);

        adapter.setOnItemClickListener(position -> {
            try {
                selectedDetail = arrProd.get(position);
                if(selectedDetail.getInt("isProblem") == 1)
                    show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                else {
                    if(productType == ProductType.TOPUPGAMES && idGame.getText().toString().isEmpty()){
                        show_error_message(layout,getResources().getString(R.string.main_menu_prepaid_input_id_error_input));
                    }else{
                        PrepaidGamesDetailModal modal = new PrepaidGamesDetailModal();
                        modal.show(getSupportFragmentManager(), "modal");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        Intent intent = getIntent();

        if(intent.hasExtra("selected_product")){
            try {
                selectedProduct = new JSONObject(Objects.requireNonNull(intent.getStringExtra("selected_product")));
                productType = ProductType.fromStrToProductType(selectedProduct.getString("productType"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(productType == ProductType.TOPUPGAMES){
                init_toolbar(getResources().getString(R.string.main_menu_prepaid_input_id_title_topup));
                inputIdGameLayout.setVisibility(View.VISIBLE);
            }else if(productType == ProductType.VOUCHERGAMES){
                init_toolbar(getResources().getString(R.string.main_menu_prepaid_input_id_title_voucher));
                inputIdGameLayout.setVisibility(View.GONE);
            }else{
                init_toolbar(getResources().getString(R.string.main_menu_prepaid_games_header));
            }

            shimmerInit();
            getGamesDetail();

            gameNames.setText(selectedProduct.getString("productName"));
        }

        // hide list category because its unused on this screen
        listCategory = findViewById(R.id.list_category);
        listCategory.setVisibility(View.GONE);

        noDataLayout = findViewById(R.id.empty_data_layout);
        hasDataLayout = findViewById(R.id.has_data_layout);
    }

    private void getGamesDetail(){
        shimmerStart();

        url = BASE_URL+"/mobile/prepaid/games-list";
        JSONObject param = getBaseAuth();

        try{
            param.put("idProduct", selectedProduct.getInt("productId"));
        }catch(JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {

                        JSONObject resp = response_data.getJSONObject("gamesPriceList");
                        Iterator<String> keys = resp.keys();

                        while(keys.hasNext()) {
                            String key = keys.next();
                            resp.getJSONArray(key);
                            JSONArray arr = resp.getJSONArray(key);

                            for(int j = 0; j < arr.length(); j++){
                                JSONObject jsonObject = arr.getJSONObject(j);
                                jsonObject.put("name",jsonObject.getString("detailGames").isEmpty() ? "Tidak ada deskripsi" : jsonObject.getString("detailGames"));
                                jsonObject.put("status",jsonObject.getInt("isProblem") == 0 ? "Tersedia" : "Gangguan");
                                jsonObject.put("price_display","Rp. "+formatter.format(jsonObject.getDouble("totalPrice")));
                                jsonObject.put("price_str","Rp. "+formatter.format(jsonObject.getDouble("totalPrice")));
                                jsonObject.put("image_url", selectedProduct.getString("image"));
                                arrProd.add(jsonObject);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                         show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    shimmerStop();
                    onUpdate(arrProd.isEmpty());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    error_handling(error, layout);
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                } finally {
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