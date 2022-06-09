package com.qdi.rajapay.agency.dashboard_super;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.dashboard_super.agent.DashboardSuperAgentFragment;
import com.qdi.rajapay.agency.dashboard_super.bonus.DashboardSuperBonusFragment;
import com.qdi.rajapay.agency.dashboard_super.commition.DashboardSuperCommitionFragment;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @module 7.0 Keagenan
 * @screen 7.4.1
 */
public class DashboardSuperIndexActivity extends BaseActivity {
    ViewPager view_pager;
    TabLayout tabbar;

    ViewPagerAdapter viewPagerAdapter;
    Fragment selected_fragment;

    public ArrayList<JSONObject> array_commition = new ArrayList<>(),
            array_bonus = new ArrayList<>(),
            array_agent = new ArrayList<>();
    public Double total_commition_data = 10000d,
            total_bonus_data = 10000d,
            total_agent_data = 10000d,
            total_commition_referral_data = 10000d,
            total_commition_premium_data = 10000d,
            total_agen_referral_data = 10000d,
            total_agen_premium_data = 10000d;

    public JSONArray arr_commition_referral = new JSONArray(), arr_commition_transaction = new JSONArray(),
            arr_agent_referral = new JSONArray(), arr_agent_transaction = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_super_index);

        init_toolbar(getResources().getString(R.string.agency_dashboard_premium));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException, ParseException {
        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);

        prepare_agent_data();
        prepare_commition_data();
        prepare_bonus_data();

        get_data();
    }

    private void prepare_commition_data() throws JSONException {
        array_commition.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_referral));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", "Rp. "+formatter.format(50));
        array_commition.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_transaction));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", "Rp. "+formatter.format(50));
        array_commition.add(jsonObject);
    }

    private void prepare_bonus_data() throws JSONException {
        array_bonus.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_referral));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", "Rp. "+formatter.format(50));
        array_bonus.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_transaction));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", "Rp. "+formatter.format(50));
        array_bonus.add(jsonObject);
    }

    private void prepare_agent_data() throws JSONException {
        array_agent.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_agent_referral));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", formatter.format(50)+" "+getResources().getString(R.string.agency_dashboard_agent));
        array_agent.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_agent_transaction));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", formatter.format(50)+" "+getResources().getString(R.string.agency_dashboard_agent));
        array_agent.add(jsonObject);
    }

    private void manage_view() throws JSONException {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DashboardSuperCommitionFragment(),getResources().getString(R.string.agency_dashboard_commition));
        viewPagerAdapter.addFragment(new DashboardSuperAgentFragment(),getResources().getString(R.string.agency_dashboard_agent));
        viewPagerAdapter.addFragment(new DashboardSuperBonusFragment(),getResources().getString(R.string.agency_dashboard_bonus));

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        selected_fragment = new DashboardSuperCommitionFragment();
    }

    private void get_data() {
        url = BASE_URL+"/mobile/agen/dashboardsuper";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        for(int x = 0; x < array_commition.size(); x++){
                            if(x == 0)
                                array_commition.get(x).put("price",response_data.getDouble("totComReferral"));
                            else if(x == 1)
                                array_commition.get(x).put("price",response_data.getDouble("totComTran"));
                            array_commition.get(x).put("price_str", "Rp. "+formatter.format(array_commition.get(x).getDouble("price")));
                        }

                        for(int x = 0; x < array_bonus.size(); x++){
                            if(x == 0)
                                array_bonus.get(x).put("price",0d);
                            else if(x == 1)
                                array_bonus.get(x).put("price",0d);
                            array_bonus.get(x).put("price_str", "Rp. "+formatter.format(array_bonus.get(x).getDouble("price")));
                        }

                        for(int x = 0; x < array_agent.size(); x++){
                            if(x == 0)
                                array_agent.get(x).put("price",response_data.getDouble("totAgenReferral"));
                            else if(x == 1)
                                array_agent.get(x).put("price",response_data.getDouble("totAgenPremium"));
                            array_agent.get(x).put("price_str", formatter.format(array_agent.get(x).getDouble("price"))+" "+getResources().getString(R.string.agency_dashboard_agent));
                        }

                        for(int x = 0; x < response_data.getJSONArray("levelBonus").length(); x++){
                            if(response_data.getJSONArray("levelBonus").getJSONObject(x).getInt("amountToReach") > 0 ||
                                    x == response_data.getJSONArray("levelBonus").length() - 1){
                                total_bonus_data = response_data.getJSONArray("levelBonus").getJSONObject(x).getDouble("amountToReach");
                                break;
                            }
                        }

                        arr_commition_referral = response_data.getJSONArray("arrComReferral");
                        arr_commition_transaction = response_data.getJSONArray("arrComAgen");
                        arr_agent_referral = response_data.getJSONArray("arrAgenReferral");
                        arr_agent_transaction = response_data.getJSONArray("arrAgenPremium");

                        total_commition_data = response_data.getDouble("totComAll");
                        total_agent_data = response_data.getDouble("totAgenAll");
                        total_commition_premium_data = response_data.getDouble("totComTran");
                        total_commition_referral_data = response_data.getDouble("totComReferral");
                        total_agen_premium_data = response_data.getDouble("totAgenPremium");
                        total_agen_referral_data = response_data.getDouble("totAgenReferral");

                        manage_arr_commition_ref();
                        manage_view();
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

    private void manage_arr_commition_ref() throws JSONException {
        Boolean flag = true;
        while(flag) {
            int counter = 0;
            for (int x = 0; x < arr_commition_referral.length(); x++) {
                if(arr_commition_referral.getJSONObject(x).getDouble("totTransaction") == 0){
                    arr_commition_referral.remove(x);
                    break;
                }
                counter++;
            }
            if(counter == arr_commition_referral.length())
                flag = false;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clear_fragment(){
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}