package com.qdi.rajapay.cashier;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.cashier.manage_sell_price.ManageSellPriceIndexActivity;
import com.qdi.rajapay.cashier.monthly_report.CashierMonthlyReportActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList; 

/**
 * @module 6.0 Kasir
 * @screen 6.0.1
 */
public class CashierIndexActivity extends BaseActivity {
    TextView sales, profit;
    Button print;
    RecyclerView list;

    CashierIndexAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>(), array_breakdown = new ArrayList<>();

    JSONObject data_profit_sales = new JSONObject();
    ArrayList<Class> array_class = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);

        init_toolbar(getResources().getString(R.string.home_menu_cashier));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new CashierIndexAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(CashierIndexActivity.this,array_class.get(position)));
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @autors : liao.mei
                 * @notes : unused code, commenting code
                 */
                // <code>
                /*
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("breakdown_price",new JSONArray(array_breakdown.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(CashierIndexActivity.this, PrintOrderOverviewActivity.class)
                        .putExtra("data",jsonObject.toString()));
                 */
                // </code>
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        sales = findViewById(R.id.sales);
        profit = findViewById(R.id.profit);
        print = findViewById(R.id.print);

        prepare_data();
        adapter = new CashierIndexAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

//        sales.setText(data_profit_sales.getJSONObject("sales").getString("price_str"));
//        profit.setText(data_profit_sales.getJSONObject("profit").getString("price_str"));
    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.cashier_manage_sell_price));
        array.add(jsonObject);
        array_class.add(ManageSellPriceIndexActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.cashier_monthly_report));
        array.add(jsonObject);
        array_class.add(CashierMonthlyReportActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("name", getResources().getString(R.string.cashier_sales));
        jsonObject.put("price", 0);
        jsonObject.put("price_str", "Rp. "+formatter.format(0));
        data_profit_sales.put("sales",jsonObject);
        array_breakdown.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("name", getResources().getString(R.string.cashier_profit));
        jsonObject.put("price", 0);
        jsonObject.put("price_str", "Rp. "+formatter.format(0));
        data_profit_sales.put("profit",jsonObject);
        array_breakdown.add(jsonObject);
    }
}