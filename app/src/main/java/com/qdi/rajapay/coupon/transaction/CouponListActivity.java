package com.qdi.rajapay.coupon.transaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.coupon.home.CouponBuyFragment;
import com.qdi.rajapay.coupon.home.CouponHomeActivity;
import com.qdi.rajapay.coupon.home.CouponListFragment;
import com.qdi.rajapay.interfaces.OnRefreshData;
import com.qdi.rajapay.payment.ChoosePaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * @screen 4.10.3
 * @screen 4.11.4
 * @screen 4.12.4
 * @screen 4.13.4
 * @screen 4.14.4
 * @screen 4.15.4
 * @screen 4.16.4
 * @screen 4.17.5
 * @screen 4.18.5
 * @screen 4.19.5
 * @screen 4.20.5
 */
public class CouponListActivity extends BaseActivity implements OnRefreshData {
    Button use;

    String coupon = "";
    JSONObject data;

    Double clean_total = 0d, admin_fee = 0d;
    boolean fromIntent = true;

    ViewPager view_pager;
    TabLayout tabbar;

    ViewPagerAdapter viewPagerAdapter;

    ArrayList<JSONObject> regulerCouponData = new ArrayList<>();
    ArrayList<JSONObject> saleCouponData = new ArrayList<>();

    CouponBuyFragment couponBuyFragment = new CouponBuyFragment(false, true);
    CouponListFragment couponListFragment = new CouponListFragment(false, true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_coupon);

