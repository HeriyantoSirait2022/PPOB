package com.qdi.rajapay.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.BuildConfig;
import com.qdi.rajapay.R;
import com.qdi.rajapay.account.AccountPinModal;
import com.qdi.rajapay.account.your_qr.YourQrScanActivity;
import com.qdi.rajapay.account.your_qr.change_nominal.YourQrChangeNominalActivity;
import com.qdi.rajapay.agency.target.TargetIndexActivity;
import com.qdi.rajapay.contact_us.ContactUsListActivity;
import com.qdi.rajapay.coupon.home.CouponHomeActivity;
import com.qdi.rajapay.deposit.TopUpActivity;
import com.qdi.rajapay.inbox.InboxListActivity;
import com.qdi.rajapay.main_menu.bank_transfer.BankTransferSelectActivity;
import com.qdi.rajapay.main_menu.electrical.ElectricalInputNoActivity;
import com.qdi.rajapay.main_menu.electrical_token.ElectricalTokenInputNoActivity;
import com.qdi.rajapay.main_menu.emoney.EmoneySelectActivity;
import com.qdi.rajapay.main_menu.games.PrepaidGamesSelectActivity;
import com.qdi.rajapay.main_menu.gas.GasInputNoActivity;
import com.qdi.rajapay.main_menu.insurance.InsuranceInputNoActivity;
import com.qdi.rajapay.main_menu.multifinance.MultifinanceChooseProviderActivity;
import com.qdi.rajapay.main_menu.phone.PhoneChooseProviderActivity;
import com.qdi.rajapay.main_menu.postpaid_data.PostpaidDataChooseProviderActivity;
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoActivity;
import com.qdi.rajapay.main_menu.prepaid_mobile_credit.PrepaidMobileCreditInputPhoneNoActivity;
import com.qdi.rajapay.main_menu.tv.TvChooseProviderActivity;
import com.qdi.rajapay.main_menu.water.WaterChooseAreaActivity;
import com.qdi.rajapay.utils.FirebaseInstanceService;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/***
 * @module 4.0 Beranda
 * @screen 4.0.1
 */
public class HomeFragment extends Fragment {
    RecyclerView list;
    LinearLayout top_up, scan, transfer, agent_target;
    ImageView notification, contact_us;
    ViewPager banner;
    TextView balance, name, phone, target_this_month;
    DotsIndicator banner_indicator;

    MainAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    HashMap<Integer,ArrayList<Class>> array_class = new HashMap<>();

    HomeBannerAdapter banner_adapter;
    ArrayList<JSONObject> banner_array = new ArrayList<>();
    ArrayList<Class> validateProduct = new ArrayList<>();

    MainActivity parent;
    int currentPage = 0;
    int NUM_PAGES = 0;

