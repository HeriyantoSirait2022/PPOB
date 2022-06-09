package com.qdi.rajapay.home;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.CashierAPI;
import com.qdi.rajapay.cashier.manage_sell_price.ManageSellPriceIndexActivity;
import com.qdi.rajapay.cashier.monthly_report.CashierMonthlyReportActivity;
import com.qdi.rajapay.contact_us.ContactUsListActivity;
import com.qdi.rajapay.model.CashierDetailData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CashierFragment extends Fragment implements APICallback.ItemCallback<CashierDetailData> {
    TextView sales, profit;
    Button print, contactUs;
    RecyclerView list;

    CashierAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    List<JSONObject> array = new ArrayList<>(), array_breakdown = new ArrayList<>();

    JSONObject data_profit_sales = new JSONObject();
    ArrayList<Class> array_class = new ArrayList<>();
    MainActivity parent;

    CashierAPI api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_cashier,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new CashierAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(parent,array_class.get(position)));
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, ContactUsListActivity.class));
            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);
        sales = view.findViewById(R.id.sales);
        profit = view.findViewById(R.id.profit);
        print = view.findViewById(R.id.print);
        contactUs = view.findViewById(R.id.contact_us);

        parent = (MainActivity) getActivity();

        prepare_data();
        adapter = new CashierAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        list.addItemDecoration(dividerItemDecoration);
        // list.addItemDecoration(decoration);

        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        api = new CashierAPI(parent, parent.user_SP);
        getCashierDetail();
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

        array_breakdown = getBreakdownPrice(0,0);
    }

    private void getCashierDetail() {

        api.getCashierDetail(this);
        parent.show_wait_modal();
    }

    private void setCashierDetail(CashierDetailData detailData) {
        sales.setText("Rp. " + parent.formatter.format(detailData.getSales()));
        profit.setText("Rp. " + parent.formatter.format(detailData.getProfit()));
    }

    @Override
    public void onItemResponseSuccess(CashierDetailData item, String message) {
        parent.dismiss_wait_modal();
        setCashierDetail(item);
        array_breakdown = getBreakdownPrice(item.getSales(), item.getProfit());
    }

    @Override
    public void onAPIResponseFailure(VolleyError error) {

        parent.dismiss_wait_modal();
        try {
            parent.error_handling(error, parent.layout);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
            parent.show_error_message(parent.layout, e.getLocalizedMessage());
        }
    }

    private List<JSONObject> getBreakdownPrice(int sales, int profit) {
        List<JSONObject> result = new ArrayList<>();
        try {
            JSONObject salesJsonObject = new JSONObject();
            salesJsonObject.put("name", getResources().getString(R.string.cashier_sales));
            salesJsonObject.put("price", sales);
            salesJsonObject.put("price_str", "Rp. " + parent.formatter.format(sales));
            result.add(salesJsonObject);

            JSONObject profitJsonObject = new JSONObject();
            profitJsonObject.put("name", getResources().getString(R.string.cashier_profit));
            profitJsonObject.put("price", profit);
            profitJsonObject.put("price_str", "Rp. " + parent.formatter.format(profit));
            result.add(profitJsonObject);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public class DividerItemDecorator extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public DividerItemDecorator(Drawable divider) {
            mDivider = divider;
        }

        @Override
        public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i <= childCount - 2; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(canvas);
            }
        }
    }
}
