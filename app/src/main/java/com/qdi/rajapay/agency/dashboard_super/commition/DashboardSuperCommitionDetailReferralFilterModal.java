package com.qdi.rajapay.agency.dashboard_super.commition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.commition_history.CommitionHistoryFilterAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DashboardSuperCommitionDetailReferralFilterModal extends BottomSheetDialogFragment {
    ImageView cancel;
    RecyclerView list;

    CommitionHistoryFilterAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    DashboardSuperCommitionDetailReferralActivity parent;
    Double total_commition_data = 0d;
    Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agency_commition_history_filter_modal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        adapter.setOnItemClickListener(new CommitionHistoryFilterAdapter.ClickListener() {
            @Override
            public void onClick(final int position, final boolean selected) {
                try {
                    if(selected){
                        for(int x=0;x<array.size();x++)
                            array.get(x).put("selected",false);
                        adapter.notifyDataSetChanged();
                        array.get(position).put("selected",true);
                        adapter.notifyDataSetChanged();

                        parent.filter_data = array.get(position);
                        parent.get_data();

                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);
        cancel = view.findViewById(R.id.cancel);

        parent = (DashboardSuperCommitionDetailReferralActivity) getActivity();

        prepare_data();
        adapter = new CommitionHistoryFilterAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void prepare_data() throws JSONException {
        SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM");

        Calendar cal = Calendar.getInstance();
        Date result = cal.getTime();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("radio", getResources().getString(R.string.agency_commition_history_filter_all));
        jsonObject.put("selected", false);
        jsonObject.put("month", "0");
        array.add(jsonObject);

        cal = Calendar.getInstance();
        result = cal.getTime();

        jsonObject = new JSONObject();
        jsonObject.put("radio", getResources().getString(R.string.agency_commition_history_filter_this_month));
        jsonObject.put("selected", false);
        jsonObject.put("month", format_date.format(result));
        array.add(jsonObject);

        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        result = cal.getTime();

        jsonObject = new JSONObject();
        jsonObject.put("radio", getResources().getString(R.string.agency_commition_history_filter_last_month));
        jsonObject.put("selected", false);
        jsonObject.put("month", format_date.format(result));
        array.add(jsonObject);

        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        result = cal.getTime();

        jsonObject = new JSONObject();
        jsonObject.put("radio", getResources().getString(R.string.agency_commition_history_filter_2_last_month));
        jsonObject.put("selected", false);
        jsonObject.put("month", format_date.format(result));
        array.add(jsonObject);

        if(!parent.filter_data.toString().equals("{}")) {
            for (int x = 0; x < array.size(); x++) {
                if(parent.filter_data.getString("radio").equals(
                        array.get(x).getString("radio")
                )) {
                    array.get(x).put("selected", true);
                    break;
                }
            }
        }
        else
            array.get(0).put("selected", true);
    }
}