    Button buyCoupon;
    Boolean verificationStatus = false;
    Boolean isStatusVerify = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_home_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new MainAdapter.ClickListener() {
            @Override
            public void onClick(int position1, int position2) {
                if(validateProduct.contains(array_class.get(position1).get(position2))){
                    checkVerificationStatus(array_class.get(position1).get(position2), true);
                }else{
                    startActivity(new Intent(parent,array_class.get(position1).get(position2)));
                }
            }
        });

        top_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, TopUpActivity.class));
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, InboxListActivity.class));
            }
        });

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, ContactUsListActivity.class));
            }
        });

        agent_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, TargetIndexActivity.class));
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVerificationStatus(YourQrScanActivity.class, false);
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, YourQrChangeNominalActivity.class));
            }
        });

        buyCoupon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(parent, CouponHomeActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        get_deposit_data();
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);
        top_up = view.findViewById(R.id.top_up);
        scan = view.findViewById(R.id.scan);
        transfer = view.findViewById(R.id.transfer);
        notification = view.findViewById(R.id.notification);
        contact_us = view.findViewById(R.id.contact_us);
        banner_indicator = view.findViewById(R.id.banner_indicator);
        banner = view.findViewById(R.id.banner);
        balance = view.findViewById(R.id.balance);
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        target_this_month = view.findViewById(R.id.target_this_month);
        agent_target = view.findViewById(R.id.agent_target);

        parent = (MainActivity) getActivity();
        prepare_data();

        adapter = new MainAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        banner_adapter = new HomeBannerAdapter(banner_array,parent);
        banner.setAdapter(banner_adapter);
        banner_indicator.setViewPager(banner);

        JSONObject user_data = new JSONObject(parent.user_SP.getString("user",""));
        name.setText(user_data.getString("shopName"));
        phone.setText(user_data.getString("noHp"));

        buyCoupon = view.findViewById(R.id.buy_coupon);

        get_banner_data();
        get_target_data();
        verifiedPinStatus();
        verifiedFCMToken();
        verifiedFCMTopic();

        /**
         * @author Jesslyn
         * @note add automatic view pager req 33
         */
        // <code>
        final Handler handler = new Handler();

        final Runnable update = new Runnable()  {
            public void run() {
                if ( currentPage == NUM_PAGES ) {
                    currentPage = 0;
                }
                banner.setCurrentItem(currentPage++, true);
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 1000, 10000);
        // </code>
    }

    private void prepare_data() throws JSONException {
        ArrayList<Class> prepaid_array_class = new ArrayList<>();
        JSONArray arr_prepaid = new JSONArray();
        arr_prepaid.put(0,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_mobile_data)+"\",\"image\":"+R.drawable.ic_icon_paket_datahdpi+"}"));
        arr_prepaid.put(1,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_mobile_credit)+"\",\"image\":"+R.drawable.ic_icon_pulsahdpi+"}"));
        arr_prepaid.put(2,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_electrical_token)+"\",\"image\":"+R.drawable.ic_icon_plnhdpi+"}"));
        arr_prepaid.put(3,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_games)+"\",\"image\":"+R.drawable.ic_icon_permainan+"}"));
        arr_prepaid.put(4,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_electronic_money)+"\",\"image\":"+R.drawable.ic_electronic_money+"}"));
        arr_prepaid.put(5,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_transfer_bank)+"\",\"image\":"+R.drawable.ic_transfer_bank+"}"));
        prepaid_array_class.add(PrepaidDataInputPhoneNoActivity.class);
        prepaid_array_class.add(PrepaidMobileCreditInputPhoneNoActivity.class);
        prepaid_array_class.add(ElectricalTokenInputNoActivity.class);
        prepaid_array_class.add(PrepaidGamesSelectActivity.class);
        prepaid_array_class.add(EmoneySelectActivity.class);
        prepaid_array_class.add(BankTransferSelectActivity.class);

        ArrayList<Class> postpaid_array_class = new ArrayList<>();
        JSONArray arr_postpaid = new JSONArray();
        arr_postpaid.put(0,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_postpaid)+"\",\"image\":"+R.drawable.ic_icon_prabayarhdpi+"}"));
        arr_postpaid.put(1,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_electrical)+"\",\"image\":"+R.drawable.ic_icon_listrikhdpi+"}"));
        arr_postpaid.put(2,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_gas)+"\",\"image\":"+R.drawable.ic_icon_pgnhdpi+"}"));
        arr_postpaid.put(3,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_insurance)+"\",\"image\":"+R.drawable.ic_icon_asuransihdpi+"}"));
        arr_postpaid.put(4,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_water)+"\",\"image\":"+R.drawable.ic_icon_pdam_largehdpi+"}"));
        arr_postpaid.put(5,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_tv)+"\",\"image\":"+R.drawable.ic_icon_tvhdpi+"}"));
        arr_postpaid.put(6,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_phone)+"\",\"image\":"+R.drawable.ic_icon_telkomhdpi+"}"));
        arr_postpaid.put(7,new JSONObject("{\"name\": \""+getResources().getString(R.string.home_multifinance)+"\",\"image\":"+R.drawable.ic_icon_multifinancehdpi+"}"));
        postpaid_array_class.add(PostpaidDataChooseProviderActivity.class);

        /**
         * @author : Jesslyn
         * @note : fixing intent action for 4.14; 4.15; 4.16;
         */
        // <code>
        postpaid_array_class.add(ElectricalInputNoActivity.class);
        postpaid_array_class.add(GasInputNoActivity.class);
        postpaid_array_class.add(InsuranceInputNoActivity.class);
        // </code>

        postpaid_array_class.add(WaterChooseAreaActivity.class);
        postpaid_array_class.add(TvChooseProviderActivity.class);
        postpaid_array_class.add(PhoneChooseProviderActivity.class);
        postpaid_array_class.add(MultifinanceChooseProviderActivity.class);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",getResources().getString(R.string.prepaid_label));
        jsonObject.put("arr",arr_prepaid);
        array.add(jsonObject);
        array_class.put(0,prepaid_array_class);

        jsonObject = new JSONObject();
        jsonObject.put("name",getResources().getString(R.string.postpaid_label));
        jsonObject.put("arr",arr_postpaid);
        array.add(jsonObject);
        array_class.put(1,postpaid_array_class);

        // add product to validate
        validateProduct.add(PrepaidGamesSelectActivity.class);
    }

    private void get_deposit_data() {
        parent.url = parent.BASE_URL+"/mobile/dashboard/show-info";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        parent.user_edit_SP.putFloat("balance_deposit", (float) response_data.getDouble("deposit"));
                        parent.user_edit_SP.commit();

                        balance.setText("Rp. "+parent.formatter.format(response_data.getDouble("deposit")));
                    } else {
                        parent.show_error_message(parent.layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parent.error_handling(error, parent.layout);
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

        parent.consume_api(jsonObjectRequest);
    }

    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes 0911254300-210 W03 News notification update
     */
    // <code>
    private void verifiedFCMTopic(){
        if(parent.user_SP.getBoolean("fcm_topic_status", false)){
            return;
        }

        FirebaseMessaging.getInstance().subscribeToTopic("news")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.w(BaseActivity.TAG, "Fetching FCM topic subscription failed", task.getException());
                        return;
                    }
                    parent.user_edit_SP.putBoolean("fcm_topic_status", true);
                }
            });
    }
    // </code>

    /**
     * @author Jesslyn
     * @note 0721531311-179 E05 New user required to change PIN at the first time. New feature would be enhanced at 4.10 - 4.20
     */
    // <code>
    private void verifiedFCMToken(){
        if(parent.user_SP.getBoolean("fcm_token_status", false)){
            return;
        }

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(BaseActivity.TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    final String fcmToken = task.getResult();

                    FirebaseInstanceService fis = new FirebaseInstanceService();
                    fis.sendRegistrationServer(parent, fcmToken);
                }
            });
    }
    // </code>

    /**
     * @author Jesslyn
     * @note 0721531311-179 E05 New user required to change PIN at the first time. New feature would be enhanced at 4.10 - 4.20
     */
    // <code>
    private void verifiedPinStatus(){
        if(parent.user_SP.getBoolean("pin_verified_status", false)){
            return;
        }

        parent.url = parent.BASE_URL+"/mobile/account/pin-status";
        parent.show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    parent.dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed") &&
                            response_data.getString("description").equals("PIN_UNSET")) {
                        parent.user_edit_SP.putBoolean("pin_verified_status", false);
                        parent.user_edit_SP.commit();

                        AccountPinModal modal = new AccountPinModal();
                        modal.setCancelable(false);
                        modal.show(getChildFragmentManager(),"modal");
                    }else if(!response_data.getString("type").equals("Failed") &&
                            response_data.getString("description").equals("PIN_SET")){
                        parent.user_edit_SP.putBoolean("pin_verified_status", true);
                        parent.user_edit_SP.commit();

                    }else{
                        parent.show_error_message(parent.layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parent.error_handling(error, parent.layout);
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

        parent.consume_api(jsonObjectRequest);
    }
    // </code>

    /**
     * @author Imelda Susanti
     * @patch FR19022
     * @notes this function intended to check verification status, if status had been verified
     * ...... on the server then check status, otherwise do check verification status on the server
     * @param cls
     */
    // <code>
    private void checkVerificationStatus(Class<?> cls, boolean showDialog){
        if(isStatusVerify){
            if(verificationStatus){
                startActivity(new Intent(parent, cls));
            }else{
                if(showDialog){
                    HomeProductClosedModal productClosedModal = new HomeProductClosedModal();
                    productClosedModal.show(getChildFragmentManager(),"modal");
                }else{
                    // display default error message
                    parent.show_error_message(parent.layout,parent.getStr(R.string.home_verification_failed));
                }
            }
        }else{
            get_verified_status(cls, showDialog);
        }
    }
    // </code>

    private void get_verified_status(Class<?> cls, boolean showDialog) {
        isStatusVerify = false;
        parent.url = parent.BASE_URL+"/mobile/account/qr-status";
        parent.show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    parent.dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")){
                        isStatusVerify = true;

                        if(response_data.getString("description").equals("VERIFIED"))
                            verificationStatus = true;

                        checkVerificationStatus(cls, showDialog);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parent.error_handling(error, parent.layout);
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

        parent.consume_api(jsonObjectRequest);
    }

    private void get_banner_data() {
        parent.url = parent.BASE_URL+"/mobile/dashboard/show-banner";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        for(int x = 0; x < response_data.getJSONArray("arrBannerList").length(); x++){
                            JSONObject data_banner = response_data.getJSONArray("arrBannerList").getJSONObject(x);
                            banner_array.add(data_banner);
                        }
                        banner_adapter.notifyDataSetChanged();
                        NUM_PAGES = response_data.getJSONArray("arrBannerList").length();
                    } else {
                        parent.show_error_message(parent.layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parent.error_handling(error, parent.layout);
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

        parent.consume_api(jsonObjectRequest);
    }

    private void get_target_data() {
        parent.url = parent.BASE_URL+"/mobile/agen/target-transaction";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        if(response_data.has("targetThisMonth"))
                            target_this_month.setText(response_data.getString("targetThisMonth")+
                            /**
                             * @author liao mei
                             * @note change to use free text instead of res file to avoid fragment not attached to context
                             */
                            // <code>
                            " Transaksi Lagi");
                            // </code>

                            /**
                             * @author Imelda Susanti
                             * @patch FR19022
                             * @notes add verification status on login scenario
                             */
                            // <code>
                            if(response_data.has("verStat"))
                                verificationStatus = response_data.getBoolean("verStat");
                            else
                                verificationStatus = false;
                            // </code>
                    } else {
                        parent.show_error_message(parent.layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parent.error_handling(error, parent.layout);
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

        parent.consume_api(jsonObjectRequest);
    }
}
