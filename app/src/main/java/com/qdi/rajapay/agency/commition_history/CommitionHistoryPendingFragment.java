package com.qdi.rajapay.agency.commition_history;

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

/**
 * @module 7.0 Keagenan
 * @screen 7.3.1.a
 */
public class CommitionHistoryPendingFragment extends Fragment {
    View no_data_layout;
    LinearLayout content;
    TextView total_commition;
    RecyclerView list;

    CommitionHistoryAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    CommitionHistoryIndexActivity parent;
    Double total_commition_data = 0d;
    Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agency_commition_history_pending_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void init(View view) throws JSONException, ParseException {
        no_data_layout = view.findViewById(R.id.no_data_layout);
        list = view.findViewById(R.id.list);
        total_commition = view.findViewById(R.id.total_commition);
        content = view.findViewById(R.id.content);

        parent = (CommitionHistoryIndexActivity) getActivity();

//        prepare_data();
        adapter = new CommitionHistoryAdapter(parent.array_pending,parent);
        layout_manager = new LinearLayoutManager(parent);
        DividerItemDecoration decoration = new DividerItemDecoration(parent,DividerItemDecoration.HORIZONTAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    @Override
    public void onResume() {
        show_data();
        super.onResume();
    }

    public void show_data(){
        adapter.notifyDataSetChanged();
        total_commition.setText("Rp. "+parent.formatter.format(parent.total_pending_commition_data));
        if(parent.array_pending.size() == 0) {
            no_data_layout.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }
        else {
            no_data_layout.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }
}
