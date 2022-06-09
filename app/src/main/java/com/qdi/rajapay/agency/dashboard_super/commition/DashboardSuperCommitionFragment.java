package com.qdi.rajapay.agency.dashboard_super.commition;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.dashboard_super.DashboardSuperIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class DashboardSuperCommitionFragment extends Fragment {
    View no_data_layout;
    LinearLayout content;
    TextView total, date, info;
    RecyclerView list;

    DashboardSuperCommitionAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    DashboardSuperIndexActivity parent;
    Double total_data = 10000d;
    Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agency_dashboard_super_commition_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new DashboardSuperCommitionAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    JSONObject data = new JSONObject();
                    if(parent.array_commition.get(position).getString("text").equals(getResources().getString(R.string.agency_dashboard_commition_referral))) {
                        data.put("arr",parent.arr_commition_referral);
                        data.put("total",parent.total_commition_referral_data);
                        startActivity(new Intent(parent, DashboardSuperCommitionDetailReferralActivity.class)
                                .putExtra("data", data.toString()));
                    }
                    else if(parent.array_commition.get(position).getString("text").equals(getResources().getString(R.string.agency_dashboard_commition_transaction))) {
                        data.put("arr",parent.arr_commition_transaction);
                        data.put("total",parent.total_commition_premium_data);
                        startActivity(new Intent(parent, DashboardSuperCommitionDetailPremiumActivity.class)
                                .putExtra("data", data.toString()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view) throws JSONException, ParseException {
        no_data_layout = view.findViewById(R.id.no_data_layout);
        list = view.findViewById(R.id.list);
        total = view.findViewById(R.id.total);
        content = view.findViewById(R.id.content);
        date = view.findViewById(R.id.date);
        info = view.findViewById(R.id.info);

        parent = (DashboardSuperIndexActivity) getActivity();

//        prepare_data();
        adapter = new DashboardSuperCommitionAdapter(parent.array_commition,parent);
        layout_manager = new LinearLayoutManager(parent);
        DividerItemDecoration decoration = new DividerItemDecoration(parent,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        no_data_layout.setVisibility(View.GONE);
        total.setText("Rp. "+parent.formatter.format(parent.total_commition_data));
        info.setText(getResources().getString(R.string.agency_dashboard_commition_info));
    }

    private void prepare_data() throws JSONException {
        array.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_referral));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", "Rp. "+parent.formatter.format(50));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_transaction));
        jsonObject.put("price", 50);
        jsonObject.put("price_str", "Rp. "+parent.formatter.format(50));
        array.add(jsonObject);
    }
}
