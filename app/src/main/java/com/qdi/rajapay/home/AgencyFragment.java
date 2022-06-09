package com.qdi.rajapay.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.commition_history.CommitionHistoryIndexActivity;
import com.qdi.rajapay.agency.dashboard_super.DashboardSuperIndexActivity;
import com.qdi.rajapay.agency.target.TargetIndexActivity;
import com.qdi.rajapay.agency.upgrade_premium.UpgradePremiumDetailActivity;
import com.qdi.rajapay.contact_us.ContactUsListActivity;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 7.0 Keagenan
 * @screen 7.0.1
 */
public class AgencyFragment extends Fragment {
    MaterialButton upgrade;
    RecyclerView list;
    Button contact_us;

    AgencyAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ArrayList<Class> array_class = new ArrayList<>();

    MainActivity parent;

    JSONObject data = new JSONObject();
    LinearLayout premium;
    ImageView standard;
    TextView referralCode;
    Boolean isPremium = false;
    String refCode = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_agency_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPremium){
                    /*Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.my_referral_code));
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://www.rajapay.co.id/register/" + refCode);
                    startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
                    */
                    ShareCompat.IntentBuilder.from(parent)
                            .setType("text/plain")
                            .setChooserTitle("Bagikan kode referral dengan : ")
                            .setText("Join RAJAPAY sekarang juga! Klik link berikut: https://api.rajapay.co.id/register/" + refCode)
                            .startChooser();
                }else
                    startActivity(new Intent(parent, UpgradePremiumDetailActivity.class));
            }
        });

        adapter.setOnItemClickListener(new AgencyAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(parent, array_class.get(position)));
            }
        });

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, ContactUsListActivity.class));
            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);
        upgrade = view.findViewById(R.id.upgrade);
        contact_us = view.findViewById(R.id.contact_us);
        referralCode = view.findViewById(R.id.referral_code);

        standard = view.findViewById(R.id.standard);
        premium = view.findViewById(R.id.premium);

        parent = (MainActivity) getActivity();

        get_super_data();
        adapter = new AgencyAdapter(array,parent);
        layout_manager = new GridLayoutManager(parent,2);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void add_list(String name, int image, Class redirect_to) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("image", image);
        array.add(jsonObject);
        array_class.add(redirect_to);
    }

    private void get_super_data() {
        parent.url = parent.BASE_URL+"/mobile/agen/premium-stat";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data = response_data;

                        isPremium = false;
                        if(data.has("account_type")){
                            refCode = data.getString("referral_code");

                            if(response_data.getString("account_type").equalsIgnoreCase("P")){
                                // Premium Agent
                                isPremium = true;

                                premium.setVisibility(View.VISIBLE);
                                standard.setVisibility(View.GONE);
                                referralCode.setText(refCode);
                                upgrade.setText(parent.getStr(R.string.share_code));

                                add_list(parent.getResources().getString(R.string.agency_dashboard_premium), R.drawable.ic_icon_riwayat_agenhdpi, DashboardSuperIndexActivity.class);
                                add_list(parent.getResources().getString(R.string.agency_target), R.drawable.ic_icon_target_agenhdpi, TargetIndexActivity.class);
                                add_list(parent.getResources().getString(R.string.agency_commition_history), R.drawable.ic_icon_promosihdpi, CommitionHistoryIndexActivity.class);
                            }
                            else{
                                // Standard agent
                                premium.setVisibility(View.GONE);
                                standard.setVisibility(View.VISIBLE);
                                referralCode.setText("");
                                upgrade.setText(parent.getStr(R.string.agency_upgrade_premium));

                                add_list(parent.getResources().getString(R.string.agency_target), R.drawable.ic_icon_target_agenhdpi, TargetIndexActivity.class);
                                add_list(parent.getResources().getString(R.string.agency_commition_history), R.drawable.ic_icon_promosihdpi, CommitionHistoryIndexActivity.class);
                            }
                        }


                        adapter.notifyDataSetChanged();
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
