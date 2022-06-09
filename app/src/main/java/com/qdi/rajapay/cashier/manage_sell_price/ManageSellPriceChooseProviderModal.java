package com.qdi.rajapay.cashier.manage_sell_price;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.model.ProductData;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @module 6.0 Kasir
 * @screen 6.7.1
 */
public class ManageSellPriceChooseProviderModal extends BottomSheetDialogFragment {
    TextView service;
    RecyclerView list;
    ImageView cancel;

    ManageSellPriceChooseProviderAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    List<ProductData> array = new ArrayList<>();

    ManageSellPriceIndexActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manage_sell_price_choose_provider_modal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new ManageSellPriceChooseProviderAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                dismiss();
                startActivity(new Intent(parent,ManageSellPriceChooseProductActivity.class)
                        .putExtra("data",array.get(position)));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void init(View view) throws JSONException {
        service = view.findViewById(R.id.service);
        list = view.findViewById(R.id.list);
        cancel = view.findViewById(R.id.cancel);

        parent = (ManageSellPriceIndexActivity) getActivity();

        array = parent.productDataList;
        adapter = new ManageSellPriceChooseProviderAdapter(array, parent);
        layout_manager = new GridLayoutManager(parent,4);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        service.setText(parent.selected.getString("name"));
    }
}
