package com.qdi.rajapay.cashier.manage_sell_price;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.CashierAPI;
import com.qdi.rajapay.model.BaseResponseData;
import com.qdi.rajapay.model.PriceData;
import com.qdi.rajapay.model.ProductData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @module 6.0 Kasir
 * @screen 6.7.2
 */
public class ManageSellPriceChooseProductActivity extends BaseActivity implements APICallback.ItemListCallback<PriceData>, APICallback.ItemCallback<BaseResponseData>, ManageSellPriceSetSellPriceModal.Callback {
    ExpandableListView list;
    ManageSellPriceChooseProductAdapter adapter;

    CashierAPI api;

    ProductData data;
    List<PriceData> priceDataList;
    PriceData selected;
    double tempUpdatedPrice;

    LinearLayout hasDataLayout;
    View noDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sell_price_choose_product);

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new ManageSellPriceChooseProductAdapter.ClickListener() {
            @Override
            public void onClick(int group_position, int child_position) {
                selected = priceDataList.get(group_position);

                ManageSellPriceSetSellPriceModal modal = new ManageSellPriceSetSellPriceModal();
                modal.setSelected(selected);
                modal.show(getSupportFragmentManager(),"modal");
                modal.setCallback(ManageSellPriceChooseProductActivity.this);
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        hasDataLayout = findViewById(R.id.has_data_layout);
        noDataLayout = findViewById(R.id.empty_data_layout);

        data = (ProductData) getIntent().getSerializableExtra("data");
        priceDataList = new ArrayList<>();

        init_toolbar(data.getNameProduct());

        adapter = new ManageSellPriceChooseProductAdapter(this, priceDataList);
        list.setAdapter(adapter);

        api = new CashierAPI(this, user_SP);
        getPricelist();
    }

    private void getPricelist() {
        api.getCashierProductPricelist(data, this);
        show_wait_modal();
    }

    private void updateData(PriceData priceData, double price) {
        api.updateCashierProductPrice(priceData, price, this);
        show_wait_modal();
    }

    @Override
    public void onListResponseSuccess(List<PriceData> list, String message) {
        dismiss_wait_modal();
        priceDataList = list;
        if(list.size() > 0){
            hasDataLayout.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
            adapter.setList(priceDataList);
        }
        else{
            hasDataLayout.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemResponseSuccess(BaseResponseData item, String message) {
        dismiss_wait_modal();
        displaySnackBar(getStr(R.string.print_confirmation_manage_sell_price_complete_sell_price));

        for(PriceData data: priceDataList) {
            if(data.getId() != null && data.getId().equals(selected.getId())) {
                data.setAgenPrice(tempUpdatedPrice);
            } else if(data.getId() == null && data.getIdProductDetail().equals(selected.getIdProductDetail())) {
                data.setAgenPrice(tempUpdatedPrice);
            }
        }
        adapter.setList(priceDataList);

        tempUpdatedPrice = 0;
        selected = null;
        getPricelist();
    }

    @Override
    public void onUpdatePriceSubmitted(double price) {
        tempUpdatedPrice = price;
        updateData(selected, price);
    }
}