        init_toolbar(getResources().getString(R.string.activity_title_coupon_code));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCouponCodeModal modal = new AddCouponCodeModal();
                modal.show(getSupportFragmentManager(),"modal");
            }
        });
    }

    private void init() throws JSONException {
        use = findViewById(R.id.use);

        clean_total = getIntent().getDoubleExtra("total",0d);
        admin_fee = getIntent().getDoubleExtra("admin_fee",0d);

        if(getIntent().hasExtra("data")) {
            data = new JSONObject(getIntent().getStringExtra("data"));
            get_data();
        }

        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(couponBuyFragment,getResources().getString(R.string.my_coupon));
        viewPagerAdapter.addFragment(couponListFragment,getResources().getString(R.string.coupon_available));

        couponBuyFragment.setClean_total(clean_total);
        couponListFragment.setClean_total(clean_total);

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        displaySnackBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataResponse) {
        super.onActivityResult(requestCode, resultCode, dataResponse);

        if (resultCode == RESULT_OK) {
            /*@see https://stackoverflow.com/a/42702301/1533670 */
            int unmaskedRequestCode = requestCode & 0x0000ffff;

            if(unmaskedRequestCode == ChoosePaymentActivity.REQUEST_COUPON){
                setResult(Activity.RESULT_OK,new Intent()
                        .putExtra("data", dataResponse.getStringExtra("data") ));
                finish();
            }
        }
    }

    private void displaySnackBar(){
        Snackbar snackbar = Snackbar
                .make(layout, "Punya kode kupon sendiri?", Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                .setDuration(6000)
                .setAction("PAKAI KUPON", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddCouponCodeModal modal = new AddCouponCodeModal();
                        modal.show(getSupportFragmentManager(),"modal");
                    }
                });

        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTypeface(null, Typeface.BOLD);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));

        snackbar.show();
    }

    @Override
    public void refreshData() {
        try {
            // refresh coupon code
            coupon = "";
            displaySnackBar();
            get_data();
            couponBuyFragment.hideLoading();
            couponListFragment.hideLoading();
        } catch (JSONException e) {
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

    /**
     * @author Liao Mei
     * @note Calculate / Refresh discount price
     * @param jsonObject
     * @param data_coupon
     * @param clean_total
     * @throws JSONException
     */
    // <code>
    public static void calculateDiscount(JSONObject jsonObject, JSONObject data_coupon, double clean_total, double admin_fee) throws JSONException {
        if(data_coupon.getString("indAmount").equals("FX"))
            jsonObject.put("price", data_coupon.getDouble("amount") * -1);
        else if(data_coupon.getString("indAmount").equals("PC")){
            if(data_coupon.has("discount_type") && data_coupon.getString("discount_type").equalsIgnoreCase("ADM"))
                // ADM => possible admin fee = 0, but don't worry, bellow calculation makes price = 0 if admin fee 0
                jsonObject.put("price", (data_coupon.getDouble("amount") / 100) * admin_fee * -1);
            else
                // NULL = total invoice
                jsonObject.put("price", (data_coupon.getDouble("amount") / 100) * clean_total * -1);
        }

        // check if discount more than max amount, set discount / price to maxAmount
        if(jsonObject.getDouble("maxAmount") > 0 && ((jsonObject.getDouble("price") * -1) > jsonObject.getDouble("maxAmount")))
            jsonObject.put("price",jsonObject.getDouble("maxAmount") * -1);

        // check if discount adm more than admin fee, set price = admin fee (to prevent discount_type adm deduct origin price / clean total)
        if(data_coupon.has("discount_type") && data_coupon.getString("discount_type").equalsIgnoreCase("ADM") && (jsonObject.getDouble("price") * -1 > admin_fee)){
            jsonObject.put("price", admin_fee * -1);
        }

        // check if discount more than price, set to price (preventive action to prevent minus if discount price is more than clean total
        if((jsonObject.getDouble("price") * -1) > clean_total)
            jsonObject.put("price", clean_total * -1);

        /**
         * @author Jesslyn
         * @note Case BDD 11544 - Price cause error when submit coupon amount. amount -> {Double@7034} 7550.700000000001
         */
        // <code>
        jsonObject.put("price", Math.round(jsonObject.getDouble("price")));
        // </code>
    }
    // </code>

    public void get_data() throws JSONException {
        show_wait_modal();

        url = BASE_URL+"/mobile/coupon/detail";
        Log.d("url",url);

        JSONObject param = getBaseAuth();
        param.put("cdeCoupon", coupon);
        param.put("categoryCoupon", data.getString("categoryCoupon"));
        param.put("typeCoupon", data.getString("typeCoupon"));
        param.put("idProduct", data.getString("idProduct"));
        if(data.has("idProductDetail"))
            param.put("idProductDetail", data.getInt("idProductDetail"));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    regulerCouponData.clear();
                    saleCouponData.clear();

                    if (!response_data.getString("type").equals("Failed")) {
                        if(response_data.has("arrCouponList")){
                            for(int x = 0; x < response_data.getJSONArray("arrCouponList").length(); x++){
                                SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy");
                                JSONObject data_coupon = response_data.getJSONArray("arrCouponList").getJSONObject(x);

                                JSONObject jsonObject = data_coupon;
                                jsonObject.put("title", !data_coupon.isNull("title") ?
                                        data_coupon.getString("title") : getResources().getString(R.string.coupon_default_title));
                                jsonObject.put("expired_date", getResources().getString(R.string.valid_until_label)+"\n"+
                                        format_date.format(parse_date.parse(data_coupon.getString("expCoupon"))));
                                jsonObject.put("image_url", data_coupon.getString("imgCoupon"));
                                jsonObject.put("categoryCoupon", response_data.getString("categoryCoupon"));
                                jsonObject.put("typeCoupon", response_data.getString("typeCoupon"));
                                jsonObject.put("button_action_title", "LIHAT DETAIL");

                                calculateDiscount(jsonObject, data_coupon, clean_total, admin_fee);
                                regulerCouponData.add(jsonObject);
                            }
                        }

                        if(response_data.has("arrCouponUserList")){
                            for(int x = 0; x < response_data.getJSONArray("arrCouponUserList").length(); x++){
                                SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy");
                                JSONObject data_coupon = response_data.getJSONArray("arrCouponUserList").getJSONObject(x);

                                JSONObject jsonObject = data_coupon;
                                jsonObject.put("title", !data_coupon.isNull("title") ?
                                        data_coupon.getString("title") : getResources().getString(R.string.coupon_default_title));
                                jsonObject.put("expired_date", getResources().getString(R.string.valid_until_label)+"\n"+
                                        format_date.format(parse_date.parse(data_coupon.getString("expCoupon"))));
                                jsonObject.put("image_url", data_coupon.getString("imgCoupon"));
                                jsonObject.put("categoryCoupon", response_data.getString("categoryCoupon"));
                                jsonObject.put("typeCoupon", response_data.getString("typeCoupon"));
                                jsonObject.put("button_action_title", "LIHAT DETAIL");

                                calculateDiscount(jsonObject, data_coupon, clean_total, admin_fee);
                                saleCouponData.add(jsonObject);
                            }
                        }

                        if(!fromIntent && regulerCouponData.size() == 0 && saleCouponData.size() == 0){
                            displaySnackBar(getStr(R.string.f_no_coupon));
                        }

                        if(!coupon.isEmpty()){
                            int totalCoupon = regulerCouponData.size() + saleCouponData.size();
                            displaySnackBar(totalCoupon + " Kupon ditemukan");
                        }
                    } else {
                        if(!fromIntent)
                            show_error_message(layout,response_data.getString("message"));
                    }

                    couponListFragment.setData(regulerCouponData);
                    couponBuyFragment.setData(saleCouponData);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                } finally {
                    fromIntent = false;
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
}