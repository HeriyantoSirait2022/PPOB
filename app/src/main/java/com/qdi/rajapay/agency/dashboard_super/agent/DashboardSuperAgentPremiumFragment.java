package com.qdi.rajapay.agency.dashboard_super.agent;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class DashboardSuperAgentPremiumFragment extends Fragment {
    View no_data_layout;
    LinearLayout content;
    TextView total, date;
    RecyclerView list;

    DashboardSuperAgentPremiumAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    DashboardSuperAgentPremiumActivity parent;
    Double total_data = 10000d;
    Calendar calendar = Calendar.getInstance();

    String type;

    public DashboardSuperAgentPremiumFragment(String type){
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agency_dashboard_super_agent_detail_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new DashboardSuperAgentPremiumAdapter.ClickListener() {
            @Override
            public void onClick(int position) {

            }
        });
    }

    private void init(View view) throws JSONException, ParseException {
        no_data_layout = view.findViewById(R.id.no_data_layout);
        list = view.findViewById(R.id.list);
        total = view.findViewById(R.id.total);
        content = view.findViewById(R.id.content);
        date = view.findViewById(R.id.date);

        parent = (DashboardSuperAgentPremiumActivity) getActivity();
        total_data = 0d;

        prepare_data();
        adapter = new DashboardSuperAgentPremiumAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        DividerItemDecoration decoration = new DividerItemDecoration(parent,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        manage_content();
    }

    private void manage_content(){
        total.setText(parent.formatter.format(total_data)+" "+getResources().getString(R.string.agency_dashboard_agent));
        if(array.size() > 0) {
            no_data_layout.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
        else {
            no_data_layout.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }

    private void prepare_data() throws JSONException {
        for(int x = 0; x < parent.data.getJSONArray("arr").length(); x++) {
            if((type == "S" && parent.data.getJSONArray("arr").getJSONObject(x).getString("isValid").equals("S")) ||
                    (type == "not S" && !parent.data.getJSONArray("arr").getJSONObject(x).getString("isValid").equals("S"))) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("text", parent.data.getJSONArray("arr").getJSONObject(x).getString("name"));
                jsonObject.put("price", parent.data.getJSONArray("arr").getJSONObject(x).getDouble("totTransaction"));
                jsonObject.put("price_str", parent.data.getJSONArray("arr").getJSONObject(x).getString("registeredAsPremium"));
                array.add(jsonObject);
                total_data++;
            }
        }
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_referral));
//        jsonObject.put("price", 50);
//        jsonObject.put("price_str", parent.formatter.format(50)+" "+getResources().getString(R.string.agency_dashboard_agent));
//        array.add(jsonObject);
//
//        jsonObject = new JSONObject();
//        jsonObject.put("text", getResources().getString(R.string.agency_dashboard_commition_transaction));
//        jsonObject.put("price", 50);
//        jsonObject.put("price_str", parent.formatter.format(50)+" "+getResources().getString(R.string.agency_dashboard_agent));
//        array.add(jsonObject);
    }
}
