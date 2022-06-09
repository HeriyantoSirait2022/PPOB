package com.qdi.rajapay.coupon.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.google.android.material.tabs.TabLayout;
import com.qdi.rajapay.coupon.transaction.AddCouponConfirmationModal;
import com.qdi.rajapay.payment.ChoosePaymentActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.21 Kupon
 * @screen 4.21.2
 */
public class CouponDetailActivity extends BaseActivity {
    ViewPager view_pager;
    TabLayout tabbar;
    MyPagerAdapter adapter;
    public JSONObject data;

    ImageView image;
    TextView expired, title, amount, qtyLeft;
    boolean buyOptions = false;
    boolean useOptions = false;
    Double clean_total = 0d;
    Button buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        init_toolbar(getResources().getString(R.string.coupon_detail_title));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buyOptions){
                    ArrayList<JSONObject> arr = new ArrayList<>();
                    JSONObject post = new JSONObject();
                    try{
                        arr.add(new JSONObject("{\"title\":\""+"Kupon Rajapay"+"\",\"price\":"+ data.getDouble("sellingPrice")+",\"detail\":\"" + data.getString("cdeCoupon") + "\"}"));
                        arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label) + "\",\"price\":"+ data.getDouble("adminFee")+"}"));
                        post.put("breakdown_price",new JSONArray(arr.toString()));

                        post.put("url_post","/mobile/prepaid/coupon-transaction");
                        post.put("url_pay","/mobile/prepaid/topup-coupon");
                        post.put("data", data);
                        post.put("data_post",add_data_to_post());

                        post_main_menu(view, post);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else if(useOptions){
                    try {
                        if(data.getDouble("minTxn") <= clean_total) {
                            AddCouponConfirmationModal modal = new AddCouponConfirmationModal();
                            modal.show(getSupportFragmentManager(), "modal");
                        }
                        else
                            show_error_message(layout, getResources().getString(R.string.coupon_min_transaction_prevent));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void post_main_menu(final View view, final JSONObject data) throws JSONException {
        if(data.has("data_post") && data.has("url_post")) {
            JSONObject arr = data.getJSONObject("data_post");

            url = BASE_URL + data.getString("url_post");
            show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            data.put("invoice_data",response_data);

                            startActivity(new Intent(CouponDetailActivity.this, ChoosePaymentActivity.class)
                                    .putExtra("type","coupon")
                                    .putExtra("data",data.toString()));
                        } else {
                            show_error_message(view, response_data.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        error_handling(error, view);
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

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
        jsonObject.put("idUser",user_SP.getString("idUser",""));
        jsonObject.put("token",user_SP.getString("token",""));
        jsonObject.put("cdeCoupon",data.getString("cdeCoupon"));

        /**
         * @author Dinda
         * @note Case CICD 10245 - missing usedRefCde at prepaid/{type}-transaction
         */
        // <code>
        setUserRefCde(jsonObject);
        // </code>

        return jsonObject;
    }

    private void init() throws JSONException {
        data = new JSONObject(getIntent().getStringExtra("data"));

        if(getIntent().hasExtra("buy_options") && getIntent().getBooleanExtra("buy_options", false))
            buyOptions = true;

        if(getIntent().hasExtra("use_options") && getIntent().getBooleanExtra("use_options", false))
            useOptions = true;

        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);

        image = findViewById(R.id.coupon_image);
        title = findViewById(R.id.coupon_text);
        expired = findViewById(R.id.coupon_expired);
        amount = findViewById(R.id.coupon_amount);
        buy = findViewById(R.id.submit);
        qtyLeft = findViewById(R.id.coupon_qty);

        if(buyOptions) {
            buy.setVisibility(View.VISIBLE);
            String buttonText = getStr(R.string.buy_coupon) + " " + toLocalIdr(data.getString("price"));
            buy.setText(buttonText);
        }else if(useOptions){
            // get clean total
            if(getIntent().hasExtra("total"))
                clean_total =  getIntent().getDoubleExtra("total", 0D);

            buy.setVisibility(View.VISIBLE);
            buy.setText(getStr(R.string.use_coupon));
        }else
            buy.setVisibility(View.GONE);

        String amountText = "Potongan ";
        if(data.has("indAmount") && data.has("amount")){
            if(data.getString("indAmount").equalsIgnoreCase("FX"))
                amountText += toLocalIdr(data.getString("amount"));
            else
                amountText += data.getString("amount") + "%";
            amount.setText(amountText);
        }

        if(data.has("expired_date"))
            expired.setText(data.getString("expired_date").replace("\n", " "));

        if(data.has("qty_left")){
            qtyLeft.setVisibility(View.VISIBLE);
            qtyLeft.setText( getString(R.string.coupon_detail_notes, data.getInt("qty_left")) );
        }else{
            qtyLeft.setVisibility(View.GONE);
        }

        if(data.has("image_url") && !isNullOrEmpty(data.getString("image_url"))){
            Picasso.get()
                    .load(data.getString("image_url"))
                    .into(image);
        }

        adapter = new MyPagerAdapter(getLayoutInflater());

        if(data.has("title") && !isNullOrEmpty(data.getString("title")))
            title.setText(data.getString("title"));
        else
            title.setText(getResources().getString(R.string.coupon_default_title));

        String desc = data.has("desc") ? data.getString("desc"): "Tidak ada data";
        if(isNullOrEmpty(desc))
            desc = "Tidak ada data";

        String term = data.has("terms") ? data.getString("terms") : "Tidak ada data";
        if(isNullOrEmpty(term))
            term = "Tidak ada data";

        adapter.getViewAtPosition(0).setText(desc);
        adapter.getViewAtPosition(1).setText(term);

        view_pager.setAdapter(adapter);
        tabbar.setupWithViewPager(view_pager);

        adapter.notifyDataSetChanged();

    }

    private class MyPagerAdapter extends PagerAdapter {

        public static final int POSITION_DESCRIPTION = 0;
        public static final int POSITION_TERM = 1;

        private TextView descriptionView = null;
        private TextView termView = null;
        private String[] tabTitles = new String[]{"DESKRIPSI", "KETENTUAN"};

        public MyPagerAdapter(LayoutInflater inflater) {
            descriptionView = (TextView) inflater.inflate(R.layout.text_view_only, null);
            descriptionView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    descriptionView.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            descriptionView.setMovementMethod(new ScrollingMovementMethod());

            termView = (TextView) inflater.inflate(R.layout.text_view_only, null);
            termView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    termView.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            termView.setMovementMethod(new ScrollingMovementMethod());
        }

        public TextView getViewAtPosition(int position) {
            TextView view = null;
            switch (position) {
                case POSITION_DESCRIPTION:
                    view = descriptionView;
                    break;
                case POSITION_TERM:
                    view = termView;
                    break;
            }

            return view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public Object instantiateItem(ViewGroup collection, int position) {
            View view = getViewAtPosition(position);
            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            ((ViewPager) collection).removeView((TextView) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((TextView) object);
        }
    }
}