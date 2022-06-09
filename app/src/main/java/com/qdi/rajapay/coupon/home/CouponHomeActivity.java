package com.qdi.rajapay.coupon.home;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.google.android.material.tabs.TabLayout;
import com.qdi.rajapay.interfaces.OnRefreshData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @module 4.21 Kupon
 * @screen 4.21.1
 */
public class CouponHomeActivity extends BaseActivity implements OnRefreshData {
    ViewPager view_pager;
    TabLayout tabbar;

    ViewPagerAdapter viewPagerAdapter;

    ArrayList<JSONObject> regulerCouponData = new ArrayList<>();
    ArrayList<JSONObject> saleCouponData = new ArrayList<>();

    CouponBuyFragment couponBuyFragment = new CouponBuyFragment();
    CouponListFragment couponListFragment = new CouponListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_home_activity);

        init_toolbar(getResources().getString(R.string.coupon_label));
        init();
    }

    private void init(){
        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(couponBuyFragment,getResources().getString(R.string.buy_coupon));
        viewPagerAdapter.addFragment(couponListFragment,getResources().getString(R.string.my_coupon));

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        try{
            get_data();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void get_data() throws JSONException {
        url = BASE_URL+"/mobile/coupon/all";
        Log.d("url",url);

        show_wait_modal();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    regulerCouponData.clear();
                    saleCouponData.clear();

                    if (!response_data.getString("type").equals("Failed")) {
                        if(response_data.has("regulerCouponData")){
                            for(int x = 0; x < response_data.getJSONArray("regulerCouponData").length(); x++){
                                SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy");
                                JSONObject data_coupon = response_data.getJSONArray("regulerCouponData").getJSONObject(x);

                                JSONObject jsonObject = data_coupon;
                                jsonObject.put("title", !data_coupon.isNull("title") ?
                                        data_coupon.getString("title") : getResources().getString(R.string.coupon_default_title));
                                jsonObject.put("expired_date", getResources().getString(R.string.valid_until_label)+"\n"+
                                        format_date.format(parse_date.parse(data_coupon.getString("expCoupon"))));
                                jsonObject.put("image_url", data_coupon.getString("imgCoupon"));
                                jsonObject.put("button_action_title", "LIHAT DETAIL");

                                regulerCouponData.add(jsonObject);
                            }
                        }

                        if(response_data.has("saleCouponData")){
                            for(int x = 0; x < response_data.getJSONArray("saleCouponData").length(); x++){
                                SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy");
                                JSONObject data_coupon = response_data.getJSONArray("saleCouponData").getJSONObject(x);

                                JSONObject jsonObject = data_coupon;
                                jsonObject.put("title", !data_coupon.isNull("title") ?
                                        data_coupon.getString("title") : getResources().getString(R.string.coupon_default_title));
                                jsonObject.put("expired_date", getResources().getString(R.string.valid_until_label)+"\n"+
                                        format_date.format(parse_date.parse(data_coupon.getString("expCoupon"))));
                                jsonObject.put("image_url", data_coupon.getString("imgCoupon"));
                                jsonObject.put("button_action_title", "LIHAT DETAIL");

                                saleCouponData.add(jsonObject);
                            }
                        }

                        if(regulerCouponData.size() == 0 && saleCouponData.size() == 0){
                            displaySnackBar(getStr(R.string.f_no_coupon));
                        }
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }

                    couponListFragment.setData(regulerCouponData);
                    couponBuyFragment.setData(saleCouponData);
                } catch (JSONException | ParseException e) {
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
    public void refreshData() {
        try{
            couponBuyFragment.hideLoading();
            couponListFragment.hideLoading();
            get_data();
        }catch(JSONException e){
            e.printStackTrace();
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