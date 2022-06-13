package com.qdi.rajapay.point;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qdi.rajapay.R;

import org.json.JSONException;

import java.text.ParseException;

public class PointRewardFragment extends Fragment {

    PointIndexActivity parent;

    RecyclerView list;
    PointRewardAdapter adapter;
    RecyclerView.LayoutManager layout_manager;

    LinearLayout no_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_point_reward,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new PointRewardAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                parent.selectedData = parent.arrReward.get(position);
                if(parent.selectedData!=null) {
                    RedeemConfirmationModal modal = new RedeemConfirmationModal();
                    modal.show(parent.getSupportFragmentManager(), "modal");
                }
            }
        });

    }

    private void init(View view) throws JSONException, ParseException {
        list = view.findViewById(R.id.list);
        parent = (PointIndexActivity) getActivity();

        adapter = new PointRewardAdapter(parent.arrReward, parent);
        layout_manager = new LinearLayoutManager(parent);
        DividerItemDecoration decoration = new DividerItemDecoration(parent,DividerItemDecoration.HORIZONTAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        no_data = view.findViewById(R.id.no_data_layout);
        no_data.setVisibility(View.GONE);
    }

    public void show_data(){
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (parent.arrReward.size() == 0) {
            no_data.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            no_data.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }
}