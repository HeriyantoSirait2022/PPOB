package com.qdi.rajapay.main_menu.games;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoCategoryAdapter;
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoModal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @module 4.22 Games
 * @screen 4.22.1
 */
public class PrepaidGamesSelectActivity extends BaseActivity implements PrepaidGamesSelectAdapter.UpdateListener {
    EditText gameNames;
    RecyclerView list, listCategory;
    CoordinatorLayout layout;
    View hasDataLayout, noDataLayout;

    PrepaidGamesSelectAdapter adapter;
    PrepaidDataInputPhoneNoCategoryAdapter catAdapter;
    RecyclerView.LayoutManager layout_manager;

    ArrayList<JSONObject> arrCat = new ArrayList<>(), selectedCat = new ArrayList<>();
    JSONObject arrProd = new JSONObject();

    String selected_tab = "TOPUP";
    ArrayList<JSONObject> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_prepaid_games_select);

        init_toolbar(getResources().getString(R.string.main_menu_prepaid_games_header));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gameNames.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onNext();
                    return true;
                }
                return false;
            }
        });

        gameNames.addTextChangedListener(new TextWatcher() {
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
    }

    private void onNext(){
        handler.removeCallbacks(runnable);
        shimmerStart();
        handler.postDelayed(runnable,1000);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            adapter.filter(gameNames.getText().toString());
            shimmerStop();
            hideSoftKeyboard(PrepaidGamesSelectActivity.this);
        }
    };

    private void init() throws JSONException {
        gameNames = findViewById(R.id.game_names);
        layout = findViewById(R.id.layout);
        list = findViewById(R.id.list);

        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listCategory = findViewById(R.id.list_category);
        listCategory.setLayoutManager(layoutManager);

        final LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(listCategory);

        listCategory.setOnFlingListener(snapHelper);

        shimmerInit();
        getGamesList();

        adapter = new PrepaidGamesSelectAdapter(array,this);
        layout_manager = new GridLayoutManager(this,3);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        noDataLayout = findViewById(R.id.empty_data_layout);
        hasDataLayout = findViewById(R.id.has_data_layout);

        catAdapter = new PrepaidDataInputPhoneNoCategoryAdapter(arrCat, this);
        listCategory.setAdapter(catAdapter);

        catAdapter.setOnItemClickListener(new PrepaidDataInputPhoneNoCategoryAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try{
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

            adapter = new PrepaidGamesSelectAdapter(selectedCat,this);
            list.setAdapter(adapter);

            adapter.filter(gameNames.getText().toString());
        }catch(JSONException e){
            e.printStackTrace();
        }

        layout_manager = new GridLayoutManager(this,3);
        list.setLayoutManager(layout_manager);

        adapter.setOnItemClickListener(new PrepaidGamesSelectAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                JSONObject jsonObject = selectedCat.get(position);
                try {
                    if(jsonObject.getInt("isProblem") == 1)
                        show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                    else
                        startActivity(
                                new Intent(PrepaidGamesSelectActivity.this, PrepaidGamesInputIdActivity.class)
                                        .putExtra("selected_product", jsonObject.toString())

                        );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getGamesList() throws JSONException {
        shimmerStart();

        url = BASE_URL+"/mobile/prepaid/games-list";
        JSONObject param = getBaseAuth();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        arrCat.clear();

                        JSONObject resp = response_data.getJSONObject("gamesList");
                        Iterator<String> keys = resp.keys();

                        while(keys.hasNext()) {
                            ArrayList<JSONObject> temp = new ArrayList<>();
                            String key = keys.next();
                            if (resp.getJSONArray(key) instanceof JSONArray) {
                                JSONArray arr = resp.getJSONArray(key);

                                for(int j = 0; j < arr.length(); j++){
                                    temp.add( arr.getJSONObject(j));
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
                } finally {
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

    @Override
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