package com.qdi.rajapay.agency.dashboard_super.bonus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.dashboard_super.DashboardSuperIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class DashboardSuperBonusFragment extends Fragment {
    View no_data_layout;
    LinearLayout content;
    TextView total, date, info;
    RecyclerView list;
    Button help;

    DashboardSuperBonusAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    DashboardSuperIndexActivity parent;
    Double total_data = 0d;
    Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agency_dashboard_super_bonus_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new DashboardSuperBonusAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:cs@rajapay.com"));
                startActivity(intent);
            }
        });
    }

    private void init(View view) throws JSONException, ParseException {
        no_data_layout = view.findViewById(R.id.no_data_layout);
        list = view.findViewById(R.id.list);
        total = view.findViewById(R.id.total);
        content = view.findViewById(R.id.content);
        date = view.findViewById(R.id.date);
        help = view.findViewById(R.id.help);
        info = view.findViewById(R.id.info);

        parent = (DashboardSuperIndexActivity) getActivity();

        prepare_data();
        adapter = new DashboardSuperBonusAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent,RecyclerView.HORIZONTAL,false);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        no_data_layout.setVisibility(View.GONE);
        total.setText(parent.formatter.format(parent.total_bonus_data)+" "+getResources().getString(R.string.agency_dashboard_bonus_more));
        info.setText(getResources().getString(R.string.agency_dashboard_bonus_info)+" "+ parent.total_agen_premium_data.intValue()
                +" "+getResources().getString(R.string.agency_dashboard_bonus_info_2));
    }

    private void prepare_data() throws JSONException {
        array.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("condition", 50d);
        jsonObject.put("text", "Rekrut "+parent.formatter.format(jsonObject.getDouble("condition"))+" agen premium");
        jsonObject.put("price", 750000);
        jsonObject.put("price_str", "Rp. "+parent.formatter.format(jsonObject.getDouble("price")));
        jsonObject.put("enabled", parent.total_agen_premium_data >= jsonObject.getDouble("condition"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("condition", 500d);
        jsonObject.put("text", "Rekrut "+parent.formatter.format(jsonObject.getDouble("condition"))+" agen premium");
        jsonObject.put("price", 5000000);
        jsonObject.put("price_str", "Rp. "+parent.formatter.format(jsonObject.getDouble("price")));
        jsonObject.put("enabled", parent.total_agen_premium_data >= jsonObject.getDouble("condition"));
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("condition", 1000d);
        jsonObject.put("text", "Rekrut "+parent.formatter.format(jsonObject.getDouble("condition"))+" agen premium");
        jsonObject.put("price", 10000000);
        jsonObject.put("price_str", "Rp. "+parent.formatter.format(jsonObject.getDouble("price")));
        jsonObject.put("enabled", parent.total_agen_premium_data >= jsonObject.getDouble("condition"));
        array.add(jsonObject);
    }
}
