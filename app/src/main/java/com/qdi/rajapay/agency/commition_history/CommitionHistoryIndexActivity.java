package com.qdi.rajapay.agency.commition_history;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @module 7.0 Keagenan
 * @screen 7.3.1A
 */
public class CommitionHistoryIndexActivity extends BaseActivity {
    ViewPager view_pager;
    TabLayout tabbar;
    Button filter;

    ViewPagerAdapter viewPagerAdapter;

    JSONObject filter_data = new JSONObject();
    Fragment selected_fragment;

    ArrayList<JSONObject> array_pending = new ArrayList<>(),
            array_success = new ArrayList<>(),
            array_cancel = new ArrayList<>();
    Double total_pending_commition_data = 0d,
            total_success_commition_data = 0d,
            total_cancel_commition_data = 0d;
    Date date_data = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_commition_history_index);

        init_toolbar(getResources().getString(R.string.agency_commition_history));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommitionHistoryFilterModal modal = new CommitionHistoryFilterModal();
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selected_fragment = viewPagerAdapter.getItem(position);

                if(selected_fragment instanceof CommitionHistoryPendingFragment) {
                    ((CommitionHistoryPendingFragment) selected_fragment).show_data();
                }
                else if(selected_fragment instanceof CommitionHistorySuccessFragment) {
                    ((CommitionHistorySuccessFragment) selected_fragment).show_data();
                }
                else if(selected_fragment instanceof CommitionHistoryCancelFragment) {
                    ((CommitionHistoryCancelFragment) selected_fragment).show_data();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void init() throws JSONException, ParseException {
        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);
        filter = findViewById(R.id.filter);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new CommitionHistoryPendingFragment(),getResources().getString(R.string.agency_commition_history_pending));
        viewPagerAdapter.addFragment(new CommitionHistorySuccessFragment(),getResources().getString(R.string.agency_commition_history_success));
        viewPagerAdapter.addFragment(new CommitionHistoryCancelFragment(),getResources().getString(R.string.agency_commition_history_cancel));

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        selected_fragment = new CommitionHistoryPendingFragment();
        get_data("");
    }

    public void get_data(String date) throws JSONException, ParseException {
        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM");
        date_data = !date.equals("") ? parse_date.parse(date) : new Date();

        url = BASE_URL+"/mobile/agen/commission";
        Log.d("url",url);
        show_wait_modal();

        JSONObject param = getBaseAuth();
        if(!date.isEmpty())
            param.put("month", date);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        array_pending.clear();
                        array_success.clear();
                        array_cancel.clear();

                        add_to_array(response_data.getJSONArray("arrPenCom"),array_pending);
                        add_to_array(response_data.getJSONArray("arrSuccsCom"),array_success);
                        add_to_array(response_data.getJSONArray("arrCancCom"),array_cancel);

                        total_pending_commition_data = response_data.getDouble("totPenCom");
                        total_success_commition_data = response_data.getDouble("totSuccsCom");
                        total_cancel_commition_data = response_data.getDouble("totCancCom");

                        if(selected_fragment instanceof CommitionHistoryPendingFragment) {
                            ((CommitionHistoryPendingFragment) selected_fragment).show_data();
                        }
                        else if(selected_fragment instanceof CommitionHistorySuccessFragment) {
                            ((CommitionHistorySuccessFragment) selected_fragment).show_data();
                        }
                        else if(selected_fragment instanceof CommitionHistoryCancelFragment) {
                            ((CommitionHistoryCancelFragment) selected_fragment).show_data();
                        }
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException | ParseException e) {
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

    private void add_to_array(JSONArray arr, ArrayList<JSONObject> array) throws JSONException, ParseException {
        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format_date = new SimpleDateFormat("HH:mm");
        SimpleDateFormat format_day = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US);

        String day = "";
        String time = "";
        for(int i = 0; i < arr.length(); i++){
            JSONObject jsonObject = new JSONObject();
            if(arr.getJSONObject(i).has("comDtm")){
                // arrSuccsCom
                jsonObject.put("time", format_date.format(parse_date.parse(arr.getJSONObject(i).getString("comDtm"))));
                jsonObject.put("day", format_day.format(parse_date.parse(arr.getJSONObject(i).getString("comDtm"))));
            }else if(arr.getJSONObject(i).has("comDate")){
                // arrPenCom
                jsonObject.put("time", arr.getJSONObject(i).getString("comDate"));
                jsonObject.put("day", format_day.format(parse_date.parse(arr.getJSONObject(i).getString("txnDate"))));
            }else if(arr.getJSONObject(i).has("txnDate")){
                // arrCancCom
                jsonObject.put("time", format_date.format(parse_date.parse(arr.getJSONObject(i).getString("txnDate"))));
                jsonObject.put("day", format_day.format(parse_date.parse(arr.getJSONObject(i).getString("txnDate"))));
            }

            if(i == 0 || !day.equalsIgnoreCase(jsonObject.getString("day"))){
                // Change temporary variable
                day = monthToBahasa(dayToBahasa(jsonObject.getString("day")));
                time = jsonObject.getString("time");

                // Update jsonObject
                jsonObject.put("day", day);

                array.add(jsonObject);
                jsonObject = new JSONObject();

                jsonObject.put("time", time);
                jsonObject.put("day", day);
            }

            jsonObject.put("title", arr.getJSONObject(i).getString("nmCom"));
            jsonObject.put("price", arr.getJSONObject(i).getDouble("comFee"));
            jsonObject.put("price_str", "Rp. "+formatter.format(arr.getJSONObject(i).getDouble("comFee")));
            array.add(jsonObject);
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