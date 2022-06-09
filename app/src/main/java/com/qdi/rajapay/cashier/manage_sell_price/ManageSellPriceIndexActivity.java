package com.qdi.rajapay.cashier.manage_sell_price;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.CashierAPI;
import com.qdi.rajapay.home.MainAdapter;
import com.qdi.rajapay.model.ProductData;
import com.qdi.rajapay.model.enums.ProductType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @module 6.2 Kasir
 * @screen 6.2.1
 */
public class ManageSellPriceIndexActivity extends BaseActivity implements APICallback.ItemListCallback<ProductData> {
    RecyclerView list;

    MainAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    HashMap<Integer,ArrayList<Class>> array_class = new HashMap<>();

    JSONObject selected = new JSONObject();
    List<ProductData> productDataList;
    CashierAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sell_price);

        init_toolbar(getResources().getString(R.string.cashier_manage_sell_price));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new MainAdapter.ClickListener() {
            @Override
            public void onClick(int position1, int position2) {
                try {
                    selected = array.get(position1).getJSONArray("arr").getJSONObject(position2);
                    getProduct((ProductType) selected.get("product_type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        prepare_data();

        adapter = new MainAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        productDataList = new ArrayList<>();

        api = new CashierAPI(this, user_SP);
    }

    private void prepare_data() throws JSONException {

        JSONArray arr_prepaid = new JSONArray();
        arr_prepaid.put(0,add_array(getResources().getString(R.string.home_mobile_data),R.drawable.ic_icon_paket_datahdpi, ProductType.DATA));
        arr_prepaid.put(1,add_array(getResources().getString(R.string.home_mobile_credit),R.drawable.ic_icon_pulsahdpi, ProductType.PULSA));
        arr_prepaid.put(2,add_array(getResources().getString(R.string.home_electrical_token),R.drawable.ic_icon_plnhdpi, ProductType.TOKENPLN));
        arr_prepaid.put(3,add_array(getResources().getString(R.string.home_topup_games),R.drawable.ic_icon_permainan_topup, ProductType.TOPUPGAMES));
        arr_prepaid.put(4,add_array(getResources().getString(R.string.home_voucher_games),R.drawable.ic_icon_permainan_voucher, ProductType.VOUCHERGAMES));
        arr_prepaid.put(5,add_array(getResources().getString(R.string.home_electronic_money),R.drawable.ic_electronic_money, ProductType.EMONEY));
        arr_prepaid.put(6,add_array(getResources().getString(R.string.home_transfer_bank),R.drawable.ic_transfer_bank, ProductType.BANKTRANSFER));

        JSONArray arr_postpaid = new JSONArray();
        arr_postpaid.put(0,add_array(getResources().getString(R.string.home_postpaid),R.drawable.ic_icon_prabayarhdpi, ProductType.CELLULAR));
        arr_postpaid.put(1,add_array(getResources().getString(R.string.home_electrical),R.drawable.ic_icon_listrikhdpi,ProductType.PLN));
        arr_postpaid.put(2,add_array(getResources().getString(R.string.home_gas),R.drawable.ic_icon_pgnhdpi, ProductType.PGN));
        arr_postpaid.put(3,add_array(getResources().getString(R.string.home_insurance),R.drawable.ic_icon_asuransihdpi, ProductType.BPJS));
        arr_postpaid.put(4,add_array(getResources().getString(R.string.home_water),R.drawable.ic_icon_pdam_largehdpi,ProductType.PDAM));
        arr_postpaid.put(5,add_array(getResources().getString(R.string.home_tv),R.drawable.ic_icon_tvhdpi,ProductType.TV));
        arr_postpaid.put(6,add_array(getResources().getString(R.string.home_phone),R.drawable.ic_icon_telkomhdpi,ProductType.TELKOM));
        arr_postpaid.put(7,add_array(getResources().getString(R.string.home_multifinance),R.drawable.ic_icon_multifinancehdpi,ProductType.MULTIFINANCE));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",getResources().getString(R.string.prepaid_label));
        jsonObject.put("arr",arr_prepaid);
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name",getResources().getString(R.string.postpaid_label));
        jsonObject.put("arr",arr_postpaid);
        array.add(jsonObject);
    }

    private JSONObject add_array(String name,int image, ProductType type) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",name);
        jsonObject.put("image",image);
        jsonObject.put("product_type", type);
        return jsonObject;
    }

    private void getProduct(ProductType type) {

        api.getCashierProduct(type, this);
        show_wait_modal();
    }

    @Override
    public void onListResponseSuccess(List<ProductData> list, String message) {
        dismiss_wait_modal();
        productDataList = list;
        ManageSellPriceChooseProviderModal modal = new ManageSellPriceChooseProviderModal();
        modal.show(getSupportFragmentManager(),"modal");
    }


}